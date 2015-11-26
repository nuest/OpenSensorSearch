/*
 * Copyright (C) 2013 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.oss.ui.beans;

import java.util.ArrayList;
import java.util.List;

import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePeriodType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.ows.x11.BoundingBoxType;
import net.opengis.swe.x101.UomPropertyType;

import org.n52.oss.sir.api.SirMatchingType;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.Tools;
import org.n52.oss.util.XmlTools;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters;
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorRequestDocument.SearchSensorRequest;
import org.x52North.sir.x032.SensorIdentificationDocument.SensorIdentification;
import org.x52North.sir.x032.ServiceCriteriaDocument.ServiceCriteria;
import org.x52North.sir.x032.ServiceReferenceDocument.ServiceReference;

/**
 * @author Jan Schulte
 * 
 */
public class SearchSensorBean extends TestClientBean {

    private String lowerCorner = "";

    private String phenomenonName = "";

    private String searchText = "";

    private String sensorIdValue = "";

    private String serviceCriteriaType = "";

    private String serviceCriteriaURL = "";

    private String serviceSpecificSensorID = "";

    private String serviceType = "";

    private String serviceURL = "";

    private boolean simpleResponse = false;

    private String sorMatchingType = "";

    private String sorSearchDepth = "";

    private String sorUrl = "";

    private String timePeriodEnd = "";

    private String timePeriodStart = "";

    private String uom = "";

    private String upperCorner = "";

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.AbstractBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        this.responseString = "";

        SearchSensorRequestDocument requestDoc = SearchSensorRequestDocument.Factory.newInstance();
        SearchSensorRequest request = requestDoc.addNewSearchSensorRequest();
        request.setService(ClientConstants.SERVICE_NAME);
        request.setVersion(ClientConstants.getServiceVersionEnum());

        SensorIdentification sensIdent = null;

        if ( !this.sensorIdValue.isEmpty() || !this.serviceURL.isEmpty()) {
            sensIdent = request.addNewSensorIdentification();

            if ( !this.sensorIdValue.isEmpty()) {
                sensIdent.setSensorIDInSIR(this.sensorIdValue);
            }

            else if ( !this.serviceURL.isEmpty() && !this.serviceType.isEmpty()
                    && !this.serviceSpecificSensorID.isEmpty()) {
                ServiceReference servRef = sensIdent.addNewServiceReference();
                servRef.setServiceURL(this.serviceURL);
                servRef.setServiceType(this.serviceType);
                servRef.setServiceSpecificSensorID(this.serviceSpecificSensorID);
            }
        }

        // if (Tools.atLeastOneIsNotEmpty(new String[] {this.serviceCriteriaType,
        // this.serviceCriteriaURL,
        // this.searchText,
        // this.phenomenonName,
        // this.uom,
        // this.upperCorner,
        // this.lowerCorner,
        // this.timePeriodEnd,
        // this.timePeriodEnd})) {
        SearchCriteria searchCriteria = request.addNewSearchCriteria();

        if ( !this.serviceCriteriaType.isEmpty() && !this.serviceCriteriaURL.isEmpty()) {
            ServiceCriteria servCriteria = searchCriteria.addNewServiceCriteria();
            if ( !this.serviceCriteriaURL.isEmpty()) {
                servCriteria.setServiceURL(this.serviceCriteriaURL);
            }
            if ( !this.serviceCriteriaType.isEmpty()) {
                servCriteria.setServiceType(this.serviceCriteriaType);
            }
        }

        if ( !this.searchText.isEmpty()) {
            searchCriteria.setSearchTextArray(this.searchText.split(";"));
        }

        if ( !this.phenomenonName.isEmpty()) {
            Phenomenon phen = searchCriteria.addNewPhenomenon();
            phen.setPhenomenonName(this.phenomenonName);

            if (Tools.noneEmpty(new String[] {this.sorMatchingType, this.sorSearchDepth, this.sorUrl})) {
                SORParameters sorParams = phen.addNewSORParameters();
                try {
                    SirMatchingType mt = SirMatchingType.getSirMatchingType(this.sorMatchingType);
                    sorParams.setMatchingType(mt.getSchemaMatchingType());
                }
                catch (OwsExceptionReport e) {
                    this.requestString = e.getDocument().xmlText();
                    return;
                }
                sorParams.setSORURL(this.sorUrl);
                sorParams.setSearchDepth(Integer.parseInt(this.sorSearchDepth));
            }

        }

        if ( !this.uom.isEmpty()) {
            String[] uomArray = this.uom.split(";");
            for (String uomCode : uomArray) {
                UomPropertyType uomPropertyType = searchCriteria.addNewUom();
                uomPropertyType.setCode(uomCode);
            }
        }

        if ( !this.lowerCorner.isEmpty() && !this.upperCorner.isEmpty()) {
            BoundingBoxType boundingBox = searchCriteria.addNewBoundingBox();
            // lower corner
            List<Double> loco = new ArrayList<>();
            loco.add(new Double(this.lowerCorner.substring(0, this.lowerCorner.indexOf(" "))));
            loco.add(new Double(this.lowerCorner.substring(this.lowerCorner.indexOf(" ") + 1, this.lowerCorner.length())));
            boundingBox.setLowerCorner(loco);
            // upper corner
            List<Double> upco = new ArrayList<>();
            upco.add(new Double(this.upperCorner.substring(0, this.upperCorner.indexOf(" "))));
            upco.add(new Double(this.upperCorner.substring(this.upperCorner.indexOf(" ") + 1, this.upperCorner.length())));
            boundingBox.setUpperCorner(upco);
        }

        if (this.timePeriodStart.isEmpty() && !this.timePeriodEnd.isEmpty()) {
            this.requestString = "Please check the start time!";
            return;
        }

        if ( !this.timePeriodStart.isEmpty() && this.timePeriodEnd.isEmpty()) {
            TimeInstantType timeInstantType = TimeInstantType.Factory.newInstance();
            TimePositionType timePosition = timeInstantType.addNewTimePosition();
            timePosition.setStringValue(this.timePeriodStart);
            searchCriteria.setTime(timeInstantType);
        }

        if ( !this.timePeriodStart.isEmpty() && !this.timePeriodEnd.isEmpty()) {
            TimePeriodType timePeriodType = TimePeriodType.Factory.newInstance();
            TimePositionType beginPosition = timePeriodType.addNewBeginPosition();
            TimePositionType endPosition = timePeriodType.addNewEndPosition();
            beginPosition.setStringValue(this.timePeriodStart);
            endPosition.setStringValue(this.timePeriodEnd);
            searchCriteria.setTime(timePeriodType);
        }

        // }

        request.setSimpleResponse(this.simpleResponse);

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if ( !requestDoc.validate(XmlTools.xmlOptionsForNamespaces()))
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        else
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());

        this.requestString = requestDoc.toString();
    }

    public String getLowerCorner() {
        return this.lowerCorner;
    }

    public SirMatchingType[] getMatchingTypes() {
        return SirMatchingType.values();
    }

    public String getPhenomenonName() {
        return this.phenomenonName;
    }

    public String getSearchText() {
        return this.searchText;
    }

    public String getSensorIdValue() {
        return this.sensorIdValue;
    }

    public String getServiceCriteriaType() {
        return this.serviceCriteriaType;
    }

    public String getServiceCriteriaURL() {
        return this.serviceCriteriaURL;
    }

    public String getServiceSpecificSensorID() {
        return this.serviceSpecificSensorID;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public String getServiceURL() {
        return this.serviceURL;
    }

    public String getSorMatchingType() {
        return this.sorMatchingType;
    }

    public String getSorSearchDepth() {
        return this.sorSearchDepth;
    }

    public String getSorUrl() {
        return this.sorUrl;
    }

    public String getTimePeriodEnd() {
        return this.timePeriodEnd;
    }

    public String getTimePeriodStart() {
        return this.timePeriodStart;
    }

    public String getUom() {
        return this.uom;
    }

    public String getUpperCorner() {
        return this.upperCorner;
    }

    public boolean isSimpleResponse() {
        return this.simpleResponse;
    }

    public void setLowerCorner(String lowerCorner) {
        this.lowerCorner = lowerCorner;
    }

    public void setPhenomenonName(String phenomenonName) {
        this.phenomenonName = phenomenonName;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setSensorIdValue(String sensorIdValue) {
        this.sensorIdValue = sensorIdValue;
    }

    public void setServiceCriteriaType(String serviceCriteriaType) {
        this.serviceCriteriaType = serviceCriteriaType;
    }

    public void setServiceCriteriaURL(String serviceCriteriaURL) {
        this.serviceCriteriaURL = serviceCriteriaURL;
    }

    public void setServiceSpecificSensorID(String serviceSpecificSensorID) {
        this.serviceSpecificSensorID = serviceSpecificSensorID;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public void setSimpleResponse(boolean simpleResponse) {
        this.simpleResponse = simpleResponse;
    }

    public void setSorMatchingType(String sorMatchingType) {
        this.sorMatchingType = sorMatchingType;
    }

    public void setSorSearchDepth(String sorSearchDepth) {
        this.sorSearchDepth = sorSearchDepth;
    }

    public void setSorUrl(String sorUrl) {
        this.sorUrl = sorUrl;
    }

    public void setTimePeriodEnd(String timePeriodEnd) {
        this.timePeriodEnd = timePeriodEnd;
    }

    public void setTimePeriodStart(String timePeriodStart) {
        this.timePeriodStart = timePeriodStart;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public void setUpperCorner(String upperCorner) {
        this.upperCorner = upperCorner;
    }

}

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

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SystemType;

import org.apache.xmlbeans.XmlException;
import org.n52.oss.util.XmlTools;
import org.x52North.sir.x032.ServiceReferenceDocument.ServiceReference;
import org.x52North.sir.x032.UpdateSensorDescriptionRequestDocument;
import org.x52North.sir.x032.UpdateSensorDescriptionRequestDocument.UpdateSensorDescriptionRequest;
import org.x52North.sir.x032.UpdateSensorDescriptionRequestDocument.UpdateSensorDescriptionRequest.SensorDescriptionToBeUpdated;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class UpdateSensorDescriptionBean extends TestClientBean {

    private String sensorDescription = "";

    private String sensorId = "";

    private String serviceSpecificSensorID = "";

    private String serviceType = "";

    private String serviceURL = "";

    @Override
    public void buildRequest() {
        UpdateSensorDescriptionRequestDocument requestDoc = UpdateSensorDescriptionRequestDocument.Factory.newInstance(XmlTools.xmlOptionsForNamespaces());
        UpdateSensorDescriptionRequest request = requestDoc.addNewUpdateSensorDescriptionRequest();
        request.setService(ClientConstants.SERVICE_NAME);
        request.setVersion(ClientConstants.getServiceVersionEnum());

        // SensorDescriptionToBeUpdated
        SensorDescriptionToBeUpdated desrcToBeUpdated = request.addNewSensorDescriptionToBeUpdated();

        if ( !this.sensorDescription.isEmpty()) {
            this.sensorDescription = this.sensorDescription.trim();

            try {
                AbstractProcessType sensorDescr = desrcToBeUpdated.addNewSensorDescription();
                SystemType system = (SystemType) sensorDescr.changeType(SystemType.type);
                SensorMLDocument doc = SensorMLDocument.Factory.parse(this.sensorDescription);
                Member member = doc.getSensorML().getMemberArray(0);
                system.set(member.getProcess());
            }
            catch (XmlException e) {
                this.requestString = "Please check the sensor description, it must be a sml:SensorML document.";
                this.requestString += "\n\n" + e.getMessage();
                return;
            }
        }

        // sensor identification
        if ( !this.sensorId.isEmpty()) {
            // by id
            desrcToBeUpdated.addNewSensorIdentification().setSensorIDInSIR(this.sensorId);
        }
        else {
            // by service reference
            // if (Tools.noneEmpty(new String[] {this.serviceType, this.serviceType,
            // this.serviceSpecificSensorID})) {
            ServiceReference serviceRef = desrcToBeUpdated.addNewSensorIdentification().addNewServiceReference();
            // serviceURL
            serviceRef.setServiceURL(this.serviceURL);
            // serviceType
            serviceRef.setServiceType(this.serviceType);
            // serviceSpecificSensorID
            serviceRef.setServiceSpecificSensorID(this.serviceSpecificSensorID);
            // }
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if ( !requestDoc.validate(XmlTools.xmlOptionsForNamespaces()))
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        else
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
    }

    public String getSensorDescription() {
        return this.sensorDescription;
    }

    public String getSensorId() {
        return this.sensorId;
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

    public void setSensorDescription(String sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setServiceSpecificSensorID(String serviceInfosServiceSpecificSensorID) {
        this.serviceSpecificSensorID = serviceInfosServiceSpecificSensorID;
    }

    /**
     * @param serviceType
     *        the serviceType to set
     */
    public void setServiceType(String serviceInfosServiceType) {
        this.serviceType = serviceInfosServiceType;
    }

    /**
     * @param serviceURL
     *        the serviceURL to set
     */
    public void setServiceURL(String serviceInfosServiceURL) {
        this.serviceURL = serviceInfosServiceURL;
    }

}

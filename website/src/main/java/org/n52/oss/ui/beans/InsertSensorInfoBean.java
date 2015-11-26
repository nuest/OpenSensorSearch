/**
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
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument.InsertSensorInfoRequest;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument.InsertSensorInfoRequest.InfoToBeInserted;
import org.x52North.sir.x032.ServiceReferenceDocument.ServiceReference;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class InsertSensorInfoBean extends TestClientBean {

    private String addRefSensorID = "";

    private String addRefType = "";

    private String addRefURL = "";

    private String sensorDescription = "";

    private String sensorId = "";

    private String serviceInfosServiceSpecificSensorID = "";

    private String serviceInfosServiceType = "";

    private String serviceInfosServiceURL = "";

    @Override
    public void buildRequest() {
        InsertSensorInfoRequestDocument requestDoc = InsertSensorInfoRequestDocument.Factory.newInstance(XmlTools.xmlOptionsForNamespaces());
        InsertSensorInfoRequest request = requestDoc.addNewInsertSensorInfoRequest();
        request.setService(ClientConstants.SERVICE_NAME);
        request.setVersion(ClientConstants.getServiceVersionEnum());

        // InfoToBeInserted
        InfoToBeInserted infoToBeInserted = request.addNewInfoToBeInserted();

        // SensorDescription with optional service reference
        if ( !this.sensorDescription.isEmpty()) {
            this.sensorDescription = this.sensorDescription.trim();

            try {
                AbstractProcessType sensorDescr = infoToBeInserted.addNewSensorDescription();
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

            // ServiceReference
            if ( !this.serviceInfosServiceType.isEmpty() && !this.serviceInfosServiceType.isEmpty()
                    && !this.serviceInfosServiceSpecificSensorID.isEmpty()) {
                ServiceReference serviceRef = infoToBeInserted.addNewServiceReference();
                // serviceURL
                serviceRef.setServiceURL(this.serviceInfosServiceURL);
                // serviceType
                serviceRef.setServiceType(this.serviceInfosServiceType);
                // serviceSpecificSensorID
                serviceRef.setServiceSpecificSensorID(this.serviceInfosServiceSpecificSensorID);
            }
        }

        // sensor id and service reference
        else if ( !this.sensorId.isEmpty()) {
            infoToBeInserted.setSensorIDInSIR(this.sensorId);

            if ( !this.addRefURL.isEmpty() && !this.addRefType.isEmpty() && !this.addRefSensorID.isEmpty()) {
                ServiceReference serviceRef = infoToBeInserted.addNewServiceReference();
                // serviceURL
                serviceRef.setServiceURL(this.addRefURL);
                // serviceType
                serviceRef.setServiceType(this.addRefType);
                // serviceSpecificSensorID
                serviceRef.setServiceSpecificSensorID(this.addRefSensorID);
            }
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if ( !requestDoc.validate(XmlTools.xmlOptionsForNamespaces()))
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        else
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
    }

    public String getAddRefSensorID() {
        return this.addRefSensorID;
    }

    public String getAddRefType() {
        return this.addRefType;
    }

    public String getAddRefURL() {
        return this.addRefURL;
    }

    public String getSensorDescription() {
        return this.sensorDescription;
    }

    public String getSensorId() {
        return this.sensorId;
    }

    public String getServiceInfosServiceSpecificSensorID() {
        return this.serviceInfosServiceSpecificSensorID;
    }

    public String getServiceInfosServiceType() {
        return this.serviceInfosServiceType;
    }

    public String getServiceInfosServiceURL() {
        return this.serviceInfosServiceURL;
    }

    public void setAddRefSensorID(String addRefSensorID) {
        this.addRefSensorID = addRefSensorID;
    }

    public void setAddRefType(String addRefType) {
        this.addRefType = addRefType;
    }

    public void setAddRefURL(String addRefURL) {
        this.addRefURL = addRefURL;
    }

    public void setSensorDescription(String sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    public void setSensorId(String id) {
        this.sensorId = id;
    }

    public void setServiceInfosServiceSpecificSensorID(String serviceInfosServiceSpecificSensorID) {
        this.serviceInfosServiceSpecificSensorID = serviceInfosServiceSpecificSensorID;
    }

    public void setServiceInfosServiceType(String serviceInfosServiceType) {
        this.serviceInfosServiceType = serviceInfosServiceType;
    }

    public void setServiceInfosServiceURL(String serviceInfosServiceURL) {
        this.serviceInfosServiceURL = serviceInfosServiceURL;
    }

}

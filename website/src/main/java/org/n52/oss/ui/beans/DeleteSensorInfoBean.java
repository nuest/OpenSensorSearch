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

import org.n52.oss.util.XmlTools;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest.InfoToBeDeleted;
import org.x52North.sir.x032.SensorIdentificationDocument.SensorIdentification;
import org.x52North.sir.x032.ServiceReferenceDocument.ServiceReference;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class DeleteSensorInfoBean extends TestClientBean {

    private String deleteRefSensorID = "";

    private String deleteRefType = "";

    private String deleteRefURL = "";

    private boolean deleteSensor = false;

    private String sensorId = "";

    private String serviceSpecificSensorID = "";

    private String serviceType = "";

    private String serviceURL = "";

    @Override
    public void buildRequest() {
        DeleteSensorInfoRequestDocument requestDoc = DeleteSensorInfoRequestDocument.Factory.newInstance(XmlTools.xmlOptionsForNamespaces());
        DeleteSensorInfoRequest request = requestDoc.addNewDeleteSensorInfoRequest();
        request.setService(ClientConstants.SERVICE_NAME);
        request.setVersion(ClientConstants.getServiceVersionEnum());

        // InfoToBeInserted
        InfoToBeDeleted infoToBeDeleted = request.addNewInfoToBeDeleted();
        SensorIdentification sensorIdentification = infoToBeDeleted.addNewSensorIdentification();

        if ( !this.sensorId.isEmpty()) {
            sensorIdentification.setSensorIDInSIR(this.sensorId);
        }

        // ServiceReference
        else if ( !this.serviceType.isEmpty() && !this.serviceType.isEmpty() && !this.serviceSpecificSensorID.isEmpty()) {
            ServiceReference serviceRef = sensorIdentification.addNewServiceReference();
            serviceRef.setServiceURL(this.serviceURL);
            serviceRef.setServiceType(this.serviceType);
            serviceRef.setServiceSpecificSensorID(this.serviceSpecificSensorID);
        }

        else {
            this.requestString = "Some kind of sensor identification is required!";
        }

        // delete sensor
        if (this.deleteSensor) {
            infoToBeDeleted.setDeleteSensor(this.deleteSensor);
        }
        // delete service reference
        else if ( !this.sensorId.isEmpty()) {
            if ( !this.deleteRefURL.isEmpty() && !this.deleteRefType.isEmpty() && !this.deleteRefSensorID.isEmpty()) {
                ServiceReference serviceRef = infoToBeDeleted.addNewServiceInfo().addNewServiceReference();
                serviceRef.setServiceURL(this.deleteRefURL);
                serviceRef.setServiceType(this.deleteRefType);
                serviceRef.setServiceSpecificSensorID(this.deleteRefSensorID);
            }
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if ( !requestDoc.validate(XmlTools.xmlOptionsForNamespaces()))
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
        else
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
    }

    public String getDeleteRefSensorID() {
        return this.deleteRefSensorID;
    }

    public String getDeleteRefType() {
        return this.deleteRefType;
    }

    public String getDeleteRefURL() {
        return this.deleteRefURL;
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

    public boolean isDeleteSensor() {
        return this.deleteSensor;
    }

    public void setDeleteRefSensorID(String deleteRefSensorID) {
        this.deleteRefSensorID = deleteRefSensorID;
    }

    public void setDeleteRefType(String deleteRefType) {
        this.deleteRefType = deleteRefType;
    }

    public void setDeleteRefURL(String deleteRefURL) {
        this.deleteRefURL = deleteRefURL;
    }

    public void setDeleteSensor(boolean deleteSensor) {
        this.deleteSensor = deleteSensor;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setServiceSpecificSensorID(String serviceInfosServiceSpecificSensorID) {
        this.serviceSpecificSensorID = serviceInfosServiceSpecificSensorID;
    }

    public void setServiceType(String serviceInfosServiceType) {
        this.serviceType = serviceInfosServiceType;
    }

    public void setServiceURL(String serviceInfosServiceURL) {
        this.serviceURL = serviceInfosServiceURL;
    }

}

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

import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.swe.x101.UomPropertyType;

import org.n52.oss.util.XmlTools;
import org.x52North.sir.x032.InsertSensorStatusRequestDocument;
import org.x52North.sir.x032.InsertSensorStatusRequestDocument.InsertSensorStatusRequest;
import org.x52North.sir.x032.StatusDescriptionDocument.StatusDescription;
import org.x52North.sir.x032.StatusDocument.Status;

/**
 * @author Jan Schulte
 * 
 */
public class InsertSensorStatusBean extends TestClientBean {

    private static final String TIME_STAMP_ID = "status_time_stamp";

    private String propertyName = "";

    private String propertyValue = "";

    private String sensorIdValue = "";

    private String timestamp = "";

    private String uom = "";

    @Override
    public void buildRequest() {
        this.responseString = "";

        InsertSensorStatusRequestDocument requestDoc = InsertSensorStatusRequestDocument.Factory.newInstance();
        InsertSensorStatusRequest request = requestDoc.addNewInsertSensorStatusRequest();
        request.setService(ClientConstants.SERVICE_NAME);
        request.setVersion(ClientConstants.getServiceVersionEnum());

        StatusDescription statusDesc = request.addNewStatusDescription();

        if ( !this.sensorIdValue.isEmpty()) {
            statusDesc.setSensorIDInSIR(this.sensorIdValue);
        }

        // status
        Status status = statusDesc.addNewStatus();
        status.setPropertyValue(this.propertyValue);
        status.setPropertyName(this.propertyName);

        // uom
        if ( !this.uom.isEmpty()) {
            UomPropertyType propertyType = UomPropertyType.Factory.newInstance();
            propertyType.setCode(this.uom);
            status.setUom(propertyType);
        }

        // timestamp
        if ( !this.timestamp.isEmpty()) {
            TimeInstantType timeInstantType = TimeInstantType.Factory.newInstance();
            timeInstantType.setId(TIME_STAMP_ID);
            TimePositionType timePosition = timeInstantType.addNewTimePosition();
            timePosition.setStringValue(this.timestamp);
            status.setTimestamp(timeInstantType);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if (requestDoc.validate())
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
        else
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getPropertyValue() {
        return this.propertyValue;
    }

    public String getSensorIdValue() {
        return this.sensorIdValue;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public String getUom() {
        return this.uom;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void setSensorIdValue(String sensorIdValue) {
        this.sensorIdValue = sensorIdValue;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

}

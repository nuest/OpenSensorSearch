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
package org.n52.sir.response;

import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.InsertSensorStatusResponseDocument;
import org.x52North.sir.x032.InsertSensorStatusResponseDocument.InsertSensorStatusResponse;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirInsertSensorStatusResponse extends AbstractXmlResponse {

    private static final Logger log = LoggerFactory.getLogger(SirInsertSensorStatusResponse.class);

    private String sensorId;

    private boolean statusUpdateSuccessful;

    @Override
    public InsertSensorStatusResponseDocument createXml() {
        InsertSensorStatusResponseDocument document = InsertSensorStatusResponseDocument.Factory.newInstance();
        InsertSensorStatusResponse insSensStatResp = document.addNewInsertSensorStatusResponse();

        insSensStatResp.setSensorIDInSIR(this.sensorId);

        XmlTools.addSirAndSensorMLSchemaLocation(insSensStatResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    public String getSensorId() {
        return this.sensorId;
    }

    public boolean isStatusUpdateSuccessful() {
        return this.statusUpdateSuccessful;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public void setStatusUpdateSuccessful(boolean statusUpdateSuccessful) {
        this.statusUpdateSuccessful = statusUpdateSuccessful;
    }

}

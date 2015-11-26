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
package org.n52.sir.response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument.InsertSensorInfoResponse;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument.InsertSensorInfoResponse.InsertedSensors;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirInsertSensorInfoResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirInsertSensorInfoResponse.class);

    private Collection<String> insertedSensors = new ArrayList<String>();

    private int numberOfInsertedSensors = 0;

    private int numberOfInsertedServiceReferences = 0;

    @Override
    public InsertSensorInfoResponseDocument createXml() {
        InsertSensorInfoResponseDocument document = InsertSensorInfoResponseDocument.Factory.newInstance();
        InsertSensorInfoResponse insSensInfoResp = document.addNewInsertSensorInfoResponse();

        insSensInfoResp.setNumberOfInsertedSensors(this.numberOfInsertedSensors);
        insSensInfoResp.setNumberOfInsertedServiceReferences(this.numberOfInsertedServiceReferences);
        InsertedSensors insertedSensor = insSensInfoResp.addNewInsertedSensors();
        for (String inSens : this.insertedSensors) {
            insertedSensor.addSensorIDInSIR(inSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(insSensInfoResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the insertedSensors
     */
    public Collection<String> getInsertedSensors() {
        return this.insertedSensors;
    }

    /**
     * @return the numberOfNewSensors
     */
    public int getNumberOfNewSensors() {
        return this.numberOfInsertedSensors;
    }

    /**
     * @return the numberOfNewServiceReferences
     */
    public int getNumberOfNewServiceReferences() {
        return this.numberOfInsertedServiceReferences;
    }

    /**
     * @param insertedSensors
     *        the insertedSensors to set
     */
    public void setInsertedSensors(Collection<String> insertedSensors) {
        this.insertedSensors = insertedSensors;
    }

    /**
     * @param numberOfNewSensors
     *        the numberOfNewSensors to set
     */
    public void setNumberOfNewSensors(int numberOfNewSensors) {
        this.numberOfInsertedSensors = numberOfNewSensors;
    }

    /**
     * @param numberOfNewServiceReferences
     *        the numberOfNewServiceReferences to set
     */
    public void setNumberOfNewServiceReferences(int numberOfNewServiceReferences) {
        this.numberOfInsertedServiceReferences = numberOfNewServiceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirInsertSensorInfoResponse: ");
        sb.append(" Inserted sensors: " + this.numberOfInsertedSensors);
        sb.append(" New service references: " + this.numberOfInsertedServiceReferences);
        sb.append(" Inserted Sensors: " + Arrays.toString(this.insertedSensors.toArray()));
        return sb.toString();
    }

}

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
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument.DeleteSensorInfoResponse;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument.DeleteSensorInfoResponse.DeletedSensors;

/**
 * @author Daniel Nüst
 * 
 */
public class SirDeleteSensorInfoResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirDeleteSensorInfoResponse.class);

    private Collection<String> deletedSensors = new ArrayList<String>();

    private int numberOfDeletedSensors = 0;

    private int numberOfDeletedServiceReferences = 0;

    @Override
    public DeleteSensorInfoResponseDocument createXml() {
        DeleteSensorInfoResponseDocument document = DeleteSensorInfoResponseDocument.Factory.newInstance();
        DeleteSensorInfoResponse delteSensInfoResp = document.addNewDeleteSensorInfoResponse();

        delteSensInfoResp.setNumberOfDeletedSensors(this.numberOfDeletedSensors);
        delteSensInfoResp.setNumberOfDeletedServiceReferences(this.numberOfDeletedServiceReferences);
        DeletedSensors deletedSensor = delteSensInfoResp.addNewDeletedSensors();
        for (String inSens : this.deletedSensors) {
            deletedSensor.addSensorIDInSIR(inSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(delteSensInfoResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the deletedSensors
     */
    public Collection<String> getDeletedSensors() {
        return this.deletedSensors;
    }

    /**
     * @return the numberOfNewSensors
     */
    public int getNumberOfDeletedSensors() {
        return this.numberOfDeletedSensors;
    }

    /**
     * @return the numberOfNewServiceReferences
     */
    public int getNumberOfDeletedServiceReferences() {
        return this.numberOfDeletedServiceReferences;
    }

    /**
     * @param deletedSensors
     *        the deletedSensors to set
     */
    public void setDeletedSensors(Collection<String> insertedSensors) {
        this.deletedSensors = insertedSensors;
    }

    /**
     * @param numberOfDeletedSensors
     *        the numberOfDeletedSensors to set
     */
    public void setNumberOfDeletedSensors(int numberOfDeletedSensors) {
        this.numberOfDeletedSensors = numberOfDeletedSensors;
    }

    /**
     * @param numberOfDeletedServiceReferences
     *        the numberOfDeletedServiceReferences to set
     */
    public void setNumberOfDeletedServiceReferences(int numberOfDeletedServiceReferences) {
        this.numberOfDeletedServiceReferences = numberOfDeletedServiceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirDeleteSensorInfoResponse: ");
        sb.append("\n# of deleted sensors: " + this.numberOfDeletedSensors);
        sb.append("\nDeleted service references: " + this.numberOfDeletedServiceReferences);
        sb.append("\nDeleted Sensors: " + Arrays.toString(this.deletedSensors.toArray()));
        return sb.toString();
    }

}

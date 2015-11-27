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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.HarvestServiceResponseDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse.DeletedSensor;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse.FailedSensor;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse.InsertedSensor;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse.UpdatedSensor;

/**
 * Internal response to a harvest service request
 *
 * @author Jan Schulte
 *
 */
public class SirHarvestServiceResponse extends AbstractXmlResponse {

    private static final Logger log = LoggerFactory.getLogger(SirHarvestServiceResponse.class);

    private Collection<SirSensor> deletedSensors;

    private Collection<String> failedSensors;

    private Map<String, String> failureDescriptions = new HashMap<>();

    private Collection<SirSensor> insertedSensors;

    private int numberOfDeletedSensors;

    private int numberOfFailedSensors;

    private int numberOfFoundSensors;

    private int numberOfInsertedSensors;

    private int numberOfUpdatedSensors;

    private String serviceType;

    private String serviceUrl;

    private Collection<SirSensor> updatedSensors;

    private boolean validateReponse;

    /*
     * TODO make injectable so that validate variable is injected here!
     */
    public SirHarvestServiceResponse(boolean validate) {
        this.validateReponse = validate;
    }

    public void addFailureDescription(String sensor, String description) {
        this.failureDescriptions.put(sensor, description);
    }

    @Override
    public HarvestServiceResponseDocument createXml() {
        HarvestServiceResponseDocument document = HarvestServiceResponseDocument.Factory.newInstance();
        HarvestServiceResponse harvServResp = document.addNewHarvestServiceResponse();

        harvServResp.setServiceURL(this.serviceUrl);
        harvServResp.setServiceType(this.serviceType);
        harvServResp.setNumberOfFoundSensors(this.numberOfFoundSensors);
        harvServResp.setNumberOfInsertedSensors(this.numberOfInsertedSensors);
        harvServResp.setNumberOfDeletedSensors(this.numberOfDeletedSensors);
        harvServResp.setNumberOfUpdatedSensors(this.numberOfUpdatedSensors);
        harvServResp.setNumberOfFailedSensors(this.numberOfFailedSensors);

        for (SirSensor inSens : this.insertedSensors) {
            InsertedSensor insertedSensor = harvServResp.addNewInsertedSensor();
            insertedSensor.setSensorIDInSIR(inSens.getInternalSensorID());
            insertedSensor.setServiceSpecificSensorID(inSens.getServDescs().iterator().next().getServiceSpecificSensorId());
        }
        for (SirSensor delSens : this.deletedSensors) {
            DeletedSensor deletedSensor = harvServResp.addNewDeletedSensor();
            deletedSensor.setSensorIDInSIR(delSens.getInternalSensorID());
            deletedSensor.setServiceSpecificSensorID(delSens.getServDescs().iterator().next().getServiceSpecificSensorId());
        }
        for (SirSensor upSens : this.updatedSensors) {
            UpdatedSensor updatedSensor = harvServResp.addNewUpdatedSensor();
            updatedSensor.setSensorIDInSIR(upSens.getInternalSensorID());
            updatedSensor.setServiceSpecificSensorID(upSens.getServDescs().iterator().next().getServiceSpecificSensorId());
        }
        for (String failSens : this.failedSensors) {
            String failureDescr = "NOT_AVAILABLE";
            if (this.failureDescriptions.get(failSens) != null) {
                failureDescr = this.failureDescriptions.get(failSens);
            }

            FailedSensor failedSensor = harvServResp.addNewFailedSensor();
            failedSensor.setFailureDescription(failureDescr);
            failedSensor.setServiceSpecificSensorID(failSens);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(harvServResp);

        if (this.validateReponse) {
            if (!document.validate()) {
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
            }
        }

        return document;
    }

    public Collection<SirSensor> getDeletedSensors() {
        return this.deletedSensors;
    }

    public Collection<String> getFailedSensors() {
        return this.failedSensors;
    }

    public Map<String, String> getFailureDescriptions() {
        return this.failureDescriptions;
    }

    public Collection<SirSensor> getInsertedSensors() {
        return this.insertedSensors;
    }

    public int getNumberOfDeletedSensors() {
        return this.numberOfDeletedSensors;
    }

    public int getNumberOfFailedSensors() {
        return this.numberOfFailedSensors;
    }

    public int getNumberOfFoundSensors() {
        return this.numberOfFoundSensors;
    }

    public int getNumberOfInsertedSensors() {
        return this.numberOfInsertedSensors;
    }

    public int getNumberOfUpdatedSensors() {
        return this.numberOfUpdatedSensors;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

    public Collection<SirSensor> getUpdatedSensors() {
        return this.updatedSensors;
    }

    public void setDeletedSensors(Collection<SirSensor> deletedSensors) {
        this.deletedSensors = deletedSensors;
    }

    public void setFailedSensors(Collection<String> failedSensors) {
        this.failedSensors = failedSensors;
    }

    public void setInsertedSensors(Collection<SirSensor> insertedSensors) {
        this.insertedSensors = insertedSensors;
    }

    public void setNumberOfDeletedSensors(int numberOfDeletedSensors) {
        this.numberOfDeletedSensors = numberOfDeletedSensors;
    }

    public void setNumberOfFailedSensors(int numberOfFailedSensors) {
        this.numberOfFailedSensors = numberOfFailedSensors;
    }

    public void setNumberOfFoundSensors(int numberOfFoundSensors) {
        this.numberOfFoundSensors = numberOfFoundSensors;
    }

    public void setNumberOfInsertedSensors(int numberOfInsertedSensors) {
        this.numberOfInsertedSensors = numberOfInsertedSensors;
    }

    public void setNumberOfUpdatedSensors(int numberOfUpdatedSensors) {
        this.numberOfUpdatedSensors = numberOfUpdatedSensors;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public void setUpdatedSensors(Collection<SirSensor> updatedSensors) {
        this.updatedSensors = updatedSensors;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirHarvestServiceResponse: ");
        sb.append("ServiceType: ").append(this.serviceType);
        sb.append(", ServiceURL: ").append(this.serviceUrl);
        sb.append(", Number Found Sensors: ").append(this.numberOfFoundSensors);
        sb.append(", Number Inserted Sensors: ").append(this.numberOfInsertedSensors);
        sb.append(", Number Deleted Sensors: ").append(this.numberOfDeletedSensors);
        sb.append(", Number Updated Sensors: ").append(this.numberOfUpdatedSensors);
        sb.append(", Number Failed Sensors: ").append(this.numberOfFailedSensors);
        sb.append(", Inserted Sensors: ").append(this.insertedSensors);
        sb.append(", Deleted Sensors: ").append(this.deletedSensors);
        sb.append(", Updated Sensors: ").append(this.updatedSensors);
        sb.append(", Failed Sensors: ").append(this.failedSensors);
        return sb.toString();
    }

}

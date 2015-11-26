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
package org.n52.sir.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.api.SirDescriptionToBeUpdated;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirUpdateSensorDescriptionRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirUpdateSensorDescriptionResponse;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.ValidationResult;
import org.n52.sir.xml.ValidatorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class UpdateSensorDescriptionListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(UpdateSensorDescriptionListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.UpdateSensorDescription.name();

    private IInsertSensorInfoDAO insertSensorInfoDAO;

    private IProfileValidator validator;

    @Inject
    public UpdateSensorDescriptionListener(IInsertSensorInfoDAO dao, Set<IProfileValidator> validators) {
        this.insertSensorInfoDAO = dao;
        this.validator = ValidatorModule.getFirstMatchFor(validators,
                                                          IProfileValidator.ValidatableFormatAndProfile.SML_DISCOVERY);

        log.info("NEW {}", this);
    }

    @Override
    public String getOperationName() {
        return UpdateSensorDescriptionListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {

        SirUpdateSensorDescriptionRequest sirRequest = (SirUpdateSensorDescriptionRequest) request;
        SirUpdateSensorDescriptionResponse response = new SirUpdateSensorDescriptionResponse();

        ArrayList<String> updatedSensors = new ArrayList<>();

        SensorMLDecoder decoder = new SensorMLDecoder();

        try {
            for (SirDescriptionToBeUpdated descrToBeUpdated : sirRequest.getDescriptionToBeUpdated()) {

                SirSensorIdentification sensorIdent = descrToBeUpdated.getSensorIdentification();
                XmlObject sensorDescription = descrToBeUpdated.getSensorDescription();

                SirSensor sensor = decoder.decode(sensorIdent, sensorDescription);
                sensor.setLastUpdate(new Date());

                // UPDATE
                updateSensor(response, updatedSensors, sensorIdent, sensor);
            }
        }
        catch (OwsExceptionReport | IOException e) {
            return new ExceptionResponse(e);
        }

        response.setUpdatedSensors(updatedSensors);

        return response;
    }

    private void updateSensor(SirUpdateSensorDescriptionResponse response,
                              ArrayList<String> updatedSensors,
                              SirSensorIdentification sensorIdent,
                              SirSensor sensor) throws OwsExceptionReport, IOException {
        // check SensorML for conformity with profile
        ValidationResult validationResult = this.validator.validate(sensor.getSensorMLDocument());
        boolean isValid = validationResult.isValidated();
        if ( !isValid) {
            String errMsg = "Sensor metadata document of sensor " + sensorIdent
                    + "is not conform with the required profile and cannot be updated!";
            log.error(errMsg);

            throw new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                                         "SensorDescription",
                                         "The given sensor description is not conform to the required profile of this service: "
                                                 + String.valueOf(validationResult.getValidationFailuresAsString()));
        }

        String sensorId = this.insertSensorInfoDAO.updateSensor(sensorIdent, sensor);
        if (sensorId != null) {
            updatedSensors.add(sensorId);
            response.setNumberOfUpdatedSensorDescriptions(response.getNumberOfUpdatedSensorDescriptions() + 1);

            log.debug("Updated sensor description for sensor {}", sensorId);
        }
    }
}

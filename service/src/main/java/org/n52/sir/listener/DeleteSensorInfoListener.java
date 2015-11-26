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

import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.api.SirInfoToBeDeleted;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDeleteSensorInfoRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirDeleteSensorInfoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Daniel Nüst
 * 
 */
public class DeleteSensorInfoListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(DeleteSensorInfoListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.DeleteSensorInfo.name();

    private IInsertSensorInfoDAO sensorInfoDao;

    @Inject
    public DeleteSensorInfoListener(IInsertSensorInfoDAO dao) {
        this.sensorInfoDao = dao;

        log.debug("NEW {}", this);
    }

    private void deleteSensor(SirDeleteSensorInfoResponse response,
                              SirSensorIdentification sensorIdent,
                              ArrayList<String> deletedSensors) throws OwsExceptionReport {
        if (sensorIdent != null) {
            String sID = this.sensorInfoDao.deleteSensor(sensorIdent);

            if (sID != null) {
                deletedSensors.add(sID);
                response.setNumberOfDeletedSensors(response.getNumberOfDeletedSensors() + 1);
                log.debug("Deleted Sensor: {}", sID);
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 "DeleteSensorInfoListener.receiveRequest()",
                                 "Missing parameter: To delete a sensor, a sensorIdentification element is required!");
            throw se;
        }
    }

    /**
     * 
     * @param response
     * @param serviceReference
     * @param sensIdent
     * @throws OwsExceptionReport
     */
    private void deleteServiceReference(SirDeleteSensorInfoResponse response,
                                        SirServiceReference serviceReference,
                                        SirSensorIdentification sensIdent) throws OwsExceptionReport {
        if (serviceReference != null) {
            String sID = this.sensorInfoDao.deleteReference(sensIdent, serviceReference);

            if (sID != null) {
                response.setNumberOfDeletedServiceReferences(response.getNumberOfDeletedServiceReferences() + 1);
                log.debug("Deleted ServiceReference for sensor {}" + sID);
            }
        }
        else {
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.MissingParameterValue,
                                 "DeleteSensorInfoListener.receiveRequest()",
                                 "Missing parameter: To delete a service reference, a ServiceReference element is required!");
            throw se;
        }
    }

    @Override
    public String getOperationName() {
        return DeleteSensorInfoListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        log.debug("** request: {}", request);

        SirDeleteSensorInfoRequest sirRequest = (SirDeleteSensorInfoRequest) request;
        SirDeleteSensorInfoResponse response = new SirDeleteSensorInfoResponse();

        ArrayList<String> deletedSensors = new ArrayList<>();

        try {
            for (SirInfoToBeDeleted intoToBeDeleted : sirRequest.getInfoToBeDeleted()) {
                SirSensorIdentification sensorIdent = intoToBeDeleted.getSensorIdentification();

                if (intoToBeDeleted.isDeleteSensor()) {
                    deleteSensor(response, sensorIdent, deletedSensors);
                }
                else if (intoToBeDeleted.getServiceInfo() != null) {
                    Collection<SirServiceReference> serviceReferences = intoToBeDeleted.getServiceInfo().getServiceReferences();

                    for (SirServiceReference serviceReference : serviceReferences) {
                        deleteServiceReference(response, serviceReference, sensorIdent);
                    }
                }
            }
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e);
        }

        response.setDeletedSensors(deletedSensors);

        log.debug("** response: {}", response);
        return response;
    }

}

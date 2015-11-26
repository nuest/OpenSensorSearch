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
package org.n52.sir.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.api.InternalSensorID;
import org.n52.oss.sir.api.SirSearchCriteria_Phenomenon;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.api.SirStatusDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IGetSensorStatusDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirGetSensorStatusRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirGetSensorStatusResponse;
import org.n52.sir.util.SORTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 *
 */
public class GetSensorStatusListener implements ISirRequestListener {

    private static final Logger log = LoggerFactory.getLogger(GetSensorStatusListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.GetSensorStatus.name();

    private IGetSensorStatusDAO getSensStatDao;

    @Inject
    public GetSensorStatusListener(IGetSensorStatusDAO dao) {
        this.getSensStatDao = dao;

        log.info("NEW {}", this);
    }

    @Override
    public String getOperationName() {
        return GetSensorStatusListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirGetSensorStatusRequest getSensStatReq = (SirGetSensorStatusRequest) request;
        SirGetSensorStatusResponse response = new SirGetSensorStatusResponse();
        ArrayList<SirStatusDescription> statDescs = new ArrayList<>();

        if (getSensStatReq.getSensIdent() != null) {
            // search by sensorIdentification
            for (SirSensorIdentification sensIdent : getSensStatReq.getSensIdent()) {
                if (sensIdent instanceof InternalSensorID) {
                    // sensorID in SIR
                    InternalSensorID sensorId = (InternalSensorID) sensIdent;
                    try {
                        statDescs = (ArrayList<SirStatusDescription>) this.getSensStatDao.getSensorStatusBySensorID(sensorId,
                                                                                                                    getSensStatReq.getPropertyFilter());
                    }
                    catch (OwsExceptionReport e) {
                        return new ExceptionResponse(e);
                    }
                }
                else {
                    // service description
                    SirServiceReference servDesc = (SirServiceReference) sensIdent;
                    try {
                        statDescs = (ArrayList<SirStatusDescription>) this.getSensStatDao.getSensorStatusByServiceDescription(servDesc,
                                                                                                                              getSensStatReq.getPropertyFilter());
                    }
                    catch (OwsExceptionReport e) {
                        return new ExceptionResponse(e);
                    }
                }
            }
        }
        else {
            // search by Criteria
            try {
                // utilize SOR if information is given
                if (getSensStatReq.getSearchCriteria().isUsingSOR()) {
                    // request the information from SOR and extend the search criteria with the result
                    Collection<SirSearchCriteria_Phenomenon> phenomena = getSensStatReq.getSearchCriteria().getPhenomena();

                    SORTools sor = new SORTools();
                    Collection<SirSearchCriteria_Phenomenon> newPhenomena = sor.getMatchingPhenomena(phenomena);

                    // add all found phenomena to search criteria
                    log.debug("Adding phenomena to search criteria: {}", Arrays.toString(newPhenomena.toArray()));
                    phenomena.addAll(newPhenomena);
                }

                statDescs = (ArrayList<SirStatusDescription>) this.getSensStatDao.getSensorStatusBySearchCriteria(getSensStatReq.getSearchCriteria(),
                                                                                                                  getSensStatReq.getPropertyFilter());
            }
            catch (OwsExceptionReport e) {
                return new ExceptionResponse(e);
            }
        }
        response.setStatusDescs(statDescs);
        return response;
    }

}

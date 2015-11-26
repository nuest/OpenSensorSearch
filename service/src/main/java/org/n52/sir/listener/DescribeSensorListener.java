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

import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IDescribeSensorDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDescribeSensorRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirDescribeSensorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Jan Schulte
 * 
 */
public class DescribeSensorListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(DescribeSensorListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.DescribeSensor.name();

    private IDescribeSensorDAO descSensDao;

    private boolean validateResponses;

    @Inject
    public DescribeSensorListener(IDescribeSensorDAO dao, @Named("oss.sir.responses.validate")
    boolean validateResponses) {
        this.descSensDao = dao;
        this.validateResponses = validateResponses;
    }

    @Override
    public String getOperationName() {
        return DescribeSensorListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirDescribeSensorRequest descSensReq = (SirDescribeSensorRequest) request;

        SirDescribeSensorResponse response = new SirDescribeSensorResponse(this.validateResponses);

        try {
            XmlObject sensorML = this.descSensDao.getSensorDescription(descSensReq.getSensorId());
            if (sensorML != null) {
                response.setSensorML(sensorML);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidRequest, null, "Unknown sensor ID in Sir! Given ID: "
                        + descSensReq.getSensorId());
                log.debug("Unknown sensor ID in Sir! Given ID: " + descSensReq.getSensorId());
                throw se;
            }
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }
        return response;
    }
}
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
package org.n52.sir.decode.impl;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.decode.IHttpGetRequestDecoder;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDescribeSensorRequest;
import org.n52.sir.request.SirGetCapabilitiesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 *
 */
public class HttpGetRequestDecoder implements IHttpGetRequestDecoder {

    private static final Logger log = LoggerFactory.getLogger(HttpGetRequestDecoder.class);

    protected HttpGetRequestDecoder() {
        log.info("NEW {}", this);
    }

    private AbstractSirRequest decodeDescribeSensor(String[] params) throws OwsExceptionReport {
        try {
            SirDescribeSensorRequest request = new SirDescribeSensorRequest();
            for (String param : params) {
                String[] nameAndValue = param.split("=");
                if (nameAndValue[0].equalsIgnoreCase(SirConstants.GetDescSensorParams.SENSORIDINSIR.name())) {
                    request = new SirDescribeSensorRequest();
                    request.setSensorId(nameAndValue[1]);
                    return request;
                }
            }
            return request;
        }
        catch (Exception e) {
            log.error("Invalid DescribeSensor request!");
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 "HttpGetRequestDecoder.receiveRequest()",
                                 "Invalid DescribeSensor request!");
            throw se;
        }
    }

    private AbstractSirRequest decodeGetCapabilities(String[] params) throws OwsExceptionReport {

        SirGetCapabilitiesRequest sirRequest = new SirGetCapabilitiesRequest();
        for (int i = 0; i < params.length; i++) {
            String[] nameAndValue = params[i].split("=");
            if (nameAndValue.length < 2) {
                log.error("Missing value for " + nameAndValue[0] + " parameter!");
                OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                     "HttpGetRequestDecoder.receiveRequest()",
                                     "Missing value for " + nameAndValue[0] + " parameter!");
                throw se;
            }
            // service
            if ( (nameAndValue[0].equalsIgnoreCase(SirConstants.GetCapGetParams.SERVICE.name()))) {
                sirRequest.setService(nameAndValue[1]);
            }
            // AcceptVersions
            if (nameAndValue[0].equalsIgnoreCase(SirConstants.GetCapGetParams.ACCEPTVERSIONS.name())) {
                sirRequest.setAcceptVersions(nameAndValue[1].split(","));
            }
            // Sections
            if (nameAndValue[0].equalsIgnoreCase(SirConstants.GetCapGetParams.SECTIONS.name())) {
                sirRequest.setSections(nameAndValue[1].split(","));
            }
            // updateSequence
            if (nameAndValue[0].equalsIgnoreCase(SirConstants.GetCapGetParams.UPDATESEQUENCE.name())) {
                sirRequest.setUpdateSequence(nameAndValue[1]);
            }
            // AcceptFormats
            if (nameAndValue[0].equalsIgnoreCase(SirConstants.GetCapGetParams.ACCEPTFORMATS.name())) {
                sirRequest.setAcceptFormats(nameAndValue[1].split(","));
            }
        }
        return sirRequest;
    }

    @Override
    public AbstractSirRequest receiveRequest(String queryString) throws OwsExceptionReport {
        // check queryString
        if ( ! (queryString != null && queryString.length() != 0)) {
            log.error("Invalid GET request!");
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 "HttpGetRequestDecoder.receiveRequest()",
                                 "Invalid GET request!");
            throw se;
        }

        String[] params = queryString.split("&");

        // if less than 2 parameters, throw exception
        if (params.length < 2) {
            log.error("Invalid GET request! At least 2 parameters needed: {}", queryString);
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 "HttpGetRequestDecoder.receiveRequest()",
                                 "Invalid GET request! At least 2 parameters needed!");
            throw se;
        }

        for (String param : params) {
            String[] nameAndValue = param.split("=");
            // check the request parameter
            if (nameAndValue[0].equalsIgnoreCase(SirConstants.GETREQUESTPARAM)) {

                // check request = GetCapabilities
                if (nameAndValue[1].equalsIgnoreCase(SirConstants.Operations.GetCapabilities.name())) {
                    log.debug("**** GetCapabilities");
                    return decodeGetCapabilities(params);
                }

                // check request = DescribeSensor
                if (nameAndValue[1].equalsIgnoreCase(SirConstants.Operations.DescribeSensor.name())) {
                    log.debug("**** DescribeSensor");
                    return decodeDescribeSensor(params);
                }
            }
        }

        log.error(SirConstants.GETREQUESTPARAM + " is needed: {}", queryString);
        OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
        se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                             "HttpGetRequestDecoder.receiveRequest()",
                             SirConstants.GETREQUESTPARAM + " is needed!");
        throw se;
    }
}

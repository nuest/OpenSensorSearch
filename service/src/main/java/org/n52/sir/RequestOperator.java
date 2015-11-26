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
package org.n52.sir;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.Tools;
import org.n52.sir.decode.IHttpGetRequestDecoder;
import org.n52.sir.decode.IHttpPostRequestDecoder;
import org.n52.sir.listener.ConnectToCatalogListener;
import org.n52.sir.listener.DeleteSensorInfoListener;
import org.n52.sir.listener.DescribeSensorListener;
import org.n52.sir.listener.DisconnectFromCatalogListener;
import org.n52.sir.listener.GetCapabilitiesListener;
import org.n52.sir.listener.GetSensorStatusListener;
import org.n52.sir.listener.HarvestServiceListener;
import org.n52.sir.listener.ISirRequestListener;
import org.n52.sir.listener.InsertSensorInfoListener;
import org.n52.sir.listener.InsertSensorStatusListener;
import org.n52.sir.listener.SearchSensorListener;
import org.n52.sir.listener.UpdateSensorDescriptionListener;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirConnectToCatalogRequest;
import org.n52.sir.request.SirDeleteSensorInfoRequest;
import org.n52.sir.request.SirDescribeSensorRequest;
import org.n52.sir.request.SirDisconnectFromCatalogRequest;
import org.n52.sir.request.SirGetCapabilitiesRequest;
import org.n52.sir.request.SirGetSensorStatusRequest;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.request.SirInsertSensorInfoRequest;
import org.n52.sir.request.SirInsertSensorStatusRequest;
import org.n52.sir.request.SirSearchSensorRequest;
import org.n52.sir.request.SirSubscriptionRequest;
import org.n52.sir.request.SirUpdateSensorDescriptionRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 * @author Jan Schulte, Daniel Nüst (d.nuest@52north.org)
 *
 */
@Singleton
public class RequestOperator {

    private static final Logger log = LoggerFactory.getLogger(RequestOperator.class);

    private IHttpGetRequestDecoder httpGetDecoder;

    private IHttpPostRequestDecoder httpPostDecoder;

    private HashMap<String, ISirRequestListener> reqListener = new HashMap<>();

    @Inject
    public RequestOperator(IHttpGetRequestDecoder getDecoder,
                           IHttpPostRequestDecoder postDecoder,
                           Set<ISirRequestListener> listeners) {
        this.httpGetDecoder = getDecoder;
        this.httpPostDecoder = postDecoder;

        for (ISirRequestListener listener : listeners) {
            addRequestListener(listener);
        }

        log.info("NEW {}", this);
    }

    private void addRequestListener(ISirRequestListener listener) {
        String name = listener.getOperationName();

        if (this.reqListener.containsKey(name))
            log.warn("Replacing listener for {}", name);

        this.reqListener.put(name, listener);

        log.debug("Added new request listener for operation {}: {}",
                  listener.getOperationName(),
                  listener.getClass().getName());
    }

    /**
     * Checks the get request query string and returns the related response
     *
     * @param queryString
     *        the post request string
     * @return the related ISirResponse
     */
    public ISirResponse doGetOperation(String queryString) {
        log.debug("GET {}", queryString);

        ISirResponse response = null;

        AbstractSirRequest request = null;

        try {
            request = this.httpGetDecoder.receiveRequest(queryString);
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }
        catch (Exception e) {
            log.error("Could not receive request, error: {} \nRequest: {}", e.getMessage(), queryString, e);
            return new ExceptionResponse(e);
        }

        if (request instanceof SirGetCapabilitiesRequest) {
            GetCapabilitiesListener capListener = (GetCapabilitiesListener) this.reqListener.get(SirConstants.Operations.GetCapabilities.name());
            log.info("Listener: " + capListener);
            response = capListener.receiveRequest(request);
        }
        else if (request instanceof SirDescribeSensorRequest) {
            DescribeSensorListener descSensListener = (DescribeSensorListener) this.reqListener.get(SirConstants.Operations.DescribeSensor.name());
            log.info("Listener: " + descSensListener);
            response = descSensListener.receiveRequest(request);
        }
        else {
            log.error("Invalid Get request!");
            OwsExceptionReport se = new OwsExceptionReport(OwsExceptionReport.ExceptionLevel.DetailedExceptions);
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                 "HttpGetRequestDecoder.receiveRequest()",
                                 "Invalid Get request!");
            return new ExceptionResponse(se.getDocument());
        }

        return response;
    }

    /**
     * Checks the post request query string and returns the related response
     *
     * @param inputString
     *        the post request document
     * @param requestUri endpoint for the post request
     * @return Returns the related ISirResponse
     */
    public ISirResponse doPostOperation(String inputString, URI requestUri) {
        ISirResponse response = null;
        AbstractSirRequest request = null;

        try {
            request = this.httpPostDecoder.receiveRequest(inputString);
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e);
        }
        catch (IllegalArgumentException e) {
            log.error("Illegal argument in request: ", e);

            OwsExceptionReport owser = new OwsExceptionReport();
            owser.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                    e.getClass().toString(),
                                    "The request contained an illeagal argument: " + e.getMessage() + "\n\n"
                                            + Tools.getStackTrace(e));
            return new ExceptionResponse(owser.getDocument());
        }

        request.setRequestUri(requestUri);

        // getCapabilities request
        if (request instanceof SirGetCapabilitiesRequest) {
            GetCapabilitiesListener capListener = (GetCapabilitiesListener) this.reqListener.get(SirConstants.Operations.GetCapabilities.name());
            response = capListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("GetCapabilities operation executed successfully!");
        }

        // harvestService request
        else if (request instanceof SirHarvestServiceRequest) {
            HarvestServiceListener harvServListener = (HarvestServiceListener) this.reqListener.get(SirConstants.Operations.HarvestService.name());
            response = harvServListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("HarvestService operation executed successfully!");

        }

        // updatesensordescription request
        else if (request instanceof SirUpdateSensorDescriptionRequest) {
            UpdateSensorDescriptionListener updSensDescrListener = (UpdateSensorDescriptionListener) this.reqListener.get(SirConstants.Operations.UpdateSensorDescription.name());
            response = updSensDescrListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("UpdateSensorDescription operation executed successfully!");
        }

        // describeSensor request
        else if (request instanceof SirDescribeSensorRequest) {
            DescribeSensorListener descSensListener = (DescribeSensorListener) this.reqListener.get(SirConstants.Operations.DescribeSensor.name());
            response = descSensListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("DescribeSensor operation executed successfully!");
        }

        // insertSensorStatus request
        else if (request instanceof SirInsertSensorStatusRequest) {
            InsertSensorStatusListener insSensStatListener = (InsertSensorStatusListener) this.reqListener.get(SirConstants.Operations.InsertSensorStatus.name());
            response = insSensStatListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("InsertSensorStatus operation executed successfully!");
        }

        // insertSensorInfo request
        else if (request instanceof SirInsertSensorInfoRequest) {
            InsertSensorInfoListener insSensInfoListener = (InsertSensorInfoListener) this.reqListener.get(SirConstants.Operations.InsertSensorInfo.name());
            response = insSensInfoListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("InsertSensorInfo operation executed successfully!");
        }

        // deleteSensorInfo request
        else if (request instanceof SirDeleteSensorInfoRequest) {
            DeleteSensorInfoListener deleteSensorInfoListener = (DeleteSensorInfoListener) this.reqListener.get(SirConstants.Operations.DeleteSensorInfo.name());
            response = deleteSensorInfoListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("DeleteSensorInfo operation executed successfully!");
        }

        // searchSensor request
        else if (request instanceof SirSearchSensorRequest) {
            SearchSensorListener searchSensListener = (SearchSensorListener) this.reqListener.get(SirConstants.Operations.SearchSensor.name());
            response = searchSensListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("SearchSensor operation executed successfully!");
        }

        // getSensorStatus request
        else if (request instanceof SirGetSensorStatusRequest) {
            GetSensorStatusListener getSensStatListener = (GetSensorStatusListener) this.reqListener.get(SirConstants.Operations.GetSensorStatus.name());
            response = getSensStatListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("GetSensorStatus operation executed successfully!");
        }

        // connectToCatalog request
        else if (request instanceof SirConnectToCatalogRequest) {
            ConnectToCatalogListener conCatListener = (ConnectToCatalogListener) this.reqListener.get(SirConstants.Operations.ConnectToCatalog.name());
            response = conCatListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse)) {
                log.debug("ConnectToCatalog operation executed successfully!");
            }
        }

        // disconnectFromCatalog request
        else if (request instanceof SirDisconnectFromCatalogRequest) {
            DisconnectFromCatalogListener disCatListener = (DisconnectFromCatalogListener) this.reqListener.get(SirConstants.Operations.DisconnectFromCatalog.name());
            response = disCatListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("DisconnectionFromCatalog operation executed successfully!");
        }

        // subscription requests wrapper
        else if (request instanceof SirSubscriptionRequest) {
            SirSubscriptionRequest subscription = (SirSubscriptionRequest) request;

            ISirRequestListener subscriptionListener = this.reqListener.get(subscription.getName());
            response = subscriptionListener.receiveRequest(request);
            if ( ! (response instanceof ExceptionResponse))
                log.debug("Subscription operation operation executed, not implemented though!");
        }

        return response;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RequestOperator [httpGetDecoder=");
        builder.append(this.httpGetDecoder);
        builder.append(", httpPostDecoder=");
        builder.append(this.httpPostDecoder);
        builder.append(", reqListeners=");
        builder.append(Arrays.toString(this.reqListener.keySet().toArray()));
        builder.append("]");
        return builder.toString();
    }

}

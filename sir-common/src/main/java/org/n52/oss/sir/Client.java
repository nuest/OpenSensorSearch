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
package org.n52.oss.sir;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import net.opengis.ows.x11.VersionType;
import net.opengis.sos.x10.GetCapabilitiesDocument;
import net.opengis.sos.x10.GetCapabilitiesDocument.GetCapabilities;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.oss.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst (daniel.nuest@uni-muenster.de)
 *
 */
public class Client {

    private static final String GET_METHOD = "GET";

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private static final String POST_METHOD = "POST";

    private static final int CONNECTION_TIMEOUT = 1000 * 30;

    protected URI uri = null;

    private HttpClientBuilder httpClientBuilder;

    public Client() {
        httpClientBuilder = HttpClientBuilder.create();
        // increase timeout for slow servers
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_TIMEOUT).setSocketTimeout(CONNECTION_TIMEOUT).build();
        httpClientBuilder.setDefaultRequestConfig(config);

        log.info("NEW {}", this);
    }

    public Client(String endpoint) {
        this.uri = URI.create(endpoint);

        log.info("NEW {}", this);
    }

    public XmlObject requestCapabilities(String serviceType, URI requestUri) throws OwsExceptionReport {
        String gcDoc = createGetCapabilities(serviceType);

        log.debug("GetCapabilities to be send to {} @ {} : {}", serviceType, requestUri.toString(), gcDoc);

        // send getCapabilities request
        XmlObject responseObject = null;
        XmlObject response = null;
        try {
            response = xSendPostRequest(XmlObject.Factory.parse(gcDoc), requestUri);
            responseObject = XmlObject.Factory.parse(response.getDomNode());
        } catch (XmlException xmle) {
            String msg = "Error on parsing Capabilities document: " + xmle.getMessage()
                    + (response == null ? "" : "\n" + response.xmlText());
            log.warn(msg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, msg);
            throw se;
        } catch (Exception e) {
            String errMsg = "Error doing GetCapabilities to " + serviceType + " @ " + requestUri.toString() + " : "
                    + e.getMessage();
            log.warn(errMsg);
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidRequest, null, errMsg);
            throw se;
        }

        return responseObject;
    }

    private String createGetCapabilities(String serviceType) {
        if (serviceType.equals(SirConstants.SOS_SERVICE_TYPE)) {
            GetCapabilitiesDocument gcdoc = GetCapabilitiesDocument.Factory.newInstance();
            GetCapabilities gc = gcdoc.addNewGetCapabilities();
            gc.setService(serviceType);
            VersionType version = gc.addNewAcceptVersions().addNewVersion();
            version.setStringValue(SirConstants.SOS_VERSION);
            return gcdoc.xmlText();
        }
        if (serviceType.equals(SirConstants.SPS_SERVICE_TYPE)) {
            net.opengis.sps.x10.GetCapabilitiesDocument gcdoc = net.opengis.sps.x10.GetCapabilitiesDocument.Factory.newInstance();
            net.opengis.sps.x10.GetCapabilitiesDocument.GetCapabilities gc = gcdoc.addNewGetCapabilities();
            gc.setService(serviceType);
            return gcdoc.xmlText();
        }

        throw new IllegalArgumentException("Service type not supported: " + serviceType);
    }

    private XmlObject doSend(String request, String requestMethod, URI requestUri) {
        log.debug("Sending request (first 100 characters): {}", request.substring(0, Math.min(request.length(), 100)));

        if (requestUri == null) {
            OwsExceptionReport oer = new OwsExceptionReport(ExceptionCode.NoApplicableCode,
                    requestMethod,
                    "given URL is null for request " + request);
            return oer.getDocument();
        }

        try (CloseableHttpClient client = httpClientBuilder.build();) {

            HttpRequestBase method = null;

            if (requestMethod.equals(GET_METHOD)) {
                log.debug("Client connecting via GET to '{}' with request '{}'", requestUri, request);

                String fullUri = null;
                if (request == null || request.isEmpty()) {
                    fullUri = requestUri.toString();
                } else {
                    fullUri = requestUri.toString() + "?" + request;
                }

                log.debug("GET call: {}", fullUri);
                HttpGet get = new HttpGet(fullUri);
                method = get;
            } else if (requestMethod.equals(POST_METHOD)) {
                log.debug("Client connecting via POST to {}", requestUri);
                HttpPost postMethod = new HttpPost(requestUri.toString());

                postMethod.setEntity(new StringEntity(request, ContentType.create(SirConstants.REQUEST_CONTENT_TYPE)));

                method = postMethod;
            } else {
                throw new IllegalArgumentException("requestMethod not supported!");
            }

            try {
                HttpResponse httpResponse = client.execute(method);

                try (InputStream is = httpResponse.getEntity().getContent();) {
                    XmlObject responseObject = XmlObject.Factory.parse(is);
                    return responseObject;
                }
            } catch (XmlException e) {
                log.error("Error parsing response.", e);

                // TODO add handling to identify HTML response
                // if (responseString.contains(HTML_TAG_IN_RESPONSE)) {
                // log.error("Received HTML!\n" + responseString + "\n");
                // }
                String msg = "Could not parse response (received via " + requestMethod + ") to the request\n\n"
                        + request + "\n\n\n" + Tools.getStackTrace(e);
                // msg = msg + "\n\nRESPONSE STRING:\n<![CDATA[" + responseObject.xmlText() + "]]>";

                OwsExceptionReport er = new OwsExceptionReport(ExceptionCode.NoApplicableCode, "Client.doSend()", msg);
                return er.getDocument();
            } catch (Exception e) {
                log.error("Error executing method on httpClient.", e);
                return new OwsExceptionReport(ExceptionCode.NoApplicableCode, "service", e.getMessage()).getDocument();
            }
        } catch (IOException e) {
            log.error("Could not create http client.", e);
            return null;
        }
    }

    public String sendGetRequest(String request) {
        if (request.isEmpty()) {
            return "The request is empty!";
        }
        return xSendGetRequest(request).xmlText();
    }

    public String sendPostRequest(String request) {
        return sendPostRequest(request, this.uri);
    }

    public String sendPostRequest(String request, URI serviceURI) {
        if (request.isEmpty()) {
            return "The request is empty!";
        }

        XmlObject response = doSend(request, POST_METHOD, serviceURI);
        return response.toString();
    }

    public XmlObject xSendGetRequest(String request) {
        log.debug("Sending request: {}", request);
        XmlObject response = doSend(request, GET_METHOD, this.uri);
        return response;
    }

    public XmlObject xSendGetRequest(URI requestUri) {
        log.debug("Sending request: {}", requestUri);
        XmlObject response;
        response = doSend(null, GET_METHOD, requestUri);
        return response;
    }

    public XmlObject xSendPostRequest(XmlObject request) {
        log.debug("Sending request: {}", request);
        return xSendPostRequest(request, this.uri);
    }

    public XmlObject xSendPostRequest(XmlObject request, URI serviceURI) {
        XmlObject response = doSend(request.xmlText(), POST_METHOD, serviceURI);
        return response;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Client [uri=");
        builder.append(this.uri);
        builder.append("]");
        return builder.toString();
    }

}

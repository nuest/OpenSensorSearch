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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.n52.oss.config.ApplicationConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.view.Viewable;

/**
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
@Path("/sir")
@RequestScoped
public class SIR {

    private static final Logger log = LoggerFactory.getLogger(SIR.class);

    private ApplicationConstants appConstants;

    RequestOperator requestOperator;

    @Inject
    public SIR(ApplicationConstants constants, RequestOperator requestOperator) {
        this.appConstants = constants;
        this.requestOperator = requestOperator;

        log.info("{} | Version: {} | Build: {} | From: {}",
                 this,
                 this.appConstants.getApplicationVersion(),
                 this.appConstants.getApplicationCommit(),
                 this.appConstants.getApplicationTimestamp());

        log.info(" ***** NEW {} *****", this);
    }

    @PreDestroy
    protected void shutdown() throws Throwable {
        log.info("SHUTDOWN called...");
        // SirConfigurator.getInstance().getExecutor().shutdown();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response doGet(@Context
    UriInfo uriInfo) {
        String query = uriInfo.getRequestUri().getQuery();
        log.debug(" ****** (GET) Connected: {} ****** ", query);

        // TODO limit the scope of the input parameters to this method
        // MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

        ISirResponse sirResp = this.requestOperator.doGetOperation(query);
        return doResponse(sirResp);
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    public Response doPost(@Context
    HttpServletRequest req, @Context
    UriInfo uri, String body) {
        log.debug(" ****** (POST) Connected from: {}", uri.getAbsolutePath());
        log.debug("POST body: {}", body);

        // Read the request
        String inputString = body;

        // // try (BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));) {
        // try (BufferedReader br = req.getReader();) {
        // String line;
        // StringBuffer sb = new StringBuffer();
        // while ( (line = br.readLine()) != null) {
        // sb.append(line + "\n");
        // }
        // br.close();
        // inputString = sb.toString();

        try {
            // discard "request="
            if (inputString.startsWith("request=")) {
                inputString = inputString.substring(8, inputString.length());
                inputString = java.net.URLDecoder.decode(inputString, "UTF-8");
            }
        }
        catch (Exception e) {
            log.error("Exception reading input stream.", e);
            return doResponse(new ExceptionResponse(e));
        }

        if (inputString.isEmpty())
            return doResponse(new ExceptionResponse(new OwsExceptionReport(ExceptionCode.InvalidRequest,
                                                                           "request",
                                                                           "request is empty.")));

        try {
            ISirResponse sirResp = this.requestOperator.doPostOperation(inputString, uri.getAbsolutePath());
            return doResponse(sirResp);
        }
        catch (Exception e) {
            log.error("Unhanlded error processing operation.", e);
            return doResponse(new ExceptionResponse(e));
        }
    }

    private Response doResponse(final ISirResponse sirResp) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                try (BufferedOutputStream bus = new BufferedOutputStream(os);) {
                    log.debug("Writing streamed response of: {}", sirResp);

                    byte[] bytes;
                    try {
                        bytes = sirResp.getByteArray();
                    }
                    catch (Exception e) {
                        log.error("Could not serialize response.", e);
                        throw new WebApplicationException(e);
                    }
                    bus.write(bytes);
                }
                catch (Exception e) {
                    log.error("Could not write to response stream.", e);
                }
            }
        };

        return Response.ok(stream).build();
    }

    @GET
    @Path("/search")
    public Response index() {
        return Response.ok().entity(new Viewable("/search")).build();
    }

}
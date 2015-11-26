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
package org.n52.oss.api;

import java.net.URI;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.n52.oss.config.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

@Path(ApiPaths.API)
@Singleton
public class ApiResource {

    private static final Logger log = LoggerFactory.getLogger(ApiResource.class);

    private URI baseUri;

    private ApplicationConstants appConstants;

    @Inject
    public ApiResource(@Context
    UriInfo uri, ApplicationConstants appConstants) {
        this.baseUri = uri.getBaseUri();
        this.appConstants = appConstants;

        log.info("NEW {} @ {}", this, this.baseUri);
    }

    @GET
//    @ApiOperation(value = "List of available API versions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiRoot() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { ");

        sb.append("\"currentVersion\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.API_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"v1\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.API_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"appVersion\" : \"");
        sb.append(this.appConstants.getApplicationVersion());
        sb.append("\"");
        sb.append(" , ");
        sb.append("\"appCommit\" : \"");
        sb.append(this.appConstants.getApplicationCommit());
        sb.append("\"");
        sb.append(" , ");
        sb.append("\"appTimestamp\" : \"");
        sb.append(this.appConstants.getApplicationTimestamp());
        sb.append("\"");

        sb.append(" } ");

        return Response.ok(sb.toString()).build();
    }

    @GET
    @Path(ApiPaths.API_VERSION)
//    @ApiOperation(value = "List of available endpoints of this version")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVersionRoot() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { ");

        // TODO this must work dynamically, i.e. the respective modules must register themselves!

        sb.append("\"statistics\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.STATISTICS_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"sensors\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.SENSORS_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"services\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.SERVICES_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"conversion\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.TRANSFORMATION_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"check\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.CHECK_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"opensearch\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.OPENSEARCH_PATH);
        sb.append("\"");
        sb.append(" , ");

        sb.append("\"autocomplete\" : \"");
        sb.append(this.baseUri);
        sb.append(ApiPaths.AUTOSUGGEST_PATH);
        sb.append("\"");

        sb.append(" } ");

        return Response.ok(sb.toString()).build();
    }

}

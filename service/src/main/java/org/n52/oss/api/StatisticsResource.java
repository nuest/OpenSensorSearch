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
/** @author Yakoub
 */

package org.n52.oss.api;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * TODO: add a cache of the values so that the database is only queried every x minutes.
 * 
 * @author Daniel
 * 
 */
@Path(ApiPaths.STATISTICS_PATH)
//@Api(value = ApiPaths.STATISTICS_PATH, description = "Endpoint of all of the statistics related to sensors in OSS")
@Singleton
public class StatisticsResource {

    private static Logger log = LoggerFactory.getLogger(StatisticsResource.class);

    private IGetCapabilitiesDAO capabilitiesDao;

    private String baseUrl;

    @Inject
    public StatisticsResource(IGetCapabilitiesDAO dao, @Context
    UriInfo uri) {
        this.capabilitiesDao = dao;
        this.baseUrl = uri.getBaseUri() + ApiPaths.STATISTICS_PATH;

        log.debug("NEW {} @ {}", this, this.baseUrl);
    }

    @GET
//    @ApiOperation(value = "List of available statistics")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatisticsIndex() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { ");
        sb.append("\"sensors\" : \"");
        sb.append(this.baseUrl);
        sb.append(ApiPaths.SUB_SENSORS);
        sb.append("\"");
        sb.append(" , ");
        sb.append("\"phenomena\" : \"");
        sb.append(this.baseUrl);
        sb.append(ApiPaths.SUB_PHENOMENA);
        sb.append("\"");
        sb.append(" , ");
        sb.append("\"services\" : \"");
        sb.append(this.baseUrl);
        sb.append(ApiPaths.SUB_SERVICES);
        sb.append("\"");
        sb.append(" } ");

        return Response.ok(sb.toString()).build();
    }

    @GET
    @Path(ApiPaths.SUB_SENSORS)
//    @ApiOperation(value = "Find the number of sensors stored in OSS")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumberOfSensors() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { \"sensors\": ");

        try {
            sb.append(this.capabilitiesDao.getSensorCount());
        }
        catch (OwsExceptionReport e) {
            return Response.serverError().entity(e).build();
        }
        sb.append(" }");

        return Response.ok(sb.toString()).build();
    }

    @GET
    @Path(ApiPaths.SUB_PHENOMENA)
//    @ApiOperation(value = "Find the number of phenomena stored in OSS")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumberOfPhenomena() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { \"phenomena\": ");

        try {
            sb.append(this.capabilitiesDao.getPhenomenonCount());
        }
        catch (OwsExceptionReport e) {
            return Response.serverError().entity(e).build();
        }
        sb.append(" }");

        return Response.ok(sb.toString()).build();
    }

    @GET
    @Path(ApiPaths.SUB_SERVICES)
//    @ApiOperation(value = "Find the number of services stored in OSS")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNumberOfServices() {
        StringBuilder sb = new StringBuilder();
        sb.append(" { \"services\": ");

        try {
            sb.append(this.capabilitiesDao.getServiceCount());
        }
        catch (OwsExceptionReport e) {
            return Response.serverError().entity(e).build();
        }
        sb.append(" }");

        return Response.ok(sb.toString()).build();
    }

}

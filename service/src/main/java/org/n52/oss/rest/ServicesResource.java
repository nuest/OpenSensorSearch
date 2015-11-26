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
package org.n52.oss.rest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.n52.oss.api.ApiPaths;
import org.n52.oss.sir.api.SirService;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

/**
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 */
@Path(ApiPaths.SERVICES_PATH)
@RequestScoped
public class ServicesResource {

    private static final Logger log = LoggerFactory.getLogger(ServicesResource.class);

    private IGetCapabilitiesDAO dao;

    protected URI baseUri;

    @Inject
    public ServicesResource(IGetCapabilitiesDAO capDao, @Context
    UriInfo uri) {
        this.baseUri = uri.getBaseUriBuilder().path(ApiPaths.SERVICES_PATH).build();

        this.dao = capDao;

        log.info("NEW {} @ {}", this, this.baseUri);
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getSensors(@QueryParam("start")
    int start, @QueryParam("limit")
    int limit) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                try (BufferedOutputStream bus = new BufferedOutputStream(os); PrintStream ps = new PrintStream(bus);) {
                    log.debug("Writing streamed response of services index...");

                    Collection<SirService> allServices = ServicesResource.this.dao.getAllServices();

                    ps.print(" { ");
                    ps.print("\"services\" : [ ");

                    Iterator<SirService> iter = allServices.iterator();
                    while (iter.hasNext()) {
                        SirService service = iter.next();

                        ps.print("{ \"id\" : \"");
                        ps.print(service.getId());
                        ps.print("\" , \"url\" : \"");
                        ps.print(ServicesResource.this.baseUri);
                        ps.print("/");
                        ps.print(service.getId());
                        ps.print("\" }");

                        if (iter.hasNext())
                            ps.print(", ");
                    }

                    ps.print(" ] } ");
                    ps.close();
                }
                catch (Exception e) {
                    log.error("Could not write to response stream.", e);
                    // ps.print("ERROR: ");
                    // ps.print(e.getMessage());
                }
            }

        };

        return Response.ok(stream).build();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getSensor(@PathParam("id")
    String id) {
        log.debug("GET service with id {}", id);

        try {
            SirService service = this.dao.getService(id);

            if (service != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("{ \"id\" : \"");
                sb.append(service.getId());
                sb.append("\" , \"url\" : \"");
                sb.append(service.getUrl());
                sb.append("\" , \"type\" : \"");
                sb.append(service.getType());
                sb.append("\" }");

                return Response.ok(sb.toString()).build();
            }
            return Response.status(Status.NOT_FOUND).entity("{ \"error\" : \"sensor not found.\" }").build();
        }
        catch (OwsExceptionReport e) {
            return Response.serverError().entity(e).build();
        }
    }
}

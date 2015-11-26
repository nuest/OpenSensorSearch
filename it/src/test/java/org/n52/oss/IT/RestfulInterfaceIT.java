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
package org.n52.oss.IT;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class RestfulInterfaceIT {

    private WebResource service;
    private static String testId = "1";

    @Before
    public void prepare() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        this.service = client.resource(getBaseURI());
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/api/v1").build();
    }

    @Test
    public void sensorResourceIsReturned() {
        String string = this.service.path("sensor").path(testId).accept(MediaType.APPLICATION_JSON).get(String.class);
        // TODO implement integration test for restful interface:
        // http://localhost:8080/OpenSensorSearch/api/v1/sensors/1?detailed=false
        // http://localhost:8080/OpenSensorSearch/api/v1/sensors/1
        System.out.println(string);
    }

    @Test
    public void detailedSensorResourceIsReturned() {
        String string = this.service.path("sensor").path(testId).accept(MediaType.APPLICATION_JSON).get(String.class);
        // TODO implement integration test for restful interface:
        // http://localhost:8080/OpenSensorSearch/api/v1/sensors/1?detailed=true
        System.out.println(string);
    }

}

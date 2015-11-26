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
package org.n52.oss.stats;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.api.StatisticsResource;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.skyscreamer.jsonassert.JSONAssert;

public class Statistics {

    private static IGetCapabilitiesDAO dao;

    private static Long sensors = Long.valueOf(42l);
    private static Long phenomena = Long.valueOf(1l);
    private static Long services = Long.valueOf(17l);

    private static String sensorResponse = "{ \"sensors\": " + sensors.toString() + " }";
    private static String phenomenaResponse = "{ \"phenomena\": " + phenomena.toString() + "}";
    private static String servicesResponse = "{ \"services\": " + services.toString() + "}";

    private StatisticsResource resource;

    @BeforeClass
    public static void prepare() throws OwsExceptionReport {
        dao = mock(IGetCapabilitiesDAO.class);

        when(dao.getSensorCount()).thenReturn(sensors);
        when(dao.getPhenomenonCount()).thenReturn(phenomena);
        when(dao.getServiceCount()).thenReturn(services);
    }

    @Before
    public void setUp() throws URISyntaxException {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(new URI("TEST_URL"));
        this.resource = new StatisticsResource(dao, uriInfo);
    }

    @Test
    public void sensorCount() throws JSONException {
        Response response = this.resource.getNumberOfSensors();
        String actual = (String) response.getEntity();

        JSONAssert.assertEquals(sensorResponse, actual, false);
    }

    @Test
    public void phenomenonCount() throws JSONException {
        Response response = this.resource.getNumberOfPhenomena();
        String actual = (String) response.getEntity();

        JSONAssert.assertEquals(phenomenaResponse, actual, false);
    }

    @Test
    public void servicesCount() throws JSONException {
        Response response = this.resource.getNumberOfServices();
        String actual = (String) response.getEntity();

        JSONAssert.assertEquals(servicesResponse, actual, false);
    }
}

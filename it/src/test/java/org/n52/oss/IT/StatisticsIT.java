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

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.api.ApiPaths;
import org.skyscreamer.jsonassert.JSONAssert;

public class StatisticsIT {

    private static Long sensors = Long.valueOf(42l);
    private static Long phenomena = Long.valueOf(1l);
    private static Long services = Long.valueOf(17l);

    private static String sensorResponse = "{ \"sensors\": " + sensors.toString() + " }";
    private static String phenomenaResponse = "{ \"phenomena\": " + phenomena.toString() + "}";
    private static String servicesResponse = "{ \"services\": " + services.toString() + "}";

    private static String path = ApiPaths.STATISTICS_PATH; // "/api/v1/statistics";

    private static String endpoint;

    @BeforeClass
    public static void prepare() {
        endpoint = "http://localhost:8080/OpenSensorSearch" + path;

        // TODO load test dataset
    }

    @Test
    public void sensorCount() throws JSONException, UnsupportedEncodingException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(new HttpGet(StatisticsIT.endpoint + "/sensors"));
        String actual = Util.getResponsePayload(response);

        JSONAssert.assertEquals(sensorResponse, actual, false);
    }

    @Test
    public void phenomenonCount() throws JSONException, UnsupportedEncodingException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(new HttpGet(StatisticsIT.endpoint + "/phenomena"));
        String actual = Util.getResponsePayload(response);

        JSONAssert.assertEquals(phenomenaResponse, actual, false);
    }

    @Test
    public void servicesCount() throws JSONException, UnsupportedEncodingException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(new HttpGet(StatisticsIT.endpoint + "/services"));
        String actual = Util.getResponsePayload(response);

        JSONAssert.assertEquals(servicesResponse, actual, false);
    }
}

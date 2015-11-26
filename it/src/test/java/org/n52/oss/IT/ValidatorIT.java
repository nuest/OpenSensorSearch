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
/**
 * @author Yakoub
 */

package org.n52.oss.IT;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class ValidatorIT {

    private static String smlCheckEndpoint = "http://localhost:8080/oss-service/api/v1/check/sml";

    @BeforeClass
    public static void prepare() {
        //
    }

    @Test
    public void testValid_json() throws JSONException, ClientProtocolException, IOException {
        String responseString = executeCheckRequest("/AirBase-test.xml");
        JSONAssert.assertEquals("{ \"status\": \"valid\" }", responseString, false);
    }

    @Test
    public void testInvalid_json() throws ClientProtocolException, IOException {
        String responseString = executeCheckRequest("/AirBase-test-invalid.xml");

        assertThat("is invalid", responseString, containsString("invalid"));
        assertThat("has message0", responseString, containsString("message0"));
        assertThat("has message1", responseString, containsString("message1"));
        assertThat("found missing validTime", responseString, containsString("sml:validTime"));
        assertThat("found missing description", responseString, containsString("gml:description"));
    }

    private String executeCheckRequest(String testFile) throws IOException, ClientProtocolException {
        String responseString = null;
        File f = new File(getClass().getResource(testFile).getFile());
    
        HttpPost post = new HttpPost(smlCheckEndpoint);
        HttpEntity entity = new FileEntity(f);
        post.setEntity(entity);
        post.setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    
        try (CloseableHttpClient client = HttpClientBuilder.create().build();
                CloseableHttpResponse response = client.execute(post);) {
    
            HttpEntity responseEntity = response.getEntity();
            responseString = EntityUtils.toString(responseEntity);
        }
    
        return responseString;
    }
}

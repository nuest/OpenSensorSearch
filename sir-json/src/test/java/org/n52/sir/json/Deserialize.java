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
package org.n52.sir.json;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Deserialize {

    private ObjectMapper mapper;

    private String searchResult = "{" + "\"source\" : \"http://service.url\"," + "\"query\" : \"temperature\","
            + "\"url\" : \"http://service.url/search?q=temperature\","
            + "\"description\" : \"Search results for the keyword 'temperature' from Open Sensor Search.\","
            + "\"author\" : \"52°North\"," + "\"date\" : 1331022803081," + "\"results\" : [ {"
            + "\"lastUpdate\" : 1331022803082," + "\"sensorDescription\" : {" + "\"boundingBox\" : {"
            + "\"east\" : 1.0," + "\"north\" : 4.0," + "\"south\" : 2.0," + "\"srid\" : 1234," + "\"west\" : 3.0"
            + "}," + "\"text\" : \"This text describes the sensor.\","
            + "\"url\" : \"http://domain.tld:port/path/001\"" + "}," + "\"sensorIdInSir\" : \"001\","
            + "\"serviceReferences\" : [ {" + "\"service\" : {" + "\"type\" : \"SOS\","
            + "\"url\" : \"http://host:port/path\"" + "}," + "\"serviceSpecificSensorId\" : \"urn:sos:001\"" + "} ]"
            + "} ]" + "}";

    @Before
    public void setUp() throws Exception {
        this.mapper = MapperFactory.getMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    public void test() throws Exception {
        SearchResult result = this.mapper.readValue(this.searchResult, SearchResult.class);
        System.out.println(result);
        
        // TODO add assertion to test
    }

}

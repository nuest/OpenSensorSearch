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
/**
 * @author Yakoub
 */

package org.n52.oss.IT;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.GuiceUtil;
import org.n52.sir.json.MapperFactory;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;
import org.xml.sax.SAXException;

import uk.co.datumedge.hamcrest.json.SameJSONAs;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenSearchIT {

    private static Logger log = LoggerFactory.getLogger(OpenSearchIT.class);

    private static Client client;

    @BeforeClass
    public static void prepare() throws XmlException, IOException, OwsExceptionReport, HttpException {
        client = GuiceUtil.configureSirClient();

        insertSensor("requests/testsensor.xml");
        // insertSensor("requests/sensors/testSensor02.xml");
    }

    private static void insertSensor(String path) throws XmlException, IOException, OwsExceptionReport, HttpException {
        File sensor = new File(ClassLoader.getSystemResource(path).getFile());
        SensorMLDocument sensorMLDoc = SensorMLDocument.Factory.parse(sensor);

        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(sensorMLDoc.getSensorML().getMemberArray(0).getProcess());
        XmlObject res = client.xSendPostRequest(req);

        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());
        log.debug("Inserted sensor: {}", resp.xmlText());
        // assertThat("inserted sensor successfully",
        // resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(),
        // is(not(0)));
    }

    public String buildQuery(String q, String format) {
        StringBuilder query = new StringBuilder();
        query.append("http://localhost:8080/OpenSensorSearch/search?q=");
        query.append(q);
        query.append("&format=");
        query.append(format);
        return query.toString();
    }

    private String sendRequest(String query) throws ClientProtocolException, IOException {
        HttpClient c = new DefaultHttpClient();
        HttpGet get = new HttpGet(query);

        HttpResponse response = c.execute(get);
        StringBuilder builder = new StringBuilder();

        String responseString = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));) {
            String s = "";
            while ( (s = reader.readLine()) != null)
                builder.append(s);

            responseString = builder.toString();
            reader.close();
        }

        return responseString;
    }

    private String readResource(String name) throws IOException {
        File results = new File(ClassLoader.getSystemResource(name).getFile());
        StringBuilder builder = new StringBuilder();

        String realResults = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(results));) {
            String string = "";
            while ( (string = reader.readLine()) != null)
                builder.append(string);
            realResults = builder.toString();
            reader.close();
        }

        return realResults;
    }

    @Test
    public void testRSSResponseFromOpenSearch() throws IOException, SAXException {
        String realResult = readResource("requests/sensors/testSensor01Result.rss");
        String responseResult = sendRequest(buildQuery("urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059",
                                                       "application/rss"));

        assertXMLEqual(realResult, responseResult);
    }

    @Test
    public void testXMLResponseFromOpenSearch() throws IOException, SAXException {
        String realResult = readResource("requests/sensors/testSensor01Result.XML");
        String responseResult = sendRequest(buildQuery("urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059",
                                                       "application/xml"));

        assertXMLEqual(realResult, responseResult);
    }

    @Test
    public void testJSONResponseFromOpenSearch() throws IOException {
        String realResult = readResource("requests/sensors/jsonSensor.json");

        ObjectMapper mapper = MapperFactory.getMapper();

        SearchResult realResultObj = mapper.readValue(realResult, SearchResult.class);

        // Get the sensor
        Collection<SearchResultElement> sensors = realResultObj.getResults();

        SearchResultElement sensorJson = sensors.iterator().next();

        String sensorJsonStr = mapper.writeValueAsString(sensorJson);

        String responseResult = sendRequest(buildQuery("test", "application/json"));

        SearchResult result = mapper.readValue(responseResult, SearchResult.class);
        Collection<SearchResultElement> results = result.getResults();
        assertTrue(results.size() > 0);

        Iterator<SearchResultElement> it = results.iterator();

        while (it.hasNext()) {
            SearchResultElement elem = it.next();
            String resultsensor = mapper.writeValueAsString(elem);
            if (elem.getSensorId() == sensorJson.getSensorId())
                assertThat(resultsensor,
                           SameJSONAs.sameJSONAs(sensorJsonStr).allowingExtraUnexpectedFields().allowingAnyArrayOrdering());

        }

    }

    @Test
    public void testKMLResponseFromOpenSearch() throws IOException, SAXException {
        String realResult = readResource("requests/sensors/testSensor01Result.kml");
        String responseResult = sendRequest(buildQuery("urn:ogc:object:feature:Sensor:EEA:airbase:4.0:DEBB059",
                                                       "application/kml"));
        assertXMLEqual(realResult, responseResult);
    }

}
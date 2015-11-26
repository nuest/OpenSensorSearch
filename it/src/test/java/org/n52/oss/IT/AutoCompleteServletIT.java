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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlbeans.XmlException;
import org.junit.Test;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.sml.SensorMLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TODO inserted test sensor is not deleted afterwards.
 * 
 * @author Yakoub
 */
public class AutoCompleteServletIT {
    private static Logger log = LoggerFactory.getLogger(AutoCompleteServletIT.class);
    private String insertedSensorId;

    // @Before
    public void insertSensor() throws OwsExceptionReport, XmlException, IOException {
        File sensor_status = new File(ClassLoader.getSystemResource("Requests/testsensor.xml").getFile());
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_status);

        // FIXME use mocked up up database backend
        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO(new SolrConnection("http://localhost:8983/solr", 1000));
        SensorMLDecoder d = new SensorMLDecoder();
        this.insertedSensorId = dao.insertSensor(d.decode(doc));
        log.debug("inserted test sensor: {}", this.insertedSensorId);
    }

    @Test
    public void testServlet() throws ClientProtocolException, IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet("http://localhost:8080/OpenSensorSearch/suggest?q=te");

        HttpResponse response = client.execute(get);
        String actual = Util.getResponsePayload(response);

        String expected = "{ \"suggestions\": [\"structual\", \"stringtheory\", \"a really strange keyword to use in a sensor description\"] }";
        assertThat("reponse string is correct", actual, is(equalTo(expected)));
    }

    // @After
    // public void deleteTestSensor() throws SolrServerException, IOException{
    // new SolrConnection().deleteByQuery("");
    // }
}

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

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.Iterator;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.sml.SensorMLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Yakoub
 */
public class OpenSearchTemporalExtensionIT {
    private static final Logger log = LoggerFactory.getLogger(OpenSearchTemporalExtensionIT.class);
    
    // TODO get the base URL using dependency injectin
    private static String query = "http://localhost:8080/OpenSensorSearch/search?q=test&dtstart=2009-12-31T22:00:00Z&dtend=2011-12-30T22:00:00Z&httpAccept=application%2Fjson";
    private Date start = new Date(1262296800000l);
    private Date end = new Date(1325282400000l);

    @Before
    public void insertSensor() throws OwsExceptionReport, XmlException, IOException {
        File sensor_status = new File(ClassLoader.getSystemResource("Requests/testsensor.xml").getFile());
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_status);

        SolrConnection c = new SolrConnection("http://localhost:8983/solr", 2000);
        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO(c);
        SensorMLDecoder d = new SensorMLDecoder();
        dao.insertSensor(d.decode(doc));
    }

    @Test
    public void testTemporal() throws ClientProtocolException, IOException {
        org.apache.http.client.HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(query);

        HttpResponse response = client.execute(get);
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String s;
        while ( (s = reader.readLine()) != null)
            builder.append(s);

        log.debug(builder.toString());
        System.out.println(builder.toString());
        ObjectMapper mapper = new ObjectMapper();
        SearchResult result = mapper.readValue(builder.toString(), SearchResult.class);

        Iterator<SearchResultElement> iter = result.getResults().iterator();
        while (iter.hasNext()) {
            SearchResultElement element = iter.next();
            if (element.getBeginDate() != null) {
                assertTrue(element.getBeginDate().getTime() >= this.start.getTime());
                assertTrue(element.getBeginDate().getTime() <= this.end.getTime());
            }
            if (element.getEndDate() != null) {
                assertTrue(element.getEndDate().getTime() >= this.start.getTime());
                assertTrue(element.getEndDate().getTime() <= this.end.getTime());
            }

        }

    }

    @After
    public void deleteTestSensor() throws SolrServerException, IOException {
        SolrConnection c = new SolrConnection("http://localhost:8983/solr", 2000);
        c.deleteSensor("");
    }
}

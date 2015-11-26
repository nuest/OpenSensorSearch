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

package org.n52.oss.IT.solr;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.sml.SensorMLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchByKeywordTest {

    private static final Logger log = LoggerFactory.getLogger(SearchByKeywordTest.class);

    public String id;

    private SirSensor sensor;

    private static SolrConnection connection;

    @BeforeClass
    public static void prepare() {
        connection = new SolrConnection("http://localhost:8983/solr", 2000);
    }

    @Before
    public void insertTestSensor() throws OwsExceptionReport, XmlException, IOException {
        // Inserts the sensor
        String basePath = (this.getClass().getResource("/Requests").getFile());
        File sensor_file = new File(basePath + "/testSensor.xml");
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
        SensorMLDecoder d = new SensorMLDecoder();
        this.sensor = d.decode(doc);
        log.trace(this.sensor.getText().toArray()[0].toString());

        // FIXME Moh-Yakoub: probably this will take some configuration - haven't decided yet.
        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO(connection);
        this.id = dao.insertSensor(this.sensor);
    }

    // @Test
    public void searchKeywords() throws OwsExceptionReport {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO(connection);
        SirSearchCriteria criteria = new SirSearchCriteria();

        ArrayList<String> searchkeywords = new ArrayList<>();
        for (String keyword : this.sensor.getKeywords()) {
            searchkeywords.add(keyword);
        }
        criteria.setSearchText(searchkeywords);

        Collection<SirSearchResultElement> results = searchDAO.searchSensor(criteria, true);

        assertNotNull(results);
        // assertEquals(results.size(), 1);

        Iterator<SirSearchResultElement> iter = results.iterator();
        SirSearchResultElement result = iter.next();

        // SensorML is stored in the sensor description value
        SirDetailedSensorDescription description = (SirDetailedSensorDescription) result.getSensorDescription();
        assertNotNull(description);

        Collection<String> actual = description.getKeywords();
        Collection<String> expected = this.sensor.getKeywords();

        assertEquals(actual.size(), expected.size());

        assertThat("keywords used for search are given in the returned sensor",
                   actual,
                   containsInAnyOrder(expected.toArray()));
    }

    @After
    public void deleteSensor() throws SolrServerException, IOException {
        connection.deleteSensor(this.id); // FIXME delete inserted sensor only
    }
}
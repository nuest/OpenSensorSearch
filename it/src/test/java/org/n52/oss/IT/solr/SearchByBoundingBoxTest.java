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

package org.n52.oss.IT.solr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.api.SirBoundingBox;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByBoundingBoxTest {

    private static SolrConnection c;

    @BeforeClass
    public static void prepare() {
        c = new SolrConnection("http://localhost:8983/solr", 2000);
    }

    @Before
    public void insertSensor() throws XmlException, IOException, OwsExceptionReport {
        /*
         * Insert testSensor for search
         */
    	String basePath = (this.getClass().getResource("/Requests").getFile());
		File sensor_file = new File(basePath+"/testSensor.xml");
		
        SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
        SensorMLDecoder d = new SensorMLDecoder();
        SirSensor sensor = d.decode(doc);

        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO(c);
        dao.insertSensor(sensor);
    }

    @Test
    public void searchByBoundingBox() {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO(c);
        /*
         * Prepare the list of keywords
         */
        SirBoundingBox box = new SirBoundingBox(2,1,4,3);
        Collection<SirSearchResultElement> results = searchDAO.searchSensorByBoundingBox(box);

        assertNotNull(results);
        assertEquals(results.size(), 1);

        Iterator<SirSearchResultElement> iter = results.iterator();
        SirSearchResultElement result = iter.next();
        // SensorML is stored in the sensor description value
        SirDetailedSensorDescription description = (SirDetailedSensorDescription) result.getSensorDescription();
        assertNotNull(description);
    //    assertTrue("urn:ogc:object:feature:testsensor".equals(description.getId()));
    }

    /*
     * Searches for a sensor but not in the range covered , should return 0
     */
    @Test
    public void searchByLocationNotInRange() {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO(c);
        SirBoundingBox box = new SirBoundingBox(5,6,7,8);
        
        Collection<SirSearchResultElement> results = searchDAO.searchSensorByBoundingBox(box);

        assertNotNull(results);
        assertEquals(results.size(), 0);

    }

    // FIXME let the delete delete only by the given id not all
    @After
    public void deleteSensor() throws SolrServerException, IOException{
        c.deleteSensor("");
    }
}

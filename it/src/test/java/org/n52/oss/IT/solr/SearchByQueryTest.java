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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.ds.solr.SolrConstants;
import org.n52.sir.ds.solr.SolrUtils;
import org.n52.sir.sml.SensorMLDecoder;

public class SearchByQueryTest {

	public String id;
	public static final double R = 6372.8; // In kilometers

    private static SolrConnection connection;

    @BeforeClass
    public static void prepare() {
        connection = new SolrConnection("http://localhost:8983/solr", 2000);
    }

	@Before
	public void insertSensor() throws XmlException, IOException,
			OwsExceptionReport {
		String basePath = (this.getClass().getResource("/Requests").getFile());
		File sensor_file = new File(basePath+"/testSensor.xml");
		SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_file);
        SensorMLDecoder d = new SensorMLDecoder();
        SirSensor sensor = d.decode(doc);
        SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO(connection);
		dao.insertSensor(sensor);
	}

	@Test
	public void keywordTemporalSearch() {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO(connection);
		// Search by keywords and By StartDate
		Map<String, String> map = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 11, 31);
        Date start = cal.getTime();

        cal = Calendar.getInstance();
        cal.set(2012, 0, 30);
        Date end = cal.getTime();

		map.put("keyword", "TEST");
		map.put("dtstart", SolrUtils.getISO8601UTCString(start));
		map.put("dtend", SolrUtils.getISO8601UTCString(end));

		Collection<SirSearchResultElement> results = searchDAO.searchByQuery(
				map, SolrConstants.OR_OP);
		assertTrue(results.size() > 0);
		assertNotNull(results);
		Iterator<SirSearchResultElement> iterator = results.iterator();
		while (iterator.hasNext()) {
			SirSearchResultElement result = iterator.next();
			SirDetailedSensorDescription desc = (SirDetailedSensorDescription) result
					.getSensorDescription();

			assertTrue(desc.getKeywords().contains("TEST")
					|| (((desc.getBegineDate().getTime() >= start.getTime()) && (desc
							.getBegineDate().getTime() <= end.getTime())) && ((desc
							.getEndDate().getTime() >= start.getTime()) && (desc
							.getEndDate().getTime() <= end.getTime()))));
		}

	}

	@Test
	public void keywordSpatialSearch() {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO(connection);
		// Search by keywords and By StartDate
		Map<String, String> map = new HashMap<>();
		map.put("keyword", "test");
		map.put("lat", "1.5");
		map.put("lng", "3.49");
		map.put("radius", "2");

		Collection<SirSearchResultElement> results = searchDAO.searchByQuery(
				map, SolrConstants.OR_OP);
		assertTrue(results.size() > 0);
		assertNotNull(results);
		Iterator<SirSearchResultElement> iterator = results.iterator();
		while (iterator.hasNext()) {
			SirSearchResultElement result = iterator.next();
			SirDetailedSensorDescription desc = (SirDetailedSensorDescription) result
					.getSensorDescription();
			String loc = desc.getLocation();
			String[] latlng = loc.split(",");
			String lat = latlng[0];
			String lng = latlng[1];
			double dist = (haversine(1.5, 3.49, Double.parseDouble(lat),
					Double.parseDouble(lng)));
			assertTrue(desc.getKeywords().contains("test") && dist < 2);
		}

	}

	@Test
	public void temporalSpatialSearch() {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO(connection);
		// Search by keywords and By StartDate
		Map<String, String> map = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 11, 31);
        Date start = cal.getTime();

        cal = Calendar.getInstance();
        cal.set(2012, 0, 30);
        Date end = cal.getTime();

		map.put("lat", "1.5");
		map.put("lng", "3.49");
		map.put("radius", "2");
		map.put("dtstart", SolrUtils.getISO8601UTCString(start));
		map.put("dtend", SolrUtils.getISO8601UTCString(end));
		Collection<SirSearchResultElement> results = searchDAO.searchByQuery(
				map, SolrConstants.OR_OP);
		assertTrue(results.size() > 0);
		assertNotNull(results);
		Iterator<SirSearchResultElement> iterator = results.iterator();
		while (iterator.hasNext()) {
			SirSearchResultElement result = iterator.next();
			SirDetailedSensorDescription desc = (SirDetailedSensorDescription) result
					.getSensorDescription();
			String loc = desc.getLocation();
			String[] latlng = loc.split(",");
			String lat = latlng[0];
			String lng = latlng[1];
			double dist = (haversine(1.5, 3.49, Double.parseDouble(lat),
					Double.parseDouble(lng)));
			
			assertTrue((((desc.getBegineDate().getTime() >= start.getTime()) && (desc
					.getBegineDate().getTime() <= end.getTime())) && ((desc
					.getEndDate().getTime() >= start.getTime()) && (desc
					.getEndDate().getTime() <= end.getTime()))));
			assertTrue(dist<2);
		}

	}

	@Test
	public void keywordTemporalSpatialSearch() {
        SOLRSearchSensorDAO searchDAO = new SOLRSearchSensorDAO(connection);
		// Search by keywords and By StartDate
		Map<String, String> map = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.set(2009, 11, 31);
        Date start = cal.getTime();

        cal = Calendar.getInstance();
        cal.set(2012, 0, 30);
        Date end = cal.getTime();

		map.put("keyword", "TEST");
		map.put("lat", "1.5");
		map.put("lng", "3.49");
		map.put("radius", "2");
		map.put("dtstart", SolrUtils.getISO8601UTCString(start));
		map.put("dtend", SolrUtils.getISO8601UTCString(end));
		Collection<SirSearchResultElement> results = searchDAO.searchByQuery(
				map, SolrConstants.OR_OP);
		assertTrue(results.size() > 0);
		assertNotNull(results);
		Iterator<SirSearchResultElement> iterator = results.iterator();
		while (iterator.hasNext()) {
			SirSearchResultElement result = iterator.next();
			SirDetailedSensorDescription desc = (SirDetailedSensorDescription) result
					.getSensorDescription();
			String loc = desc.getLocation();
			String[] latlng = loc.split(",");
			String lat = latlng[0];
			String lng = latlng[1];
			double dist = (haversine(1.5, 3.49, Double.parseDouble(lat),
					Double.parseDouble(lng)));
			
			assertTrue((((desc.getBegineDate().getTime() >= start.getTime()) && (desc
					.getBegineDate().getTime() <= end.getTime())) && ((desc
					.getEndDate().getTime() >= start.getTime()) && (desc
					.getEndDate().getTime() <= end.getTime()))));
			assertTrue(dist<2);
		}

	}

	public double haversine(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2)
				* Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}

	@After
	public void deleteSensor() throws SolrServerException, IOException {
        connection.deleteSensor(""); // FIXME delete inserted sensor only
	}

}

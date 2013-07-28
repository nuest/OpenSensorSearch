/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sir.IT.solr;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.util.Iterator;

import javax.servlet.UnavailableException;

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
import org.n52.sir.SirConfigurator;
import org.n52.sir.datastructure.SirSearchResultElement;
import org.n52.sir.datastructure.detailed.SirDetailedSensorDescription;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.ows.OwsExceptionReport;
import org.n52.sir.sml.SensorMLDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Yakoub
 */
public class OpenSearchSpatialExtensionIT {
	private static Logger log = LoggerFactory
			.getLogger(OpenSearchTemporalExtensionIT.class);
	private static String lon_lat_radius_query = "http://localhost:8080/SIR/search?q=pre&lat=1.5&lon=3.49&radius=2&httpAccept=application%2Fjson";
	private Date start = new Date(1262296800000l);
	private Date end = new Date(1325282400000l);
	public static final double R = 6372.8; // In kilometers

	@Before
	public  void insertSensor() throws UnavailableException, OwsExceptionReport, XmlException, IOException {
		InputStream dbStream = ClassLoader
				.getSystemResourceAsStream("prop/db.PROPERTIES");
		InputStream sirStream = ClassLoader
				.getSystemResourceAsStream("prop/sir.PROPERTIES");
		// Read configurator if null
		SirConfigurator.getInstance(sirStream, dbStream, null, null);
		File sensor_status = new File(ClassLoader.getSystemResource(
				"Requests/testsensor.xml").getFile());
		SensorMLDocument doc = SensorMLDocument.Factory.parse(sensor_status);
		
		SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
		dao.insertSensor(SensorMLDecoder.decode(doc));
			
	}
	@Test
	public  void testSpatialLonLatRadius() throws ClientProtocolException, IOException {
		org.apache.http.client.HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(lon_lat_radius_query);
		
		HttpResponse response = client.execute(get);
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String s ;
		while((s=reader.readLine())!=null)
			builder.append(s);
		
		log.debug(builder.toString());
		ObjectMapper mapper = new ObjectMapper();
		SearchResult result = mapper.readValue(builder.toString(),SearchResult.class);

		Iterator<SearchResultElement> iter = result.getResults().iterator();
		while(iter.hasNext()){
			SearchResultElement element = iter.next();
			/**TODO 
			 * Add LngLat to searchResult and assert here
			 */
			
		}
		
	}

	public static double haversine(double lat1, double lon1, double lat2, double lon2) {
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
	public  void deleteTestSensor() throws SolrServerException, IOException{
		new  SolrConnection().deleteByQuery("");
	}
}
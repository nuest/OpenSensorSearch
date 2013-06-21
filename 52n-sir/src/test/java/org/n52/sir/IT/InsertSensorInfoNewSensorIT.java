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

/**
 * @author Yakoub
 */

package org.n52.sir.IT;

import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;


import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.n52.sir.client.Client;
import org.n52.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

public class InsertSensorInfoNewSensorIT {


	@Test
	public void insertSampleSensor() throws XmlException, IOException,
			OwsExceptionReport, HttpException {

		/*
		 * Create a sensor insert request from sensorFile
		 */
		File sensor = new File(ClassLoader.getSystemResource(
				"Requests/testSensor.xml").getFile());
		SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor);

		InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory
				.newInstance();
		req.addNewInsertSensorInfoRequest()
				.addNewInfoToBeInserted()
				.setSensorDescription(
						DOC.getSensorML().getMemberArray(0).getProcess());
		XmlObject res = Client.xSendPostRequest(req);
		InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory
				.parse(res.getDomNode());

		assertNotEquals(
				"Failed to insert sensor",
				resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors() , 0);

		System.out
				.println("Loaded a sensor , encoded and inserted successfully");
		
		/*
		 * TODO delete the sensor here
		 */

	}

	@Test
	public void insertSensorDirectly() throws XmlException, IOException,
			OwsExceptionReport, HttpException {
		File f = new File(ClassLoader.getSystemResource(
				"Requests/InsertSensorInfo_newSensor.xml").getFile());

		InsertSensorInfoRequestDocument doc = InsertSensorInfoRequestDocument.Factory
				.parse(f);

		XmlObject response = Client.xSendPostRequest(doc);

		InsertSensorInfoResponseDocument responseDoc = InsertSensorInfoResponseDocument.Factory
				.parse(response.getDomNode());
		// Check the number of inserted sensor
		int number = (responseDoc.getInsertSensorInfoResponse()
				.getNumberOfInsertedSensors());
		assertNotEquals("Failed to insert sensor", number, 0);
	}

}

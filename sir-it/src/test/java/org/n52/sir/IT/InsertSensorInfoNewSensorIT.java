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

package org.n52.sir.IT;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

public class InsertSensorInfoNewSensorIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = Util.configureSirClient();
    }

    private String sensorId;

    @Test
    public void insertAirBaseSensor() throws XmlException, IOException {
        File sensor = new File(ClassLoader.getSystemResource("requests/sir/testSensor-AirBase.xml").getFile());
        SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor);
        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(DOC.getSensorML().getMemberArray(0).getProcess());
        XmlObject res = client.xSendPostRequest(req);
        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());

        assertThat("one sensor was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(),
                   is(equalTo(1)));
        assertThat("no service reference was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedServiceReferences(),
                   is(equalTo(0)));
        this.sensorId = resp.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);
    }

    @Test
    public void insertTestSensorFromFile1() throws XmlException, IOException {
        File f = new File(ClassLoader.getSystemResource("requests/sir/testSensor-1.xml").getFile());
        SensorMLDocument doc = SensorMLDocument.Factory.parse(f);
        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(doc.getSensorML().getMemberArray()[0].getProcess());
        XmlObject res = client.xSendPostRequest(req);
        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());

        assertThat("one sensor was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(),
                   is(equalTo(1)));
        assertThat("no service reference was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedServiceReferences(),
                   is(equalTo(0)));
        this.sensorId = resp.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);
    }

    @Test
    public void insertTestSensorFromFile() throws XmlException, IOException {
        File sensor = new File(ClassLoader.getSystemResource("requests/sir/testSensor.xml").getFile());
        SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor);

        InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
        req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(DOC.getSensorML().getMemberArray(0).getProcess());
        XmlObject res = client.xSendPostRequest(req);
        InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());

        assertThat("one sensor was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors(),
                   is(equalTo(1)));
        assertThat("no service reference was inserted",
                   resp.getInsertSensorInfoResponse().getNumberOfInsertedServiceReferences(),
                   is(equalTo(0)));
        this.sensorId = resp.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);
    }

    @After
    public void cleanUp() throws XmlException {
        DeleteSensorInfoRequestDocument req = DeleteSensorInfoRequestDocument.Factory.newInstance();
        req.addNewDeleteSensorInfoRequest().addNewInfoToBeDeleted().addNewSensorIdentification().setSensorIDInSIR(this.sensorId);

        XmlObject response = client.xSendPostRequest(req);
        DeleteSensorInfoResponseDocument responseDoc = DeleteSensorInfoResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());
    }

}

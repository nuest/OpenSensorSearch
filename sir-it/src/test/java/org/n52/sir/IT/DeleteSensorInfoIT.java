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
package org.n52.sir.IT;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import net.opengis.ows.ExceptionReportDocument;
import net.opengis.ows.impl.ExceptionReportDocumentImpl;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument.DeleteSensorInfoRequest.InfoToBeDeleted;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

/**
 * 
 * @author Yakoub
 * 
 */
public class DeleteSensorInfoIT {

    private static Client client;
    private static String sensorID;
    private static File testSensorInfo;

    @BeforeClass
    public static void setUp() {
        client = Util.configureSirClient();
        testSensorInfo = new File(ClassLoader.getSystemResource("requests/sir/InsertSensorInfo_newSensor.xml").getFile());
    }

    @Before
    public void insertSensor() throws IOException, XmlException {

        InsertSensorInfoRequestDocument doc = InsertSensorInfoRequestDocument.Factory.parse(testSensorInfo);
        XmlObject response = client.xSendPostRequest(doc);
        InsertSensorInfoResponseDocument isird = InsertSensorInfoResponseDocument.Factory.parse(response.xmlText());
        sensorID = isird.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);

        response = client.xSendGetRequest("request=DescribeSensor&service=SIR&sensorIDinSIR=" + sensorID);
        SensorMLDocument sensorDocument = SensorMLDocument.Factory.parse(response.xmlText());
        assertThat("test DescribeSensor-request returns valid document.", sensorDocument.validate(), is(equalTo(true)));
    }

    @After
    public void sensorMustBeGone() throws
            XmlException {
        XmlObject response = client.xSendGetRequest("request=DescribeSensor&service=SIR&sensorIDinSIR=" + sensorID);

        assertThat("response is an exception",
                   response.getClass().toString(),
                   is(equalTo(ExceptionReportDocumentImpl.class.toString())));
        ExceptionReportDocument report = ExceptionReportDocument.Factory.parse(response.xmlText());
        assertThat("exception is valid", report.validate(), is(equalTo(true)));
        assertThat("exception text contains id",
                   report.getExceptionReport().getExceptionArray(0).getExceptionTextArray(0),
                   containsString(sensorID));
    }

    @Test
    public void deleteReference() throws Exception {
        File f = new File(ClassLoader.getSystemResource("Requests/DeleteSensorInfo_deleteReference.xml").getFile());
        DeleteSensorInfoRequestDocument req = DeleteSensorInfoRequestDocument.Factory.parse(f);
        XmlObject response = client.xSendPostRequest(req);

        DeleteSensorInfoResponseDocument responseDoc = DeleteSensorInfoResponseDocument.Factory.parse(response.getDomNode());

        assertTrue(responseDoc.validate());

        // FIXME test must check whether the reference was actually deleted
    }

    @Test
    public void deleteSensor() throws Exception {
        File f = new File(ClassLoader.getSystemResource("requests/sir/DeleteSensorInfo.xml").getFile());
        DeleteSensorInfoRequestDocument req = DeleteSensorInfoRequestDocument.Factory.parse(f);
        req.getDeleteSensorInfoRequest().getInfoToBeDeletedArray(0).getSensorIdentification().setSensorIDInSIR(sensorID);

        XmlObject response = client.xSendPostRequest(req);

        DeleteSensorInfoResponseDocument responseDoc = DeleteSensorInfoResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());

        assertThat("one sensor was deleted.",
                   responseDoc.getDeleteSensorInfoResponse().getNumberOfDeletedSensors(),
                   is(equalTo(1)));
        assertThat("no reference was deleted.",
                   responseDoc.getDeleteSensorInfoResponse().getNumberOfDeletedServiceReferences(),
                   is(equalTo(0)));
    }

    private int countServiceReferences(DeleteSensorInfoRequestDocument isird) {
        int referenceCount = 0;

        InfoToBeDeleted[] infoArray = isird.getDeleteSensorInfoRequest().getInfoToBeDeletedArray();
        for (InfoToBeDeleted infoToBeDel : infoArray) {
            referenceCount += infoToBeDel.getServiceInfo().getServiceReferenceArray().length;
        }

        return referenceCount;
    }

}
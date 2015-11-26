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
 * author Yakoub
 */

package org.n52.sir.IT;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.n52.oss.util.XmlTools;
import org.x52North.sir.x032.DeleteSensorInfoRequestDocument;
import org.x52North.sir.x032.DeleteSensorInfoResponseDocument;
import org.x52North.sir.x032.DescribeSensorRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;
import org.xml.sax.SAXException;

public class DescribeSensorIT {

    private static Client client;

    private static AbstractProcessType expected;

    private static String sensorID;

    @BeforeClass
    public static void setup() throws XmlException, IOException {
        client = Util.configureSirClient();

        File f = new File(ClassLoader.getSystemResource("Requests/InsertSensorInfo_newSensor.xml").getFile());

        InsertSensorInfoRequestDocument doc = InsertSensorInfoRequestDocument.Factory.parse(f);
        XmlObject response = client.xSendPostRequest(doc);
        InsertSensorInfoResponseDocument isird = InsertSensorInfoResponseDocument.Factory.parse(response.xmlText());
        sensorID = isird.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);

        expected = doc.getInsertSensorInfoRequest().getInfoToBeInsertedArray()[0].getSensorDescription();
    }

    @AfterClass
    public static void deleteInsertedSensor() throws Exception {
        DeleteSensorInfoRequestDocument requestDocument = DeleteSensorInfoRequestDocument.Factory.newInstance();
        requestDocument.addNewDeleteSensorInfoRequest().addNewInfoToBeDeleted().addNewSensorIdentification().setSensorIDInSIR(sensorID);

        XmlObject response = client.xSendPostRequest(requestDocument);
        DeleteSensorInfoResponseDocument responseDoc = DeleteSensorInfoResponseDocument.Factory.parse(response.xmlText());

        boolean isValid = response.validate();
        assertTrue("valid response", isValid);

        assertThat("one sensor was deleted.",
                   responseDoc.getDeleteSensorInfoResponse().getNumberOfDeletedSensors(),
                   is(equalTo(1)));
        assertThat("correct sensor was deleted.",
                   responseDoc.getDeleteSensorInfoResponse().getDeletedSensors().getSensorIDInSIRArray(0),
                   is(equalTo(sensorID)));
    }

    @Test
    public void describeSensorWithTestFile() throws Exception {
        DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory.parse(new File(ClassLoader.getSystemResource("requests/sir/DescribeSensor.xml").getFile()));
        doc.getDescribeSensorRequest().setSensorIDInSIR(sensorID);

        XmlObject response = client.xSendPostRequest(doc);
        SensorMLDocument actual = SensorMLDocument.Factory.parse(response.getDomNode());
        checkSensor(actual);
    }

    @Test
    public void getDescribeSensor() throws Exception {
        XmlObject response = client.xSendGetRequest("request=DescribeSensor&service=SIR&sensorIDinSIR="
                + DescribeSensorIT.sensorID);

        SensorMLDocument actual = SensorMLDocument.Factory.parse(response.getDomNode());
        checkSensor(actual);
    }

    @Test
    public void postDescribeSensor() throws Exception {
        DescribeSensorRequestDocument doc = DescribeSensorRequestDocument.Factory.newInstance();
        doc.addNewDescribeSensorRequest().setSensorIDInSIR(sensorID);

        XmlObject response = client.xSendPostRequest(doc);
        SensorMLDocument actual = SensorMLDocument.Factory.parse(response.getDomNode());
        checkSensor(actual);
    }

    private void checkSensor(SensorMLDocument actual) throws SAXException, IOException {
        assertThat("Valid sensorML returned.", actual.validate(), is(true));

        Diff diff = new Diff(actual.toString(), expected.xmlText());
        System.out.println(diff);
        XMLAssert.assertXMLEqual("documents similar", diff, true);

        // assertThat("XML is similar.", diff.similar(), is(true));
        // assertThat("XML is identical.", diff.identical(), is(true));
    }

    /**
     * 
     * Something is wrong with the validation of SensorML regarding the version numbers...
     * 
     * @param sml
     * @param isValid
     */
    private void weakValidate(SensorMLDocument sml, boolean isValid) {
        if ( !isValid) {
            System.out.println("Response document is invalid, but test probably passed...\n"
                    + XmlTools.validateAndIterateErrors(sml));
        }
        else {
            assertTrue(isValid);
        }
    }
}

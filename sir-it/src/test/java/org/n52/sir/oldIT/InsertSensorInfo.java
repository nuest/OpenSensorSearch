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
package org.n52.sir.oldIT;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument.InsertSensorInfoRequest.InfoToBeInserted;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class InsertSensorInfo extends SirTest {

    @Test
    public void testPostExampleNewSensor() throws Exception {
        File f = getPostExampleFile("InsertSensorInfo_newSensor.xml");
        InsertSensorInfoRequestDocument isird = InsertSensorInfoRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(isird);

        // parse and validate response
        InsertSensorInfoResponseDocument responseDoc = InsertSensorInfoResponseDocument.Factory.parse(response.getDomNode());

        int send = isird.getInsertSensorInfoRequest().getInfoToBeInsertedArray().length;
        int inserted = responseDoc.getInsertSensorInfoResponse().getNumberOfInsertedSensors();

        SirTest.insertedSensorId = responseDoc.getInsertSensorInfoResponse().getInsertedSensors().getSensorIDInSIRArray(0);

        assertEquals(send, inserted);
        assertTrue(responseDoc.validate());
    }

    @Test
    public void testPostExampleAddReference() throws Exception {
        File f = getPostExampleFile("InsertSensorInfo_addReference.xml");
        InsertSensorInfoRequestDocument isird = InsertSensorInfoRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(isird);

        // parse and validate response
        InsertSensorInfoResponseDocument responseDoc = InsertSensorInfoResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());

        int inserted = responseDoc.getInsertSensorInfoResponse().getNumberOfInsertedServiceReferences();
        assertEquals(countServiceReferences(isird), inserted);
    }

    private int countServiceReferences(InsertSensorInfoRequestDocument isird) {
        int referenceCount = 0;

        InfoToBeInserted[] infoArray = isird.getInsertSensorInfoRequest().getInfoToBeInsertedArray();
        for (InfoToBeInserted infoToBeInserted : infoArray) {
            referenceCount += infoToBeInserted.getServiceReferenceArray().length;
        }

        return referenceCount;
    }

}
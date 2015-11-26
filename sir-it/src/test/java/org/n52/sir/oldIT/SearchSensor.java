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
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class SearchSensor extends SirTest {

    @Test
    public void testPostExampleByID() throws Exception {
        File f = getPostExampleFile("SearchSensor_bySensorIDInSIR.xml");
        SearchSensorRequestDocument req = SearchSensorRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(req);

        // parse and validate response
        SearchSensorResponseDocument responseDoc = SearchSensorResponseDocument.Factory.parse(response.getDomNode());

        int send = req.getSearchSensorRequest().getSensorIdentificationArray().length;
        int inserted = responseDoc.getSearchSensorResponse().sizeOfSearchResultElementArray();

        assertEquals(send, inserted);
        assertTrue(responseDoc.validate());
    }

    @Test
    public void testPostExampleByDescr() throws Exception {
        File f = getPostExampleFile("SearchSensor_byServiceDescription.xml");
        SearchSensorRequestDocument req = SearchSensorRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(req);

        // parse and validate response
        SearchSensorResponseDocument responseDoc = SearchSensorResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());
    }
}
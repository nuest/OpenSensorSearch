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
 * author Yakoub
 */

package org.n52.sir.IT;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorResponseDocument;

public class SearchSensorIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = Util.configureSirClient();
    }

    public SearchSensorResponseDocument searchSensor(String file) throws Exception {
        File f = new File( (ClassLoader.getSystemResource(file).getFile()));
        SearchSensorRequestDocument doc = SearchSensorRequestDocument.Factory.parse(f);

        XmlObject response = null;

        response = client.xSendPostRequest(doc);
        SearchSensorResponseDocument resp_doc = SearchSensorResponseDocument.Factory.parse(response.getDomNode());

        assertThat("response is valid (XMLBeans validate)", resp_doc.validate(), is(true));

        return resp_doc;
        // int send = doc.getSearchSensorRequest().getSensorIdentificationArray().length;
        // int response_count = resp_doc.getSearchSensorResponse().sizeOfSearchResultElementArray();
        // assertEquals(send, response_count);
    }

    @Test
    public void searchSensorbyIDInSIR() throws Exception {
        SearchSensorResponseDocument responseDocument = searchSensor("Requests/SearchSensor_bySensorIDInSIR.xml");

        // FIXME test must check if the returned sensor is the one with the used ID
    }

    @Test
    public void searchSensorbySearchCriteria() throws Exception {
        SearchSensorResponseDocument responseDocument = searchSensor("Requests/SearchSensor_bySearchCriteria.xml");

        // FIXME test must check if the returned sensor is the one with the used ID
    }

    @Test
    public void searchSensorByDescription() throws Exception {
        SearchSensorResponseDocument responseDocument = searchSensor("Requests/SearchSensor_byServiceDescription.xml");

        // FIXME test must check if the returned sensor contains the used service description
    }

}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.x52North.sir.x032.DisconnectFromCatalogRequestDocument;
import org.x52North.sir.x032.DisconnectFromCatalogResponseDocument;

/**
 * 
 * @author Yakoub
 * 
 */
public class DisconnectFromCatalogIT {

    private String catalogURL = "http://localhost:8080/ergorr/webservice";

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = Util.configureSirClient();
    }

    @Ignore("bean test is not an IT")
    @Test
    public void fromBean() throws Exception {
//        // buildRequest
//        DisconnectFromCatalogBean dfcb = new DisconnectFromCatalogBean(this.catalogURL);
//        dfcb.buildRequest();
//
//        // send request
//        String response = client.sendPostRequest(dfcb.getRequestString());
//
//        // parse and validate response
//        DisconnectFromCatalogResponseDocument responseDoc = DisconnectFromCatalogResponseDocument.Factory.parse(response);
//        assertTrue(responseDoc.validate());
//
//        assertEquals(this.catalogURL, responseDoc.getDisconnectFromCatalogResponse().getCatalogURL());
    }

    @Test
    public void fromFile() throws Exception {
        File f = new File(ClassLoader.getSystemResource("Requests/ConnectToCatalog.xml").getFile());
        DisconnectFromCatalogRequestDocument dfcrd = DisconnectFromCatalogRequestDocument.Factory.parse(f);

        String sentUrl = dfcrd.getDisconnectFromCatalogRequest().getCatalogURL();
        XmlObject response = client.xSendPostRequest(dfcrd);

        // parse and validate response
        DisconnectFromCatalogResponseDocument responseDoc = DisconnectFromCatalogResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(responseDoc.validate());

        assertEquals(sentUrl, responseDoc.getDisconnectFromCatalogResponse().getCatalogURL());
    }

}
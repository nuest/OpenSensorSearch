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
 * @author Yakoub
 */

package org.n52.oss.IT;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse.InsertedSensor;

public class HarvestingIT {

    private static Client client;

    private String serviceURL = "http://sensorweb.demo.52north.org/EO2HeavenSOS/sos";

    private String serviceType = "SOS";

//    private SOLRSearchSensorDAO dao;

    @BeforeClass
    public static void setUp() {
        client = Util.configureSirClient();
    }

    @Before
    public void prepare() {
//        SolrConnection c = new SolrConnection("http://localhost:8983/solr", 2000);
//        this.dao = new SOLRSearchSensorDAO(c);
    }

    @Test
    public void harvestService() throws IOException,
            XmlException,
            URISyntaxException {
        File f = new File(ClassLoader.getSystemResource("Requests/HarvestService_WeatherSOS.xml").getFile());
        HarvestServiceRequestDocument doc = HarvestServiceRequestDocument.Factory.parse(f);
        XmlObject resp = client.xSendPostRequest(doc, new URI(this.serviceURL));
        System.out.println(resp);

        HarvestServiceResponseDocument respDoc = HarvestServiceResponseDocument.Factory.parse(resp.getDomNode());
        InsertedSensor[] sensors = respDoc.getHarvestServiceResponse().getInsertedSensorArray();

        for (int i = 0; i < sensors.length; i++) {
            String id = sensors[i].getSensorIDInSIR();
            // FIXME use HTTP client to search Solr
            Collection<SirSearchResultElement> elements = Collections.EMPTY_LIST; // this.dao.searchByID(id);
            assertTrue(elements.size() > 0);
        }
    }
}

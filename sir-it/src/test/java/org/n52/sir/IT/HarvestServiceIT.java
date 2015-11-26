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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument;

/**
 * 
 * @author Yakoub, Daniel Nüst
 */
public class HarvestServiceIT {

    // FIXME use a mocked up webservice to test harvesting
    private String harvestedServiceURL = "http://geoviqua.dev.52north.org/SOS-Q/sos/pox";

    private static String sirServiceURL = "http://localhost:8080/oss-service/sir";

    private String harvestedServiceType = "SOS";

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = new Client(sirServiceURL);
    }

    @Test
    public void harvestWeatherServiceDoc() throws Exception {
        File f = new File(ClassLoader.getSystemResource("requests/HarvestService.xml").getFile());
        HarvestServiceRequestDocument hsrd = HarvestServiceRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(hsrd);

        // parse and validate response
        HarvestServiceResponseDocument cd = HarvestServiceResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(cd.validate());

        // FIXME test must check whether the correct number of sensors was added, and more
    }

}
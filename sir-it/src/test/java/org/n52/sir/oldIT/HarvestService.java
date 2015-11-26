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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument;

/**
 * 
 * @author Daniel Nüst
 * 
 */
public class HarvestService extends SirTest {

    private String serviceURL = "http://v-swe.uni-muenster.de:8080/WeatherSOS/sos";

    private String serviceType = "SOS";

    @Test
    public void testPostExample() throws Exception {
        File f = getPostExampleFile("HarvestService_WeatherSOS.xml");
        HarvestServiceRequestDocument hsrd = HarvestServiceRequestDocument.Factory.parse(f);

        XmlObject response = client.xSendPostRequest(hsrd);

        // parse and validate response
        HarvestServiceResponseDocument cd = HarvestServiceResponseDocument.Factory.parse(response.getDomNode());
        assertTrue(cd.validate());
    }

}
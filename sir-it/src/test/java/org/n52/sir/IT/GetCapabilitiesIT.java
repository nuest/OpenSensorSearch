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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.x52North.sir.x032.CapabilitiesDocument;
import org.x52North.sir.x032.GetCapabilitiesDocument;
import org.xml.sax.SAXException;

public class GetCapabilitiesIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = Util.configureSirClient();
//        GuiceUtil.configureSirConfigurator(); // required by XmlTools used in bean to build the request
    }

    @Test
    public void postRequest() throws IOException, XmlException, SAXException {
        File f = new File(ClassLoader.getSystemResource("Requests/GetCapabilities.xml").getFile());

        GetCapabilitiesDocument doc = GetCapabilitiesDocument.Factory.parse(f);
        XmlObject response = client.xSendPostRequest(doc);
        CapabilitiesDocument actual = CapabilitiesDocument.Factory.parse(response.getDomNode());

        checkCapabilities(actual);
    }

    @Test
    public void getRequest() throws IOException, XmlException, SAXException {
        XmlObject response = client.xSendGetRequest("request=GetCapabilities&service=SIR");
        CapabilitiesDocument actual = CapabilitiesDocument.Factory.parse(response.getDomNode());

        checkCapabilities(actual);
    }

    private void checkCapabilities(CapabilitiesDocument actual) throws XmlException, IOException, SAXException {
        assertThat("Document is valid according to XMLBeans.", actual.validate(), is(true));

        File f = new File(ClassLoader.getSystemResource("responses/sir/capabilities.xml").getFile());
        CapabilitiesDocument expected = CapabilitiesDocument.Factory.parse(f);

        Diff diff = new Diff(actual.getCapabilities().getOperationsMetadata().toString(),
                             expected.getCapabilities().getOperationsMetadata().toString());

        XMLAssert.assertXMLEqual(diff, true);

        // assertThat("XML is similar.", diff.similar(), is(true));
        // assertThat("XML is identical.", diff.identical(), is(true));
    }
}

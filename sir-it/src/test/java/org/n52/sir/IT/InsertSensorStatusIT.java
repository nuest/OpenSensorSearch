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

package org.n52.sir.IT;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.x52North.sir.x032.InsertSensorStatusRequestDocument;
import org.x52North.sir.x032.InsertSensorStatusResponseDocument;

public class InsertSensorStatusIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = Util.configureSirClient();
    }

    @Test
    public void insertSensorStatus() throws XmlException, IOException {
        File sensor_status = new File(ClassLoader.getSystemResource("Requests/InsertSensorStatus.xml").getFile());
        InsertSensorStatusRequestDocument request = InsertSensorStatusRequestDocument.Factory.parse(sensor_status);

        XmlObject res = client.xSendPostRequest(request);

        InsertSensorStatusResponseDocument response = InsertSensorStatusResponseDocument.Factory.parse(res.getDomNode());

        String expected = request.getInsertSensorStatusRequest().getStatusDescription().getSensorIDInSIR();
        String actual = response.getInsertSensorStatusResponse().getSensorIDInSIR();

        assertThat("sensor IDs equal", actual, is(equalTo(expected)));
    }

    @After
    public void cleanUp() {
        // TODO remove the inserted status again
    }

}

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
package org.n52.sir.json;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializeSensorDescription {

    private static File f;
    private ObjectMapper mapper;
    private ByteArrayOutputStream baos;
    private SimpleSensorDescription expected;

    @BeforeClass
    public static void prepare() {
        f = new File(SerializeSensorDescription.class.getResource("/sensordescription.json").getFile());
    }

    @Before
    public void setUp() throws Exception {
        this.mapper = MapperFactory.getMapper();
        this.baos = new ByteArrayOutputStream();
        this.expected = this.mapper.readValue(f, SimpleSensorDescription.class);
    }

    @Test
    public void mappingMatchesTestFile() throws Exception {
        SimpleSensorDescription sd = TestObjectGenerator.getSensorDescription();
        this.mapper.writeValue(this.baos, sd);

        try (BufferedReader br = new BufferedReader(new FileReader(f));) {
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ( (line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONAssert.assertEquals(sb.toString(), new String(this.baos.toByteArray()), false);
        }

    }
}

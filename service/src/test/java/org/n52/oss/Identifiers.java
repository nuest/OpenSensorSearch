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
package org.n52.oss;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.n52.oss.id.IdentifierGenerator;
import org.n52.oss.id.ShortAlphanumericIdentifierGenerator;

public class Identifiers {

    @Test
    public void identifiersAreGenerated() {
        IdentifierGenerator gen = new ShortAlphanumericIdentifierGenerator();
        String id0 = gen.generate();
        System.out.println("Generated id: " + id0);
        String id1 = gen.generate();
        System.out.println("Generated id: " + id1);
        String id2 = gen.generate();
        System.out.println("Generated id: " + id2);

        assertThat(id0, not(equalTo(id1)));
        assertThat(id0, not(equalTo(id2)));
        assertThat(id1, not(equalTo(id2)));

        assertTrue(StringUtils.isAlphanumeric(id0));
        assertTrue(StringUtils.isAlphanumeric(id1));
        assertTrue(StringUtils.isAlphanumeric(id2));

        assertTrue(StringUtils.isAllLowerCase(id0.replaceAll("[\\d.]", "")));
        assertTrue(StringUtils.isAllLowerCase(id1.replaceAll("[\\d.]", "")));
        assertTrue(StringUtils.isAllLowerCase(id2.replaceAll("[\\d.]", "")));
    }

    @Test
    public void listOfIdentifiersIsGenerated() {
        IdentifierGenerator gen = new ShortAlphanumericIdentifierGenerator();
        int size = 10;
        Collection<String> ids = gen.generate(size);
        System.out.println("Generated ids: " + Arrays.toString(ids.toArray()));

        assertThat(ids, hasSize(size));
    }

}

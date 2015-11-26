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

package org.n52.oss.sir.xml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.GuiceUtil;
import org.n52.sir.xml.ValidationResult;
import org.n52.sir.xml.impl.SensorML4DiscoveryValidatorImpl;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class Validator {

    private static String schematronFile;

    private static String svrlFile;

    @BeforeClass
    public static void prepare() {
        Injector i = GuiceUtil.configurePropertiesFiles();
        schematronFile = i.getInstance(Key.get(String.class, Names.named("oss.sir.validation.profile.sml.discovery")));
        svrlFile = i.getInstance(Key.get(String.class, Names.named("oss.sir.validation.svrlSchema")));
    }

    @Test
    public void testAirBase() throws OwsExceptionReport, URISyntaxException {
        File f = new File(getClass().getResource("/AirBase-test.xml").getFile());

        SensorML4DiscoveryValidatorImpl validator = new SensorML4DiscoveryValidatorImpl(schematronFile, svrlFile);

        ValidationResult vr = validator.validate(f);
        assertThat("tested file is valid", vr.isValidated(), is(true));
    }

    @Test
    public void testInvalidAirBase() throws OwsExceptionReport, URISyntaxException {
        File f = new File(getClass().getResource("/AirBase-test-invalid.xml").getFile());

        SensorML4DiscoveryValidatorImpl validator = new SensorML4DiscoveryValidatorImpl(schematronFile, svrlFile);

        ValidationResult vr = validator.validate(f);
        assertThat("tested file is INvalid", vr.isValidated(), is(false));
        Collection<String> validationFailures = vr.getValidationFailures();
        assertThat(validationFailures.size(), is(2));
        assertThat(Arrays.toString(validationFailures.toArray()), containsString("gml:description"));
        assertThat(Arrays.toString(validationFailures.toArray()), containsString("sml:validTime"));
    }
}

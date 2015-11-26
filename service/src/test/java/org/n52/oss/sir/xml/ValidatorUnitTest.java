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

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.junit.Test;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.xml.impl.SensorML4DiscoveryValidatorImpl;

/*
 * The file name to be changed to ValidatorTest
 */
public class ValidatorUnitTest {
	@Test
    public void readFile() throws TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException,
            OwsExceptionReport {

        File f = new File(ClassLoader.getSystemResource("AirBase-test.xml").getFile());

        // Read schema
        File schematronFile = new File(ClassLoader.getSystemResource("SensorML_Profile_for_Discovery.sch").getFile());
        // Read svrl
        File svrlFile = new File(ClassLoader.getSystemResource("xslt/iso_svrl_for_xslt2.xsl").getFile());

        // Now validate
        SensorML4DiscoveryValidatorImpl validator;

//        validator = new SensorML4DiscoveryValidatorImpl(schematronFile, svrlFile);
  //      boolean v = validator.validate(f);
    //    assertTrue(v);
    }
}

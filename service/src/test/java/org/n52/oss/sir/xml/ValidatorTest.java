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
package org.n52.oss.sir.xml;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 * 
 */
@SuppressWarnings("unused")
public class ValidatorTest {

    private static final Logger log = LoggerFactory.getLogger(ValidatorTest.class);

    public static void main(String[] args) throws OwsExceptionReport,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException {
        // test01();
        // testIfgicam();

        testAirBase();
    }

    private static void testAirBase() throws TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException,
            OwsExceptionReport {
        // File f = new
        // File("D:/52n/OpenSensorSearch/52n-sir/target/test-classes/transformation/AirBase-test.xml");

        // validateFileToConsole(f);
    }

    private static void testIfgicam() throws OwsExceptionReport,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException {
        // File f = new
        // File("D:/52n/OpenSensorSearch/52n-sir/target/test-classes/transformation/ifgicam-discoveryprofile.xml");

        // validateFileToConsole(f);
    }

    /**
     * @param f
     * @throws TransformerConfigurationException
     * @throws TransformerFactoryConfigurationError
     * @throws ParserConfigurationException
     * @throws OwsExceptionReport
     */
    private static void validateFileToConsole(File f) throws TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException,
            OwsExceptionReport {
        // File schematronFile = new
        // File("D:/52n/OpenSensorSearch/52n-sir/target/SIR/WEB-INF/classes/SensorML_Profile_for_Discovery.sch");
        // File svrlFile = new
        // File("D:/52n/OpenSensorSearch/52n-sir/target/SIR/WEB-INF/classes/xslt/iso_svrl_for_xslt2.xsl");
        // SensorML4DiscoveryValidatorImpl validator = new SensorML4DiscoveryValidatorImpl(schematronFile,
        // svrlFile);

        // boolean v = validator.validate(f);

        /*
         * System.out.println("Patterns:"); for (String string : validator.getActivatedPatterns()) {
         * System.out.println(string); } System.out.println("Rules:"); for (String string :
         * validator.getFiredRules()) { System.out.println(string); }
         * 
         * if ( !v) { System.out.println("Failures:"); for (String string : validator.getValidationFailures())
         * { System.out.println(string); } } else { System.out.println("VALID!!!11"); }
         */
    }

    private static void test01() throws OwsExceptionReport,
            TransformerConfigurationException,
            TransformerFactoryConfigurationError,
            ParserConfigurationException {
        /*
         * File schematronFile = new
         * File("/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/SensorML_Profile_for_Discovery.sch"); File
         * svrlFile = new File("/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/iso_svrl_for_xslt2.xsl");
         * 
         * SensorML4DiscoveryValidatorImpl validator = new SensorML4DiscoveryValidatorImpl(schematronFile,
         * svrlFile);
         * 
         * boolean v = validator.validate(new
         * File("/home/daniel/workspace/SIR/data/transformation/SensorML_Profile_for_Discovery_Example.xml"));
         * if ( !v) { for (String string : validator.getValidationFailures()) { System.out.println(string); }
         * } else { System.out.println("VALID!!!11"); }
         * 
         * v = validator.validate(new
         * File("/home/daniel/workspace/SIR/data/transformation/IFGI_HWS1-discoveryprofile.xml"));
         * 
         * if ( !v) { for (String string : validator.getValidationFailures()) { System.out.println(string); }
         * } else { System.out.println("VALID!!!11"); }
         */
    }

    /**
     * 
     * @param inputFile
     * @param transformationDir
     * @param dataDir
     * @throws InstantiationError
     */
    private static void test02() throws InstantiationError {
        /*
         * String inputFile =
         * "D:/52n/OpenSensorSearch/52n-sir/target/test-classes/transformation/IFGI_HWS1-discoveryprofile.xml"
         * ; log.info("Transforming " + inputFile);
         * 
         * try { Transformer transformer = TransformerFactory.newInstance().newTransformer(new
         * StreamSource("/home/daniel/workspace/SIR/data/discovery.xsl"));
         * 
         * // test the input document FileReader inputReader = new FileReader(inputFile); SensorMLDocument
         * smlDoc = SensorMLDocument.Factory.parse(inputReader);
         * log.info(XmlTools.validateAndIterateErrors(smlDoc));
         * 
         * // encapsulate input document in a Source Source input = new DOMSource(smlDoc.getDomNode());
         * 
         * // create output string StringWriter sw = new StringWriter(); StreamResult output = new
         * StreamResult(sw);
         * 
         * // do the transformation transformer.transform(input, output);
         * 
         * // create output document String outputString = output.getWriter().toString();
         * 
         * System.out.println(outputString);
         * 
         * // clean up input = null; sw = null; output = null; outputString = null; } catch
         * (FileNotFoundException e) { log.error("", e); } catch (TransformerException e) { log.error("", e);
         * } catch (IOException e) { log.error("", e); } catch (XmlException e) { log.error("", e); }
         */
    }

}

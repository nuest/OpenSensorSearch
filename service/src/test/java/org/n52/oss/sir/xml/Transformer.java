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

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.util.GuiceUtil;
import org.n52.oss.util.XmlTools;
import org.n52.sir.xml.impl.SMLtoEbRIMTransformer;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageType;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class Transformer {

    private static String xsltDir;

    private void failIfURLNull(String resource) {
        if (ClassLoader.getSystemResource(resource) == null)
            fail(resource + " Is missing");
    }

    @BeforeClass
    public static void prepare() {
        Injector i = GuiceUtil.configurePropertiesFiles();
        xsltDir = i.getInstance(Key.get(String.class, Names.named("oss.transform.xsltDir")));
    }

    @Test
    public void testTransform() {
        String[] s = new String[] {"IFGI_HWS1-discoveryprofile.xml",
                                   "FH_HWS1-discoveryprofile.xml",
                                   "FH_HWS1-discoveryprofile.xml"};

        for (String str : s)
            failIfURLNull("transformation/" + str);

        File transformations = new File(ClassLoader.getSystemResource("transformation/").getFile());

        for (int i = 0; i < s.length; i++) {
            File file = new File(ClassLoader.getSystemResource("transformation/" + s[i]).getFile());
            testTransformation(file.getName(), xsltDir, transformations.getAbsolutePath() + "/");
        }

    }

    private static void testTransformation(String inputFile, String transformationDir, String dataDir) throws InstantiationError {

        SMLtoEbRIMTransformer transformer = new SMLtoEbRIMTransformer(transformationDir, false);

        try {
            // test the input document
            FileReader inputReader = new FileReader(dataDir + inputFile);
            SensorMLDocument smlDoc = SensorMLDocument.Factory.parse(inputReader);

            transformer.setValidatingInputAndOutput(false);

            Result r = transformer.transform(dataDir + inputFile);
            StreamResult sr = (StreamResult) r;

            String outputString = sr.getWriter().toString();

            RegistryPackageDocument rpd = RegistryPackageDocument.Factory.parse(outputString);
            RegistryPackageType rp = rpd.getRegistryPackage();

            String eoInfo = XmlTools.inspect(rp);
        }
        catch (Exception e) {
            fail(e.toString());
        }
    }

}

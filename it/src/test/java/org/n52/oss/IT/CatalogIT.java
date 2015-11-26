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
package org.n52.oss.IT;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.catalog.csw.CswFactory;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.ITransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CatalogIT {

    private static Logger log = LoggerFactory.getLogger(CatalogIT.class);

    private static void getCap() {
        URL url;
        try {
            url = new URL("http://localhost:8080/ergorr/webservice");
        }
        catch (MalformedURLException e1) {
            e1.printStackTrace();
            return;
        }

        String classInit = "/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/sirClassificationInit.xml, /home/daniel/workspace/SIR/WebContent/WEB-INF/conf/ISO19119-Services-Scheme.xml";
        String slotInit = "/home/daniel/workspace/SIR/WebContent/WEB-INF/conf/sirSlotInit.xml";

        ICatalogFactory factory;
        ISearchSensorDAO dao = null;
        Set<ITransformer> transformers = null;
        Set<IProfileValidator> vals = null;
        try {
            factory = new CswFactory(classInit, slotInit, "http://doNotCheck.url", transformers, dao, vals, true);
        }
        catch (XmlException e) {
            log.error("Could not parse classification scheme file!", e);
            return;
        }
        catch (IOException e) {
            log.error("Could not read classification scheme file!", e);
            return;
        }

        ICatalog client;
        try {
            client = factory.getCatalog(url);
        }
        catch (OwsExceptionReport e1) {
            e1.printStackTrace();
            return;
        }

        log.debug(client.toString());
        boolean b;
        try {
            b = client.ensureSufficientCapabilities();
            log.debug("Are capabilities sufficient? " + b);
        }
        catch (OwsExceptionReport e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException {

        getCap();

        System.exit(0);
    }
}

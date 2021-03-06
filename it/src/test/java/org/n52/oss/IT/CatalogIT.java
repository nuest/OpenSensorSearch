/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

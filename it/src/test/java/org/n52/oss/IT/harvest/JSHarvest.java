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
package org.n52.oss.IT.harvest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;

public class JSHarvest {
    @Test
    public void harvestJSFile() throws SolrServerException, IOException {
        File harvestScript = new File(ClassLoader.getSystemResource("harvest/harvestScript.js").getFile());

        IJSExecute execEngine = new RhinoJSExecute();
        String id = execEngine.execute(harvestScript);

        assertNotNull(id);

        SolrConnection c = new SolrConnection("http://localhost:8983/solr", 2000);
        SOLRSearchSensorDAO dao = new SOLRSearchSensorDAO(c);
        Collection<SirSearchResultElement> elements = dao.searchByID(id);
        assertTrue(elements.size() != 0);

        SirSearchResultElement element = elements.iterator().next();
        SirDetailedSensorDescription description = (SirDetailedSensorDescription) element.getSensorDescription();
        Collection<String> keywords = description.getKeywords();

        assertTrue(keywords.contains("javascript"));
        assertTrue(keywords.contains("harvest"));

        assertTrue(description.getLocation().equals("3,1.5"));

        Collection<String> contacts = description.getContacts();

        assertTrue(contacts.contains("52north"));
        assertTrue(contacts.contains("rhino"));

        new SolrConnection("http://localhost:8983/solr", 2000).deleteSensor("id:" + id);
    }

}

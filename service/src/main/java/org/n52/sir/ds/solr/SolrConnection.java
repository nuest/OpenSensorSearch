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
package org.n52.sir.ds.solr;

/** 
 * @author Yakoub 
 */
import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SolrConnection {

    private static Logger log = LoggerFactory.getLogger(SolrConnection.class);

    private HttpSolrServer server;

    private int timeoutMillis;

    @Inject
    public SolrConnection(@Named("oss.solr.url")
    String url, @Named("oss.solr.timeoutMillis")
    int timeout) {
        this.timeoutMillis = timeout;
        this.server = new HttpSolrServer(url);
        this.server.setConnectionTimeout(this.timeoutMillis);

        System.out.println(this.server);

        log.info("NEW {} for URL {} with timeout {}", this, url, timeout);
    }

    public void addInputDocument(SolrInputDocument doc) throws SolrServerException, IOException {
        this.server.add(doc);
    }

    public void commitChanges() throws SolrServerException, IOException {
        this.server.commit();
    }

    public QueryResponse query(SolrParams params) throws SolrServerException {
        return this.server.query(params);
    }

    public void deleteSensorWithID(String sensorID) throws SolrServerException, IOException {
        this.server.deleteByQuery(SolrConstants.ID + ":" + sensorID);
        commitChanges();
    }

    public void deleteSensor(String query) throws SolrServerException, IOException {
        this.server.deleteByQuery("*:*");
        commitChanges();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SolrConnection [");
        if (this.server != null) {
            builder.append("serverBaseURL=");
            builder.append(this.server.getBaseURL());
            builder.append(", server=");
            builder.append(this.server);
        }
        builder.append(", timeoutMillis=");
        builder.append(this.timeoutMillis);
        builder.append("]");
        return builder.toString();
    }

}

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
package org.n52.oss.ui.beans;

import org.n52.oss.sir.Client;
import org.n52.oss.ui.WebsiteConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public abstract class TestClientBean {

    private static Logger log = LoggerFactory.getLogger(TestClientBean.class);

    protected String requestString = "";

    protected String responseString = "";

    private Client client;

    public TestClientBean() {
        WebsiteConfig c = new WebsiteConfig();
        String sirEndpoint = c.getSirEndpoint();
        this.client = new Client(sirEndpoint);
        log.info("NEW {}", this);
    }

    /**
     * Build the request based on the user input, then save it in {@link TestClientBean#requestString}.
     */
    public abstract void buildRequest();

    public String getRequestString() {
        return this.requestString;
    }

    public String getResponseString() {
        return this.responseString;
    }

    public void setRequestString(String request) {
        this.requestString = request;
    }

    public void setResponseString(String response) {
        this.responseString = response;
    }

    public String sendRequest(String request) {
        log.debug("Sending request: {}", request);

        String response;
        response = this.client.sendPostRequest(request);

        log.debug("Got response: {}", response);
        setResponseString(response);
        return response;
    }

}

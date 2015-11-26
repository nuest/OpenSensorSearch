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
package org.n52.oss.ui.beans;

import org.n52.oss.util.XmlTools;
import org.x52North.sir.x032.ConnectToCatalogRequestDocument;
import org.x52North.sir.x032.ConnectToCatalogRequestDocument.ConnectToCatalogRequest;


/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class ConnectToCatalogBean extends TestClientBean {

    private String catalogUrl = "";

    private int pushInterval = 0;

    /**
     * 
     */
    public ConnectToCatalogBean() {
        //
    }

    /**
     * @param catalogUrl
     * @param pushInterval
     */
    public ConnectToCatalogBean(String catalogUrl, int pushInterval) {
        this.catalogUrl = catalogUrl;
        this.pushInterval = pushInterval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.sir.client.beans.IBean#buildRequest()
     */
    @Override
    public void buildRequest() {
        this.responseString = "";

        ConnectToCatalogRequestDocument requestDoc = ConnectToCatalogRequestDocument.Factory.newInstance();
        ConnectToCatalogRequest request = requestDoc.addNewConnectToCatalogRequest();
        request.setService(ClientConstants.SERVICE_NAME);
        request.setVersion(ClientConstants.getServiceVersionEnum());

        // cswURL
        if (this.catalogUrl != null && !this.catalogUrl.equals("")) {
            request.setCatalogURL(this.catalogUrl);
        }
        else {
            this.requestString = "<!-- Catalog URL is mandatory! -->";
            return;
        }

        // pushInterval
        if (this.pushInterval > 0) {
            request.setPushIntervalSeconds(this.pushInterval);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if (requestDoc.validate())
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
        else
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
    }

    /**
     * @return the cswUrl
     */
    public String getCatalogUrl() {
        return this.catalogUrl;
    }

    /**
     * @return the pushIntervalSeconds
     */
    public int getPushInterval() {
        return this.pushInterval;
    }

    /**
     * @param cswUrl
     *        the cswUrl to set
     */
    public void setCatalogUrl(String cswUrl) {
        this.catalogUrl = cswUrl;
    }

    /**
     * @param pushIntervalSeconds
     *        the pushIntervalSeconds to set
     */
    public void setPushInterval(int pushInterval) {
        this.pushInterval = pushInterval;
    }

}

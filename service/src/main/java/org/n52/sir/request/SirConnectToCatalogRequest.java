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
package org.n52.sir.request;

import java.net.URL;

import org.n52.sir.catalog.ICatalogConnection;

/**
 * Internal request to connect to a CSW catalog service
 * 
 * @author Jan Schulte
 * 
 */
public class SirConnectToCatalogRequest extends AbstractSirRequest {

    /**
     * url of the catalog service
     */
    private URL cswUrl;

    /**
     * update interval in seconds. Default value is @see {@link ICatalogConnection#NO_PUSH_INTERVAL}.
     */
    private int pushInterval = ICatalogConnection.NO_PUSH_INTERVAL;

    /**
     * @return the cswUrl
     */
    public URL getCswUrl() {
        return this.cswUrl;
    }

    /**
     * @return the pushInterval
     */
    public int getPushInterval() {
        return this.pushInterval;
    }

    /**
     * @param cswUrl
     *        the cswUrl to set
     */
    public void setCswUrl(URL cswUrl) {
        this.cswUrl = cswUrl;
    }

    /**
     * @param pushInterval
     *        the pushInterval to set
     */
    public void setPushInterval(int pushInterval) {
        this.pushInterval = pushInterval;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ConnectToCatalogRequest: ");
        sb.append("CswURL: " + this.cswUrl);
        sb.append(", PushInterval: " + this.pushInterval);
        return sb.toString();
    }
}

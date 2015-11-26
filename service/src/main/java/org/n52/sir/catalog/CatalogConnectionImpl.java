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
package org.n52.sir.catalog;

import java.net.URL;


/**
 * 
 * Class encapsulates all inormation that is required to start and also persistently store a connection to a
 * catalog.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CatalogConnectionImpl implements ICatalogConnection {

    private URL catalogURL;
    private String connectionID;
    private int pushIntervalSeconds;
    private String status;

    public CatalogConnectionImpl(String connectionID, URL url, int pushInterval, String connectionStatus) {
        this.connectionID = connectionID;
        this.catalogURL = url;
        this.pushIntervalSeconds = pushInterval;
        this.status = connectionStatus;
    }

    @Override
    public URL getCatalogURL() {
        return this.catalogURL;
    }

    @Override
    public String getConnectionID() {
        return this.connectionID;
    }

    @Override
    public int getPushIntervalSeconds() {
        return this.pushIntervalSeconds;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CatalocConnection [connectionID=");
        sb.append(this.connectionID);
        sb.append(", pushIntervalSeconds=");
        sb.append(this.pushIntervalSeconds);
        sb.append(", catalogURL=");
        sb.append(this.catalogURL);
        sb.append(", status=");
        sb.append(this.status);
        sb.append("]");
        return sb.toString();
    }

}

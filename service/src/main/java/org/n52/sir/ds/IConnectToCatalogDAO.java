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
package org.n52.sir.ds;

import java.net.URL;
import java.util.List;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.ICatalogConnection;

/**
 * @author Jan Schulte
 *
 */
public interface IConnectToCatalogDAO {

    /*
     * Returns a list of all catalogs that have a push interval
     */
    List<ICatalogConnection> getCatalogConnectionList() throws OwsExceptionReport;

    /**
     * checks in database if connection exists. If exists, the ConnectionID is returned, else null
     *
     * @param url the catalog endpoint
     * @param pushInterval the repetition interval in seconds
     * @return Returns the catalog ID in SIR or null, if not exists
     * @throws OwsExceptionReport on any error
     */
    String getConnectionID(URL url, int pushInterval) throws OwsExceptionReport;

    /**
     * insert a connection to a catalog service
     *
     * @param cswUrl url to the catalog service
     * @param pushInterval update interval
     * @return Returns the catalog ID in SIR
     * @throws OwsExceptionReport on any error
     */
    String insertConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport;

    /**
     * update the connection in database
     *
     * @param cswUrl url to the catalog service
     * @param pushInterval update interval
     * @throws OwsExceptionReport on any error
     */
    void updateConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport;

}

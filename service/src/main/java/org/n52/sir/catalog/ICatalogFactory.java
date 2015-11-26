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
package org.n52.sir.catalog;

import java.net.URL;

import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * 
 * Factory for objects that access catalogue services, {@link ICatalog}. The created catalogs use the url that
 * is available via {@link ICatalogFactory#getCatalogUrl()} as their endpoint.
 * 
 * The method {@link ICatalogFactory#getCatalogConnection(String, URL, int, String)} shall be used to create
 * {@link ICatalogConnection} instances that contain the neccessary parameters to create and save a connection
 * to a catalog.
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 * 
 */
public interface ICatalogFactory {

    /**
     * 
     * @param url
     *        the endpoint the catalog must use
     * @return a new instance of an ICatalog
     * @throws OwsExceptionReport
     */
    public abstract ICatalog getCatalog(URL url) throws OwsExceptionReport;

    /**
     * 
     * Method creates objects that encapsulate all information that is needed to persitently save a connection
     * to a catalog.
     * 
     * @param connectionID
     * @param url
     * @param pushInterval
     * @param newConnectionStatus
     * @return
     */
    public abstract ICatalogConnection getCatalogConnection(String connectionID,
                                                            URL url,
                                                            int pushInterval,
                                                            String newConnectionStatus);

}

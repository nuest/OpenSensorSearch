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
 * Class encapsulates all information for persistent storage of a connection to a catalog.
 * 
 * If the connetion is only scheduled for single execution the parameters
 * {@link ICatalogConnection#NO_PUSH_INTERVAL} and {@link ICatalogConnection#UNSAVED_CONNECTION_ID} shall be
 * used for instantiation.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface ICatalogConnection {

    /**
     * The string that is written into the status field if there is no real status yet.
     */
    public static final String NEW_CONNECTION_STATUS = "NEW";

    /**
     * Push interval value if not push interval is set.
     */
    public static final int NO_PUSH_INTERVAL = Integer.MIN_VALUE;

    /**
     * String that is used as the connectionID for unsaved connections.
     */
    public static final String UNSAVED_CONNECTION_ID = "[unsaved_connection!]";

    /**
     * 
     * @return The URL pointing to the catalog service
     */
    public abstract URL getCatalogURL();

    /**
     * 
     * @return The internal identifier, can be used to cancel a connection
     */
    public abstract String getConnectionID();

    /**
     * 
     * This parameter is optional. It can be checked via comparison with {@link #NO_PUSH_INTERVAL}.
     * 
     * @return The temporal interval at which updates are sent to this catalog in seconds
     */
    public abstract int getPushIntervalSeconds();

    /**
     * 
     * @return A textual description of the connection's status. This should be human readable.
     */
    public abstract String getStatus();
}

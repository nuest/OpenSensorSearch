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

import java.util.Collection;

/**
 * 
 * Interface for viewing the status of a saved catalog connection.
 * 
 * TODO move this into a module and as a service endpoint /catalog
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 * 
 */
public interface ICatalogStatusHandler {

    /**
     * If the catalog connections are executed in different threads this identifier shall be used to
     * save/access the implementation.
     */
    public static final String NAME_IN_CONTEXT = "CatalogStatusHandler";

    /**
     * 
     * @return The maximum number of events that are available.
     */
    public int getMaximumInfolistSize();

    /**
     * 
     * Returns a list of Strings that give information about the most recent catalog status events that
     * happened during runtime.
     * 
     * @return a collection of status descriptions
     */
    public abstract Collection<String> getRuntimeInfo();

    /**
     * 
     * Updates the status of the catalog saved under the given identifier with the given message and saves the
     * information for runtime querying (the former only if the push is scheduled for repetition).
     * 
     * @param identifier
     * @param statusMessage
     */
    public abstract void setStatus(String identifier, String statusMessage);

}

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
package org.n52.sir.catalog.csw;

import java.util.HashMap;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.IdentifiableType;

/**
 * 
 * Class caches {@link IdentifiableType} instances that were queried from a catalog. This can be used to avoid
 * repeated querying during the process of updating a transformed document prior to insertion. For example, a
 * classificatio node that should not be inserted again because it is already present in the catalog can be
 * cached here to avoid requesting it again from the catalog. Attention: The equality check is purely based on
 * the attribute "id" of {@link IdentifiableType}.
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 * 
 */
public class CswCatalogCache {

    private HashMap<String, IdentifiableType> cache;

    public CswCatalogCache() {
        this.cache = new HashMap<String, IdentifiableType>();
    }

    /**
     * 
     * @param cnt
     */
    public void add(IdentifiableType iT) {
        this.cache.put(iT.getId(), iT);
    }

    /**
     * 
     * Uses the id of the given identifiable to check against the cached identifiables.
     * 
     * @param identifiableType
     * @return true if an identifiable with the same id is already cached.
     */
    public boolean contains(IdentifiableType iT) {
        return this.cache.containsKey(iT.getId());
    }

}

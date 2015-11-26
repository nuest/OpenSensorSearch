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

import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.ICatalogStatusHandlerDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 *
 * Implementation of {@link ICatalogStatusHandler} that utilizes a database (via an instance of
 * {@link ICatalogStatusHandlerDAO}) to save the status description as a simple string.
 *
 * @author Jan Schulte, Daniel Nüst
 *
 */
public class CatalogStatusHandlerImpl implements ICatalogStatusHandler {

    private static final Logger log = LoggerFactory.getLogger(CatalogStatusHandlerImpl.class);

    private static final int MAXIMUM_INFOLIST_SIZE = 100;

    private ICatalogStatusHandlerDAO catStatHandlerDao;

    private ArrayList<String> runtimeInfo;

    @Inject
    public CatalogStatusHandlerImpl(ICatalogStatusHandlerDAO dao) {
        this.catStatHandlerDao = dao;
        this.runtimeInfo = new ArrayList<>();
    }

    @Override
    public int getMaximumInfolistSize() {
        return MAXIMUM_INFOLIST_SIZE;
    }

    @Override
    public Collection<String> getRuntimeInfo() {
        ArrayList<String> infolist = new ArrayList<>(this.runtimeInfo);
        if (infolist.size() == MAXIMUM_INFOLIST_SIZE) {
            infolist.add(0, "(Status information of the last " + MAXIMUM_INFOLIST_SIZE + " events only.)");
            infolist.add(1, " ");
        }
        else if (infolist.isEmpty()) {
            infolist.add("No events logged yet.");
        }
        return infolist;
    }

    private void saveRuntimeInfo(String identifier, String statusMessage) {
        if (this.runtimeInfo.size() == MAXIMUM_INFOLIST_SIZE) {
            this.runtimeInfo.remove(0);
        }

        this.runtimeInfo.add("Connection id: " + identifier + "\t>>>\t" + statusMessage);
    }

    @Override
    public void setStatus(String identifier, String statusMessage) {
        if (identifier.equals(ICatalogConnection.UNSAVED_CONNECTION_ID)) {
            log.info("* STATUS CHANGE FOR UNSAVED CONNECTION: " + statusMessage + " *");
            saveRuntimeInfo(identifier, statusMessage);
            return;
        }

        try {
            this.catStatHandlerDao.setNewStatus(identifier, statusMessage);
            saveRuntimeInfo(identifier, statusMessage);
        }
        catch (OwsExceptionReport e) {
            log.error("Error setting new status for " + identifier, e);
        }
    }

}

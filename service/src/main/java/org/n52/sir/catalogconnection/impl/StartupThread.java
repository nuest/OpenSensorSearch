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
package org.n52.sir.catalogconnection.impl;

import java.util.List;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.catalogconnection.CatalogConnectionScheduler;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Uses a thread for a delayed execution. This is necessary if both the catalog and the SIR run in the same
 * container. The update can be blocked if the {@link ICatalogStatusHandler} is not available in the context.
 * 
 * @author Daniel
 * 
 */
public class StartupThread extends Thread {

    protected static final int STARTUP_DELAY_SECS = 10;

    private static final Logger log = LoggerFactory.getLogger(StartupThread.class);

    private CatalogConnectionScheduler scheduler;

    private boolean scheduleOnStartup = false;

    private IConnectToCatalogDAO catalogDao;

    @Inject
    public StartupThread(@Named("oss.catalogconnection.scheduleJobsOnStartup")
    boolean scheduleOnStart, CatalogConnectionScheduler scheduler, IConnectToCatalogDAO dao) {
        this.scheduleOnStartup = scheduleOnStart;
        this.scheduler = scheduler;
        this.catalogDao = dao;

        log.debug("NEW {}", this);
    }

    @Override
    public void run() {
        if ( !this.scheduleOnStartup) {
            log.info("Startup scheduling not activated");
            return;
        }
        log.debug("* Starting Thread for catalog connections with a delay of {} seconds *", STARTUP_DELAY_SECS);

        try {
            // delay startup to minize issues if the catalog runs in the same container
            sleep(STARTUP_DELAY_SECS * 1000);
        }
        catch (InterruptedException e1) {
            log.error("Error waiting before start of catalog connections.", e1);
        }

        log.info("* Starting catalog connections *");

        int i = 0, j = 0, k = 0;
        try {
            List<ICatalogConnection> savedConnections = this.catalogDao.getCatalogConnectionList();

            for (ICatalogConnection iCatalogConnection : savedConnections) {
                if (iCatalogConnection.getPushIntervalSeconds() != ICatalogConnection.NO_PUSH_INTERVAL) {
                    boolean submit = this.scheduler.submit(iCatalogConnection);
                    if (submit)
                        i++;
                    else
                        j++;
                }
                else
                    log.debug("ICatalogConnection without push interval is ignored: {}",
                              iCatalogConnection.getConnectionID());
                k++;
            }
        }
        catch (OwsExceptionReport e) {
            log.error("Could not run tasks for saved catalog connections.", e.getMessage());
        }

        log.info("* Scheduled {} of {} task(s) from the database, could not schedule {}, and {} did not have a schedule *",
                 i,
                 i + j + k,
                 j,
                 k);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StartupThread [");
        if (this.catalogDao != null) {
            builder.append("catalogDao=");
            builder.append(this.catalogDao);
            builder.append(", ");
        }
        if (this.scheduler != null) {
            builder.append("scheduler=");
            builder.append(this.scheduler);
            builder.append(", ");
        }
        builder.append("scheduleOnStartup=");
        builder.append(this.scheduleOnStartup);
        builder.append("]");
        return builder.toString();
    }

}

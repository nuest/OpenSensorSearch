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
package org.n52.sir.catalogconnection.impl;

import java.util.Date;
import java.util.TimerTask;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalogconnection.CatalogConnectionScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Class encapsulates a {@link TimerServlet} where tasks are forwared to.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class CatalogConnectionSchedulerImpl implements CatalogConnectionScheduler {

    private static final long DEFAULT_DELAY_MILLISECS = 100;

    private static Logger log = LoggerFactory.getLogger(CatalogConnectionSchedulerImpl.class);

    private static final int SECONDS_TO_MILLISECONDS_FACTOR = 1000;

    /**
     * 
     * @param timer
     */
    protected CatalogConnectionSchedulerImpl() {
        log.info("NEW {}", this);
    }

    @Override
    public void cancel(String identifier) {
        log.debug("Cancelling Task: {}", identifier);

        // this.timerServlet.cancel(identifier);
    }

    @Override
    public boolean submit(ICatalogConnection conn) {
        log.debug("incoming submission: {}", conn);

        try {
            submit(conn, DEFAULT_DELAY_MILLISECS);
            return true;
        }
        catch (OwsExceptionReport e) {
            log.error("Could not submit catalog connection.", e);
            return false;
        }
    }

    /**
     * 
     * @param conn
     * @param delay
     * @throws OwsExceptionReport
     */
    private void submit(ICatalogConnection conn, long delay) throws OwsExceptionReport {
        ICatalog catalog = null; // this.timerServlet.getCatalog(conn);

        if (conn.getConnectionID() == ICatalogConnection.UNSAVED_CONNECTION_ID
                && conn.getPushIntervalSeconds() == ICatalogConnection.NO_PUSH_INTERVAL) {
            // push only once
            submitOnce(new PushCatalogTask(conn.getConnectionID(), null, // this.timerServlet.getCatalogStatusHandler(),
                                           catalog), delay);
        }
        else {
            // schedule periodic push
            submitRepeating(conn.getConnectionID(), new PushCatalogTask(conn.getConnectionID(), null, // this.timerServlet.getCatalogStatusHandler(),
                                                                        catalog), delay, conn.getPushIntervalSeconds()
                    * SECONDS_TO_MILLISECONDS_FACTOR);
        }
    }

    private void submitOnce(TimerTask task, long delay) {
        if (log.isDebugEnabled()) {
            log.debug("Scheduling Task: " + task + " for execution: now.");
        }
        Date runAt = new Date();
        runAt.setTime(runAt.getTime() + delay);
        // this.timerServlet.submit(task, runAt);
    }

    private void submitRepeating(String identifier, TimerTask task, long delay, long period) {
        if (log.isDebugEnabled()) {
            log.debug("Scheduling Task: " + task + " for execution now and with period of " + period
                    + "ms after a delay of " + delay + "ms.");
        }
        // this.timerServlet.submit(identifier, task, delay, period);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("JobSchedulerImpl [default delay (msecs) (ALWAYS applied!)=");
        sb.append(DEFAULT_DELAY_MILLISECS);
        sb.append(", internal task handler: ");
        sb.append("...");
        sb.append("]");
        return sb.toString();
    }
}
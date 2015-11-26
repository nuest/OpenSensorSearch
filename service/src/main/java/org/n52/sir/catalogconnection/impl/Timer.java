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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * This class can be used to execute {@link TimerTask} instances. It runs as a servlet and can be accessed by
 * other servlets for task scheduling and cancelling. The actual service method for GET and POST requests are
 * not implemented. It also provides methods to access the appropriate instances of
 * {@link ICatalogStatusHandler} and {@link ICatalogFactory} for tasks that run within this servlet.
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 * 
 */
@Singleton
public class Timer {

    /**
     * Inner class to handle storage and cancelling of tasks at runtime.
     */
    private static class TaskElement {
        protected Date date;
        protected long delay;
        protected String id;
        protected long period;
        protected TimerTask task;

        /**
         * 
         * @param identifier
         * @param task
         * @param delay
         * @param period
         */
        protected TaskElement(String identifier, TimerTask task, long delay, long period) {
            this.id = identifier;
            this.task = task;
            this.delay = delay;
            this.period = period;
            this.date = new Date(0l);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("TaskElement [");
            sb.append(this.task);
            sb.append(", delay=");
            sb.append(this.delay);
            sb.append(", period=");
            sb.append(this.period);
            sb.append(", date=");
            sb.append(this.date);
            sb.append("]");
            return sb.toString();
        }
    }

    private static final Logger log = LoggerFactory.getLogger(Timer.class);

    private Map<URI, ICatalog> catalogCache = new HashMap<>();

    private String[] catalogInitClassificationFiles;

    private String catalogSlotInitFile;

    private ICatalogStatusHandler catalogStatusHandler;

    private Properties props;

    private ArrayList<TaskElement> tasks = new ArrayList<>();

    private ICatalogFactory catalogFactory;

    @Inject
    public Timer(ICatalogStatusHandler handler, ICatalogFactory catalogFactory) {
        this.catalogStatusHandler = handler;
        this.catalogFactory = catalogFactory;

        // TODO create inner quartz timer
        // timer = new Timer(getServletName(),
        // Boolean.parseBoolean(getInitParameter(IS_DAEMON_INIT_PARAM_NAME)));

        log.info("NEW {}", this);
    }

    public void cancel(String identifier) {
        for (TaskElement te : this.tasks) {
            if (te.id.equals(identifier)) {
                te.task.cancel();
                log.info("CANCELED " + te);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        log.info("got called finalize()...");
    }

    public ICatalog getCatalog(ICatalogConnection conn) throws OwsExceptionReport {
        try {
            if ( !this.catalogCache.containsKey(conn.getCatalogURL().toURI())) {
                ICatalog catalog = this.catalogFactory.getCatalog(conn.getCatalogURL());
                this.catalogCache.put(conn.getCatalogURL().toURI(), catalog);
            }

            return this.catalogCache.get(conn.getCatalogURL().toURI());
        }
        catch (URISyntaxException e) {
            log.error("URI", e);
        }

        return null;
    }

    /**
     * 
     * " Finally, fixed-rate execution is appropriate for scheduling multiple repeating timer tasks that must
     * remain synchronized with respect to one another." See
     * {@link Timer#scheduleAtFixedRate(TimerTask, long, long)} for details.
     * 
     * @param task
     * @param delay
     * @param period
     */
    public void submit(String identifier, TimerTask task, long delay, long period) {
        // timer.scheduleAtFixedRate(task, delay, period);
        log.debug("Submitted: {} under id {}, with period {} and delay {}", task, period, delay, identifier);
        // element is scheduled with repetition period, so save for later cancelling!
        this.tasks.add(new TaskElement(identifier, task, delay, period));
    }

    public void submit(TimerTask task, Date date) {
        // timer.schedule(task, date);
        log.debug("Submitted: {} to run at {}", task, date);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Timer [");
        if (this.catalogCache != null) {
            builder.append("catalogCache=");
            builder.append(this.catalogCache);
            builder.append(", ");
        }
        if (this.catalogInitClassificationFiles != null) {
            builder.append("catalogInitClassificationFiles=");
            builder.append(Arrays.toString(this.catalogInitClassificationFiles));
            builder.append(", ");
        }
        if (this.catalogSlotInitFile != null) {
            builder.append("catalogSlotInitFile=");
            builder.append(this.catalogSlotInitFile);
            builder.append(", ");
        }
        if (this.catalogStatusHandler != null) {
            builder.append("catalogStatusHandler=");
            builder.append(this.catalogStatusHandler);
            builder.append(", ");
        }
        if (this.props != null) {
            builder.append("props=");
            builder.append(this.props);
            builder.append(", ");
        }
        if (this.tasks != null) {
            builder.append("tasks=");
            builder.append(this.tasks);
        }
        builder.append("]");
        return builder.toString();
    }

}

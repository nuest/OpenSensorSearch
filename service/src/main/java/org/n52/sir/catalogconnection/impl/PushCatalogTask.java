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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.XmlTools;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Class for executing the method {@link ICatalog#pushAllDataToCatalog()}. Error handling, logging and status
 * updating are included.
 *
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 *
 */
public class PushCatalogTask extends TimerTask {

    private static final String CAPABILITIES_NOT_ENSURED = "Catalog does not have sufficient capabilities and extensions could not be inserted!";

    private static final String LAST_COMPLETE_PUSH = "Last push finished succesfully with [insert, update, delete]: ";

    private static final String LAST_ERROR_OWS = "Error on last push: ";

    private static final String LAST_ERROR_PUSH = "Last push with errors (see the log for details).";

    private static final String LAST_PUSH_WITH_REPORTS = "Last push finished with errors, see the log for details. Error count: ";

    private static final Logger log = LoggerFactory.getLogger(PushCatalogTask.class);

    private ICatalog catalog;

    private ICatalogStatusHandler catalogStatusHandler;

    private String connectionID;

    private boolean repeated;

    public PushCatalogTask(String connectionID, ICatalogStatusHandler catalogStatusHandler, ICatalog catalogP) {
        this.connectionID = connectionID;
        this.catalogStatusHandler = catalogStatusHandler;
        this.catalog = catalogP;
    }

    @Override
    public boolean cancel() {
        log.info("Cancelling " + this);
        return super.cancel();
    }

    @Override
    protected void finalize() throws Throwable {
        log.debug("Finalizing {} ", this);
        super.finalize();
    }

    private String getReportString(List<OwsExceptionReport> reports) {
        StringBuilder sb = new StringBuilder();
        for (OwsExceptionReport owsExceptionReport : reports) {
            sb.append("\n");
            sb.append(owsExceptionReport.getDocument().xmlText(XmlTools.PRETTY_PRINT_OPTIONS));
        }
        return sb.toString();
    }

    private String getStatusSignature() {
        return " AT " + new Date(System.currentTimeMillis()).toString() + " WITH CATALOG " + this.catalog;
    }

    private void logReports(List<OwsExceptionReport> reports) {
        int i = 0;
        for (OwsExceptionReport owsExceptionReport : reports) {
            i++;
            log.error("Error " + i + " of " + reports.size() + " when pushing data to catalog (" + this.connectionID
                    + "): " + owsExceptionReport);
        }
    }

    @Override
    public void run() {
        log.info("*** Run PushCatalogTask to " + this.catalog);

        try {
            // check (probably once more, as the catalog was probably checked during first insertion - BUT
            // check here will be saved during runtime!)
            boolean caps = this.catalog.ensureSufficientCapabilities();
            if ( !caps) {
                log.error(CAPABILITIES_NOT_ENSURED);
                this.catalogStatusHandler.setStatus(this.connectionID, CAPABILITIES_NOT_ENSURED + getStatusSignature());
                return;
            }

            List<OwsExceptionReport> reports = this.catalog.pushAllDataToCatalog();
            if (reports.size() > 0) {
                logReports(reports);
                this.catalogStatusHandler.setStatus(this.connectionID, LAST_PUSH_WITH_REPORTS + reports.size() + "."
                        + getStatusSignature() + ":" + getReportString(reports));
            }
            else {
                int[] summary = this.catalog.getSummaryOfLastPush();
                this.catalogStatusHandler.setStatus(this.connectionID, LAST_COMPLETE_PUSH + Arrays.toString(summary)
                        + getStatusSignature());
            }

        }
        catch (OwsExceptionReport e) {
            log.error("*** Could not complete push catalog task!", e);
            this.catalogStatusHandler.setStatus(this.connectionID, LAST_ERROR_OWS + e.getMessage()
                    + getStatusSignature());
        }
        catch (Exception e) {
            log.error("*** Could not complete push catalog task!", e);
            this.catalogStatusHandler.setStatus(this.connectionID, LAST_ERROR_PUSH + getStatusSignature());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PushCatalogTask [connectionID=");
        sb.append(this.connectionID);
        sb.append(", catalog=");
        sb.append(this.catalog);
        sb.append(", repeated=");
        sb.append(this.repeated);
        sb.append("]");
        return sb.toString();
    }

}

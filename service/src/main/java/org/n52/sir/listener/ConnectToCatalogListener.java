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
package org.n52.sir.listener;

import java.net.URL;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.catalogconnection.CatalogConnectionScheduler;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirConnectToCatalogRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirConnectToCatalogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class ConnectToCatalogListener implements ISirRequestListener {

    private static final Logger log = LoggerFactory.getLogger(ConnectToCatalogListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.ConnectToCatalog.name();

    private IConnectToCatalogDAO conToCatDao;

    private CatalogConnectionScheduler scheduler;

    private ICatalogFactory catalogFactory;

    @Inject
    public ConnectToCatalogListener(IConnectToCatalogDAO dao,
                                    ICatalogFactory catalogFactory,
                                    CatalogConnectionScheduler scheduler) {
        this.catalogFactory = catalogFactory;
        this.conToCatDao = dao;
        this.scheduler = scheduler;

        log.info("NEW {}", this);
    }

    @Override
    public String getOperationName() {
        return ConnectToCatalogListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        log.debug("Receiving ConnectoToCatalogRequest");

        SirConnectToCatalogRequest conToCatReq = (SirConnectToCatalogRequest) request;
        SirConnectToCatalogResponse response = new SirConnectToCatalogResponse();
        String connectionID;

        int pushInterval = conToCatReq.getPushInterval();
        URL url = conToCatReq.getCswUrl();

        try {
            ICatalog catalog = this.catalogFactory.getCatalog(url);

            // check if csw is capable (with getCapabilities and more...)
            boolean b = catalog.ensureSufficientCapabilities();

            if ( !b) {
                log.warn("Catalog does not have sufficient capabilities!");
                OwsExceptionReport oer = new OwsExceptionReport();
                oer.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                      url.toString(),
                                      "Catalog capabilities not sufficient! Check the log for details.");
                return new ExceptionResponse(oer.getDocument());
            }

            // catalog not needed anymore, will be created again (and checked once more!) by scheduling
            // system.
            catalog = null;

            // only single push
            if (pushInterval == ICatalogConnection.NO_PUSH_INTERVAL) {
                // start connection with the same connectionID
                ICatalogConnection conn = this.catalogFactory.getCatalogConnection(ICatalogConnection.UNSAVED_CONNECTION_ID,
                                                                                   url,
                                                                                   ICatalogConnection.NO_PUSH_INTERVAL,
                                                                                   ICatalogConnection.NEW_CONNECTION_STATUS);
                this.scheduler.submit(conn);
                log.info("Submitted single connection:" + conn);
            }
            // with period
            else {
                // check for existing connection in database
                connectionID = this.conToCatDao.getConnectionID(url, pushInterval);

                if (connectionID == null) {
                    // a completely new task, insert connection to database
                    connectionID = this.conToCatDao.insertConnection(conToCatReq.getCswUrl(),
                                                                     conToCatReq.getPushInterval());

                    // change update sequence
                    // SirConfigurator.getInstance().newUpdateSequence();

                    // start connection with the same connectionID
                    ICatalogConnection conn = this.catalogFactory.getCatalogConnection(connectionID,
                                                                                       url,
                                                                                       pushInterval,
                                                                                       ICatalogConnection.NEW_CONNECTION_STATUS);
                    this.scheduler.submit(conn);

                    log.info("Inserted new connection into database, connection identifier: " + connectionID);
                }
                else {
                    // cancel existing task with connectionID
                    this.scheduler.cancel(connectionID);

                    // start connection with the same connectionID
                    ICatalogConnection conn = this.catalogFactory.getCatalogConnection(connectionID,
                                                                                       url,
                                                                                       pushInterval,
                                                                                       ICatalogConnection.NEW_CONNECTION_STATUS);
                    this.scheduler.submit(conn);

                    // update connection and start timertaks with same ID
                    this.conToCatDao.updateConnection(conToCatReq.getCswUrl(), conToCatReq.getPushInterval());
                    log.info("Updated connection with identifier: " + connectionID);
                }
            }

            // return url in response to show successful completition
            response.setCatalogUrl(url);
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }

        return response;
    }
}

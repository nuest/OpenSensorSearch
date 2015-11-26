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
package org.n52.sir.listener;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.ICatalogStatusHandler;
import org.n52.sir.catalogconnection.CatalogConnectionScheduler;
import org.n52.sir.ds.IDisconnectFromCatalogDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirDisconnectFromCatalogRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirDisconnectFromCatalogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class DisconnectFromCatalogListener implements ISirRequestListener {

    private static Logger log = LoggerFactory.getLogger(DisconnectFromCatalogListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.DisconnectFromCatalog.name();

    private IDisconnectFromCatalogDAO disconFromCatDao;

    CatalogConnectionScheduler scheduler;

    private ICatalogStatusHandler handler;

    @Inject
    public DisconnectFromCatalogListener(IDisconnectFromCatalogDAO dao,
                                         CatalogConnectionScheduler scheduler,
                                         ICatalogStatusHandler handler) {
        this.disconFromCatDao = dao;
        this.scheduler = scheduler;
        this.handler = handler;

        log.info("NEW {}", this);
    }

    @Override
    public String getOperationName() {
        return DisconnectFromCatalogListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirDisconnectFromCatalogRequest disconFromCatReq = (SirDisconnectFromCatalogRequest) request;
        SirDisconnectFromCatalogResponse response = new SirDisconnectFromCatalogResponse();

        String connectionID;
        try {
            // delete database entry
            connectionID = this.disconFromCatDao.deleteConnection(disconFromCatReq.getCswURL());

            if (connectionID == null) {
                // connection not in SIR
                OwsExceptionReport oer = new OwsExceptionReport();
                oer.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                      null,
                                      "No connection with given url (" + disconFromCatReq.getCswURL() + ") saved!");
                return new ExceptionResponse(oer.getDocument());
            }
            // delete timer with connectionID
            this.scheduler.cancel(connectionID);

            response.setCatalogUrl(disconFromCatReq.getCswURL());

            // set runtime status info
            this.handler.setStatus(connectionID, "Disconnected from catalog " + disconFromCatReq.getCswURL());

            return response;
        }
        catch (OwsExceptionReport e) {
            return new ExceptionResponse(e);
        }
    }

}

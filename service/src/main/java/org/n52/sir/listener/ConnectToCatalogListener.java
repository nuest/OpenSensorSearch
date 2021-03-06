/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    private static Logger log = LoggerFactory.getLogger(ConnectToCatalogListener.class);

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

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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.listener.harvest.IOOSHarvester;
import org.n52.sir.listener.harvest.SOSServiceHarvester;
import org.n52.sir.listener.harvest.SPSServiceHarvester;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirHarvestServiceRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte, Daniel Nüst (d.nuest@52north.org)
 *
 */
public class HarvestServiceListener implements ISirRequestListener {

    protected static Logger log = LoggerFactory.getLogger(HarvestServiceListener.class);

    private static final int THREAD_POOL_SIZE = 10;

    private static final String OPERATION_NAME = SirConstants.Operations.HarvestService.name();

    private final ExecutorService exec;

    private final IHarvestServiceDAO harvServDao;

    private final SOSServiceHarvester sosHarvester;

    private final SPSServiceHarvester spsHarvester;

    private final IOOSHarvester ioosHarvester;

    /*
     * FIXME instantiate a new harvester for every request
     */
    @Inject
    public HarvestServiceListener(IHarvestServiceDAO dao,
            SOSServiceHarvester sosHarvester,
            SPSServiceHarvester spsHarvester,
            IOOSHarvester ioosHarvester) {
        this.exec = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        this.harvServDao = dao;
        this.sosHarvester = sosHarvester;
        this.spsHarvester = spsHarvester;
        this.ioosHarvester = ioosHarvester;

        log.info("NEW {}", this);
    }

    @Override
    public String getOperationName() {
        return HarvestServiceListener.OPERATION_NAME;
    }

    private ISirResponse harvestIOOSCatalog(SirHarvestServiceRequest request) throws OwsExceptionReport {
        log.debug("Start harvest IOOSCatalog: {}", request.getServiceUrl());

        this.ioosHarvester.setRequest(request);
        Future<ISirResponse> future = this.exec.submit(this.ioosHarvester);

        try {
            ISirResponse response = future.get();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            throw new OwsExceptionReport(request.getServiceUrl(), e);
        }
    }

    private ISirResponse harvestSOS(SirHarvestServiceRequest request) throws OwsExceptionReport {
        log.debug("Start harvest SOS: {}", request.getServiceUrl());

        this.sosHarvester.setRequest(request);
        Future<ISirResponse> future = this.exec.submit(this.sosHarvester);

        try {
            ISirResponse response = future.get();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            throw new OwsExceptionReport(request.getServiceUrl(), e);
        }
    }

    private ISirResponse harvestSPS(SirHarvestServiceRequest request) throws OwsExceptionReport {
        log.debug("Start harvest SPS: {}", request.getServiceUrl());

        this.spsHarvester.setRequest(request);
        Future<ISirResponse> future = this.exec.submit(this.spsHarvester);

        try {
            ISirResponse response = future.get();
            return response;
        } catch (InterruptedException | ExecutionException e) {
            throw new OwsExceptionReport(request.getServiceUrl(), e);
        }
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        SirHarvestServiceRequest harvServReq = (SirHarvestServiceRequest) request;
        ISirResponse response = null;

        String serviceType = harvServReq.getServiceType().toUpperCase();
        try {
            switch (serviceType) {
                case SirConstants.SOS_SERVICE_TYPE:
                    response = harvestSOS(harvServReq);
                    break;
                case SirConstants.SPS_SERVICE_TYPE:
                    response = harvestSPS(harvServReq);
                    break;
                case SirConstants.IOOSCATAL0G_SERVICE_TYPE:
                    response = harvestIOOSCatalog(harvServReq);
                    break;
                default:
                    OwsExceptionReport report = new OwsExceptionReport(ExceptionCode.InvalidParameterValue,
                            "serviceType",
                            "Harvesting for the given service type '"
                            + harvServReq.getServiceType()
                            + "' not supported!");
                    return new ExceptionResponse(report);
            }

        } catch (OwsExceptionReport e) {
            return new ExceptionResponse(e);
        }

        return response;
    }

}

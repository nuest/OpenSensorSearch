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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.SirConstants.CapabilitiesSection;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.n52.sir.request.AbstractSirRequest;
import org.n52.sir.request.SirGetCapabilitiesRequest;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirGetCapabilitiesResponse;
import org.n52.sir.util.GMLDateParser;
import org.n52.sir.util.ListenersTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * @author Daniel
 * 
 */
public class GetCapabilitiesListener implements ISirRequestListener {

    private static final Logger log = LoggerFactory.getLogger(GetCapabilitiesListener.class);

    private static final String OPERATION_NAME = SirConstants.Operations.GetCapabilities.name();

    private IGetCapabilitiesDAO capDao;

    private SirConfigurator config;

    @Inject
    public GetCapabilitiesListener(SirConfigurator config, IGetCapabilitiesDAO dao) {
        this.config = config;
        this.capDao = dao;

        log.info("NEW {}", this);
    }

    private void checkAcceptedVersions(String[] versions) throws OwsExceptionReport {
        String serviceVersion = this.config.getServiceVersion();
        String[] acceptedServiceVersions = this.config.getAcceptedServiceVersions();

        for (String version : versions) {
            List<String> versionsList = Arrays.asList(acceptedServiceVersions);

            if (version == null || !versionsList.contains(version)) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                                     "version",
                                     "The Parameter 'version' does not contain the version of this SIR: '"
                                             + serviceVersion + "'");
                log.error("The accepted versions parameter is incorrect.", se);
                throw se;
            }
        }
    }

    private ArrayList<CapabilitiesSection> checkSections(String[] sections) throws OwsExceptionReport {
        ArrayList<CapabilitiesSection> responseSection = new ArrayList<>();
        for (String section : sections) {
            if (section.equalsIgnoreCase(CapabilitiesSection.Contents.name())) {
                responseSection.add(CapabilitiesSection.Contents);
            }
            else if (section.equalsIgnoreCase(CapabilitiesSection.OperationsMetadata.name())) {
                responseSection.add(CapabilitiesSection.OperationsMetadata);
            }
            else if (section.equalsIgnoreCase(CapabilitiesSection.ServiceIdentification.name())) {
                responseSection.add(CapabilitiesSection.ServiceIdentification);
            }
            else if (section.equalsIgnoreCase(CapabilitiesSection.ServiceProvider.name())) {
                responseSection.add(CapabilitiesSection.ServiceProvider);
            }
            else if (section.equalsIgnoreCase(CapabilitiesSection.All.name())) {
                responseSection.add(CapabilitiesSection.All);
            }
            else {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.InvalidParameterValue,
                                     null,
                                     "The parameter 'Sections' has a wrong value: '" + section
                                             + "'. Please use only this values: "
                                             + CapabilitiesSection.ServiceIdentification.name() + ", "
                                             + CapabilitiesSection.ServiceProvider.name() + ", "
                                             + CapabilitiesSection.OperationsMetadata.name() + ", "
                                             + CapabilitiesSection.Contents.name() + ", "
                                             + CapabilitiesSection.All.name());
                log.error("The sections parameter is incorrect.", se);
                throw se;
            }
        }
        return responseSection;
    }

    private boolean checkUpdateSequenceEquals(String updateSequence) throws OwsExceptionReport {
        if (updateSequence != null && !updateSequence.equals("") && !updateSequence.equals("NOT_SET")) {

            try {
                Calendar usDate = GMLDateParser.getInstance().parseString(updateSequence);
                Calendar sorUpdateSequence = GMLDateParser.getInstance().parseString(this.config.getUpdateSequence());
                if (usDate.equals(sorUpdateSequence)) {
                    return true;
                }
                else if (usDate.after(sorUpdateSequence)) {
                    OwsExceptionReport se = new OwsExceptionReport();
                    se.addCodedException(ExceptionCode.InvalidUpdateSequence, null, "The parameter 'updateSequence'"
                            + " is wrong. The Value should be a date in gml-format and could not be after '"
                            + GMLDateParser.getInstance().parseDate(sorUpdateSequence));
                    log.error("The update Sequence parameter is wrong!", se);
                    throw se;
                }
            }
            catch (ParseException pe) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(OwsExceptionReport.ExceptionCode.InvalidUpdateSequence,
                                     null,
                                     "The value of parameter update sequence has to be a date in GML format like this '"
                                             + GMLDateParser.getInstance().parseDate(Calendar.getInstance())
                                             + "'! Your requested value was: '" + updateSequence + "'");
                log.error("The date of the update sequence could not be parsed!", pe);
                throw se;
            }
        }
        return false;
    }

    @Override
    public String getOperationName() {
        return GetCapabilitiesListener.OPERATION_NAME;
    }

    @Override
    public ISirResponse receiveRequest(AbstractSirRequest request) {
        try {
            SirGetCapabilitiesRequest sirRequest = (SirGetCapabilitiesRequest) request;
            SirGetCapabilitiesResponse response = new SirGetCapabilitiesResponse(this.config, request.getRequestUri());

            // check service
            ListenersTools.checkServiceParameter(sirRequest.getService());

            // check acceptVersions
            if (sirRequest.getAcceptVersions() != null) {
                checkAcceptedVersions(sirRequest.getAcceptVersions());
            }
            // check sections
            if (sirRequest.getSections() != null) {
                response.setSections(checkSections(sirRequest.getSections()));
            }
            else {
                ArrayList<CapabilitiesSection> temp = new ArrayList<>();
                temp.add(CapabilitiesSection.All);
                response.setSections(temp);
            }

            // check updateSequence
            if (sirRequest.getUpdateSequence() != null) {
                if (checkUpdateSequenceEquals(sirRequest.getUpdateSequence())) {
                    return new SirGetCapabilitiesResponse(this.config, request.getRequestUri());
                }
            }

            // TODO check AcceptFormats (Not supported now)

            // set harvested Services
            response.setServices(this.capDao.getAllServices());

            // set catalog connections
            response.setCatalogConnection(this.capDao.getAllCatalogConnections());

            return response;
        }
        catch (OwsExceptionReport se) {
            return new ExceptionResponse(se.getDocument());
        }
    }
}
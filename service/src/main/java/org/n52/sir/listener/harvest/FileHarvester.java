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
/**
 * 
 */

package org.n52.sir.listener.harvest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import org.n52.oss.id.IdentifierGenerator;
import org.n52.oss.sir.Client;
import org.n52.oss.sir.api.SirSensor;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.response.ExceptionResponse;
import org.n52.sir.response.ISirResponse;
import org.n52.sir.response.SirHarvestServiceResponse;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.IProfileValidator.ValidatableFormatAndProfile;
import org.n52.sir.xml.ValidatorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * 
 * @author Daniel Nüst
 */
public abstract class FileHarvester extends Harvester {

    private static final Logger log = LoggerFactory.getLogger(FileHarvester.class);

    @Inject
    public FileHarvester(IHarvestServiceDAO harvServDao, IInsertSensorInfoDAO insertDao, @Named(ISearchSensorDAO.FULL)
    ISearchSensorDAO searchDao, Client client, Set<IProfileValidator> validators, @Named("oss.sir.responses.validate")
    boolean validateResponses, IdentifierGenerator idGen) {
        super(harvServDao,
              insertDao,
              searchDao,
              client,
              ValidatorModule.getFirstMatchFor(validators, ValidatableFormatAndProfile.SML_DISCOVERY),
              validateResponses,
              idGen);

        log.info("NEW {}", this);
    }

    @Override
    public ISirResponse call() throws Exception {
        ISirResponse r = null;

        try {
            log.info("Starting Harvesting of File " + this.request.getServiceUrl());

            SirHarvestServiceResponse response = new SirHarvestServiceResponse(this.validateResponses);
            // set service type in response
            response.setServiceType(this.request.getServiceType());
            // set service URL in response
            response.setServiceUrl(this.request.getServiceUrl());

            // this.reader = XMLInputFactory.newFactory().createXMLEventReader(new
            // FileReader(catalogFile));

            URL fileURL = new URL(this.request.getServiceUrl());
            // this.catalogXml = XmlObject.Factory.parse(fileURL);

            // event based parsing:
            XMLReader myReader = XMLReaderFactory.createXMLReader();

            // get the handler from implementing classes
            ContentHandler handler = getHandler();

            myReader.setContentHandler(handler);
            myReader.parse(new InputSource(fileURL.openStream()));

            // TODO add harvested catalog to database (with harvesting intervall) >> harvesting history in DB

            // change update sequence
            // SirConfigurator.getInstance().newUpdateSequence();

            response.setUpdatedSensors(this.updatedSensors);
            response.setNumberOfUpdatedSensors(this.updatedSensors.size());
            response.setDeletedSensors(new ArrayList<SirSensor>());
            response.setFailedSensors(this.failedSensors.keySet());
            for (Entry<String, String> failedSensor : this.failedSensors.entrySet()) {
                response.addFailureDescription(failedSensor.getKey(), failedSensor.getValue());
            }
            response.setNumberOfFailedSensors(this.failedSensors.size());
            response.setInsertedSensors(this.insertedSensors);
            response.setNumberOfInsertedSensors(this.insertedSensors.size());

            r = response;
        }
        catch (Exception e) {
            log.error("Error harvesting file at {}", this.request.getServiceUrl(), e);
            r = new ExceptionResponse(e);
        }

        reset();

        return r;
    }

    protected abstract ContentHandler getHandler();

}
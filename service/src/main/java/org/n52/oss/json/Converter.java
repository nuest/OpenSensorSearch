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
package org.n52.oss.json;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.opensearch.listeners.OpenSearchTools;
import org.n52.oss.sir.api.SirBoundingBox;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirService;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.api.SirSimpleSensorDescription;
import org.n52.sir.json.BoundingBox;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.json.Service;
import org.n52.sir.json.ServiceReference;
import org.n52.sir.json.SimpleSensorDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Converter {

    private static final Logger log = LoggerFactory.getLogger(Converter.class);

    public Converter() {
        //
    }

    public SearchResultElement convert(SirSearchResultElement sirSearchResultElement, boolean detailed) {
        if (detailed && sirSearchResultElement.getSensorDescription() instanceof SirDetailedSensorDescription) {
            SirDetailedSensorDescription desc = (SirDetailedSensorDescription) sirSearchResultElement.getSensorDescription();

            return createDetailedResult(desc);
        }

        return createResult(sirSearchResultElement);
    }

    private SearchResultElement createDetailedResult(SirDetailedSensorDescription desc) {
        log.debug("Adding a detailed sensor profile");

        SearchResultElement element = new SearchResultElement();

        element.setSensorId(desc.getId());
        log.trace("Begin date:" + desc.getBegineDate());
        element.setBeginDate(desc.getBegineDate());
        element.setEndDate(desc.getEndDate());

        if (desc.getInputs() != null)
            element.setInputs(desc.getInputs());
        if (desc.getOutputs() != null)
            element.setOutputs(desc.getOutputs());
        if (desc.getIdentifiers() != null)
            element.setIdentifiers(desc.getIdentifiers());
        if (desc.getClassifiers() != null)
            element.setClassifiers(desc.getClassifiers());
        if (desc.getContacts() != null)
            element.setContacts(desc.getContacts());
        if (desc.getKeywords() != null)
            element.setKeywords(desc.getKeywords());

        return element;
    }

    private SearchResultElement createResult(SirSearchResultElement sirSearchResultElement) {
        SearchResultElement sre = new SearchResultElement();

        sre.setSensorId(sirSearchResultElement.getSensorId());
        sre.setLastUpdate(sirSearchResultElement.getLastUpdate());

        Collection<ServiceReference> sr = new ArrayList<>();
        Collection<SirServiceReference> serviceReferences = sirSearchResultElement.getServiceReferences();

        if (serviceReferences != null) {
            for (SirServiceReference sirServiceReference : serviceReferences) {
                SirService service = sirServiceReference.getService();
                sr.add(new ServiceReference(new Service(service.getUrl(), service.getType()),
                                            sirServiceReference.getServiceSpecificSensorId()));
            }
            sre.setServiceReferences(sr);
        }

        if (sirSearchResultElement.getSensorDescription() instanceof SirSimpleSensorDescription) {
            SirSimpleSensorDescription d = (SirSimpleSensorDescription) sirSearchResultElement.getSensorDescription();

            SirBoundingBox b = d.getBoundingBox();
            BoundingBox bbox = null;
            if (b != null) {
                bbox = new BoundingBox(b.getEast(), b.getSouth(), b.getWest(), b.getNorth());
                bbox.setSrid(b.getSrid());
            }

            String text = OpenSearchTools.extractDescriptionText(d);
            SimpleSensorDescription sd = new SimpleSensorDescription(d.getSensorDescriptionURL(), text, bbox);
            sre.setSensorDescription(sd);
        }

        return sre;
    }

    public SearchResultElement convert(SirSensor decoded, boolean detailed) {
        SearchResultElement sre = new SearchResultElement();

        sre.setSensorId(decoded.getInternalSensorID());
        // sre.setLastUpdate(.getLastUpdate());

        Collection<ServiceReference> sr = new ArrayList<>();
        // Collection<SirServiceReference> serviceReferences = decoded.get

        // if (serviceReferences != null) {
        // for (SirServiceReference sirServiceReference : serviceReferences) {
        // SirService service = sirServiceReference.getService();
        // sr.add(new ServiceReference(new Service(service.getUrl(), service.getType()),
        // sirServiceReference.getServiceSpecificSensorId()));
        // }
        // sre.setServiceReferences(sr);
        // }

        // if (sirSearchResultElement.getSensorDescription() instanceof SirSimpleSensorDescription) {
        // SirSimpleSensorDescription d = (SirSimpleSensorDescription)
        // sirSearchResultElement.getSensorDescription();
        //
        // SirBoundingBox b = d.getBoundingBox();
        // BoundingBox bbox = null;
        // if (b != null) {
        // bbox = new BoundingBox(b.getEast(), b.getSouth(), b.getWest(), b.getNorth());
        // bbox.setSrid(b.getSrid());
        // }
        //
        // String text = OpenSearchTools.extractDescriptionText(d);
        // SimpleSensorDescription sd = new SimpleSensorDescription(d.getSensorDescriptionURL(), text, bbox);
        // sre.setSensorDescription(sd);
        // }

        // TODO implement conversion to Json

        return sre;
    }

}

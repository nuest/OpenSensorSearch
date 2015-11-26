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
package org.n52.sir.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class TestObjectGenerator {

    public static SimpleSensorDescription getSensorDescription() {
        return new SimpleSensorDescription("http://domain.tld:port/path/001",
                                     "This text describes the sensor.",
                                     getBoundingBox());
    }

    private static BoundingBox getBoundingBox() {
        return new BoundingBox(1, 2, 3, 4);
    }

    public static SearchResultElement getSearchResultElement() {
        SearchResultElement resultElement = new SearchResultElement();
        resultElement.setSensorId("001");
        Collection<ServiceReference> references = new ArrayList<ServiceReference>();
        references.add(getServiceReference());
        resultElement.setServiceReferences(references);
        resultElement.setLastUpdate(new Date());
        resultElement.setSensorDescription(TestObjectGenerator.getSensorDescription());

        return resultElement;
    }

    public static SearchResultElement getSearchResultElement2() {
        SearchResultElement resultElement = new SearchResultElement();
        resultElement.setSensorId("002");
        Collection<ServiceReference> references = new ArrayList<ServiceReference>();
        references.add(getServiceReference());
        resultElement.setServiceReferences(references);
        resultElement.setLastUpdate(new Date());
        resultElement.setSensorDescription(TestObjectGenerator.getSensorDescription());

        return resultElement;
    }

    private static ServiceReference getServiceReference() {
        return new ServiceReference(getService(), "urn:sos:001");
    }

    private static Service getService() {
        return new Service("http://host:port/path", "SOS");
    }

    public static SearchResult getSearchResult() {
        SearchResult sr = new SearchResult("http://service.url",
                                           "temperature",
                                           "http://service.url/search?q=temperature",
                                           "Search results for the keyword 'temperature' from Open Sensor Search.",
                                           "52°North",
                                           new Date());

        sr.addResult(getSearchResultElement());
        sr.addResult(getSearchResultElement2());

        return sr;
    }

}

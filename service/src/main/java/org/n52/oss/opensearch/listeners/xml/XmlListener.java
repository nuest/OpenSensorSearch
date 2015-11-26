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
package org.n52.oss.opensearch.listeners.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.n52.oss.opensearch.OpenSearchConfigurator;
import org.n52.oss.opensearch.OpenSearchConstants;
import org.n52.oss.opensearch.listeners.OpenSearchListener;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.XmlTools;
import org.n52.sir.response.SirSearchSensorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.SearchSensorResponseDocument;

import com.google.inject.Inject;

public class XmlListener implements OpenSearchListener {

    private static final Logger log = LoggerFactory.getLogger(XmlListener.class);

    private static final String MIME_TYPE = "application/xml";

    private static final String NAME = "XML";

    private OpenSearchConfigurator conf;

    @Inject
    public XmlListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);
    }

    @Override
    public Response createResponse(final Collection<SirSearchResultElement> searchResult,
                                   final MultivaluedMap<String, String> params) throws OwsExceptionReport {
        log.debug("Creating streamed response...");
        
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                String query = params.getFirst(OpenSearchConstants.QUERY_PARAM);

                log.debug("Creating XML response for {} with {} results.", query, searchResult.size());

                SirSearchSensorResponse sssr = new SirSearchSensorResponse();
                sssr.setSearchResultElements(searchResult);

                SearchSensorResponseDocument searchSensorRespDoc = sssr.createXml();

                searchSensorRespDoc.save(os, XmlTools.xmlOptionsForNamespaces());

                log.debug("Done with XML response.");
            }
        };

        return Response.ok(stream).build();
    }

    @Override
    public String getMimeType() {
        return MIME_TYPE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setOpenSearchEndpoint(URI uri) {
        // doing nothing with it yet
    }

    @Override
    public void setHomeURI(URI uri) {
        // doing nothing with it yet
    }

}

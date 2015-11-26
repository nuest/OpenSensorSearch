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
package org.n52.oss.opensearch.listeners;

import java.net.URI;
import java.util.Collection;
import java.util.Date;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.n52.oss.json.Converter;
import org.n52.oss.opensearch.OpenSearchConfigurator;
import org.n52.oss.opensearch.OpenSearchConstants;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.json.SearchResult;
import org.n52.sir.json.SearchResultElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class JsonListener implements OpenSearchListener {

    private static Logger log = LoggerFactory.getLogger(JsonListener.class);

    public static final String MIME_TYPE = MediaType.APPLICATION_JSON;

    private static final String NAME = "JSON";

    private OpenSearchConfigurator conf;

    private Converter converter;

    private URI openSearchEndpoint;

    private URI homeUri;

    @Inject
    public JsonListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);

        this.converter = new Converter();

        log.info("NEW {}", this);
    }

    @Override
    public Response createResponse(Collection<SirSearchResultElement> searchResult,
                                   MultivaluedMap<String, String> params) throws OwsExceptionReport {
        log.debug("Creating response for {} search results with params {}", searchResult.size(), params);
        if (this.openSearchEndpoint == null)
            return Response.serverError().entity(" {\"error\" : \"no OpenSearch endpoint defined, cannot create response.\" } ").build();
        if (this.homeUri == null)
            return Response.serverError().entity(" {\"error\" : \"no home URI defined, cannot create response.\" } ").build();

        String website = this.homeUri.toString();
        String searchUri = this.openSearchEndpoint.toString();

        String searchText = params.getFirst(OpenSearchConstants.QUERY_PARAM);

        String responseDescription = "These are the search hits for the keyword(s) '" + searchText
                + "' from Open Sensor Search (" + website + ").";
        String responseURL = searchUri + "?" + OpenSearchConstants.QUERY_PARAM + "="
                + searchText + "&" + OpenSearchConstants.FORMAT_PARAM + "=" + MIME_TYPE;

        // build the response object
        SearchResult result = new SearchResult(website,
                                               searchText,
                                               responseURL,
                                               responseDescription,
                                               this.conf.getResponseAuthor(),
                                               new Date());

        for (SirSearchResultElement sirSearchResultElement : searchResult) {
            SearchResultElement element = this.converter.convert(sirSearchResultElement, true);
            result.addResult(element);
        }

        return Response.ok(result).build();
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
        this.openSearchEndpoint = uri;
    }

    @Override
    public void setHomeURI(URI uri) {
        this.homeUri = uri;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JsonListener [");
        if (this.conf != null) {
            builder.append("conf=");
            builder.append(this.conf);
            builder.append(", ");
        }
        if (this.converter != null) {
            builder.append("converter=");
            builder.append(this.converter);
            builder.append(", ");
        }
        if (this.openSearchEndpoint != null) {
            builder.append("openSearchEndpoint=");
            builder.append(this.openSearchEndpoint);
            builder.append(", ");
        }
        if (this.homeUri != null) {
            builder.append("homeUri=");
            builder.append(this.homeUri);
        }
        builder.append("]");
        return builder.toString();
    }

}

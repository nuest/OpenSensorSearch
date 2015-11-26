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

import net.opengis.kml.x22.AbstractFeatureType;
import net.opengis.kml.x22.KmlDocument;
import net.opengis.kml.x22.KmlType;

import org.n52.oss.opensearch.OpenSearchConfigurator;
import org.n52.oss.opensearch.OpenSearchConstants;
import org.n52.oss.opensearch.listeners.OpenSearchListener;
import org.n52.oss.opensearch.listeners.feed.AbstractFeedListener;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class KmlListener implements OpenSearchListener {

    protected static final Logger log = LoggerFactory.getLogger(AbstractFeedListener.class);

    public static final String MIME_TYPE = OpenSearchConstants.APPLICATION_VND_KML;

    private static final String NAME = "KML";

    private OpenSearchConfigurator conf;

    protected URI homeUri;

    @Inject
    public KmlListener(OpenSearchConfigurator configurator) {
        this.conf = configurator;
        this.conf.addResponseFormat(this);
    }

    @Override
    public Response createResponse(final Collection<SirSearchResultElement> searchResult,
                                   final MultivaluedMap<String, String> params) throws OwsExceptionReport {
        if (this.homeUri == null) {
            log.error("Could not create response because OpenSearch endpoint or home URI are not set: {}", this);
            return Response.serverError().build();
        }

        log.debug("Creating streamed response...");
        final String query = params.getFirst(OpenSearchConstants.QUERY_PARAM);

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                log.debug("Creating KML response for {} with {} results.", query, searchResult.size());

                KmlDocument doc = KmlDocument.Factory.newInstance();
                KmlType kml = doc.addNewKml();
                
                AbstractFeatureType abstractFeatureGroup = kml.addNewAbstractFeatureGroup();
                abstractFeatureGroup.addNewAuthor().addName("Open Sensor Search");
                abstractFeatureGroup.addNewLink().addNewHref().setStringValue(KmlListener.this.homeUri.toString());

                // TODO add kml content

                doc.save(os, XmlTools.xmlOptionsForNamespaces());

                log.debug("Done with XML response.");
            }
        };

        return Response.ok(stream).header("Content-Disposition",
                                          "attachment; filename=" + query + "_Open-Sensor-Search.kml").build();
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
        // do nothing with it yet
    }

    @Override
    public void setHomeURI(URI uri) {
        this.homeUri = uri;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("KmlListener [");
        if (this.conf != null) {
            builder.append("conf=");
            builder.append(this.conf);
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

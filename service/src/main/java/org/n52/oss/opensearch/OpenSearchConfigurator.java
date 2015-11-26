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
package org.n52.oss.opensearch;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.n52.oss.opensearch.listeners.OpenSearchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * 
 * TODO move everything in configurator to external configuration file!
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
@Singleton
public class OpenSearchConfigurator {

    private static Logger log = LoggerFactory.getLogger(OpenSearchConfigurator.class);

    private int capabilitiesCacheMaximumAgeSeconds = 60 * 60;

    /**
     * use contextually shortended urls (replacing long string identifiers with integer ids)
     */
    private boolean compressPermalinks = true;

    @Deprecated
    private String cssFile = "sir.css";

    @Deprecated
    private String feedAuthor = "Open Sensor Search by 52°North";

    // TODO move to external configuration file
    private String permalinkBaseURL = "http://sensorweb.demo.52north.org/sensorwebclient-webapp-stable/";

    private SimpleDateFormat permalinkDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    HashMap<String, String> responseFormats = new HashMap<>();

    @Deprecated
    private String responseAuthor = "52°North";

    public OpenSearchConfigurator() {
        log.info("NEW {}", this);
    }

    public void addResponseFormat(OpenSearchListener listener) {
        this.responseFormats.put(listener.getMimeType(), listener.getName());
    }

    public int getCapabilitiesCacheMaximumAgeSeconds() {
        return this.capabilitiesCacheMaximumAgeSeconds;
    }

    @Deprecated
    public String getCssFile() {
        return this.cssFile;
    }

    @Deprecated
    public String getFeedAuthor() {
        return this.feedAuthor;
    }

    public String getPermalinkBaseURL() {
        return this.permalinkBaseURL;
    }

    public SimpleDateFormat getPermalinkDateFormat() {
        return this.permalinkDateFormat;
    }

    public Map<String, String> getResponseFormats() {
        return this.responseFormats;
    }

    public boolean isCompressPermalinks() {
        return this.compressPermalinks;
    }

    @Deprecated
    public String getResponseAuthor() {
        return this.responseAuthor;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OpenSearchConfigurator [capabilitiesCacheMaximumAgeSeconds=");
        builder.append(this.capabilitiesCacheMaximumAgeSeconds);
        builder.append(", compressPermalinks=");
        builder.append(this.compressPermalinks);
        builder.append(", ");
        if (this.cssFile != null) {
            builder.append("cssFile=");
            builder.append(this.cssFile);
            builder.append(", ");
        }
        if (this.feedAuthor != null) {
            builder.append("feedAuthor=");
            builder.append(this.feedAuthor);
            builder.append(", ");
        }
        if (this.permalinkBaseURL != null) {
            builder.append("permalinkBaseURL=");
            builder.append(this.permalinkBaseURL);
            builder.append(", ");
        }
        if (this.permalinkDateFormat != null) {
            builder.append("permalinkDateFormat=");
            builder.append(this.permalinkDateFormat);
            builder.append(", ");
        }
        if (this.responseFormats != null) {
            builder.append("responseFormats=");
            builder.append(this.responseFormats);
            builder.append(", ");
        }
        if (this.responseAuthor != null) {
            builder.append("responseAuthor=");
            builder.append(this.responseAuthor);
        }
        builder.append("]");
        return builder.toString();
    }

}

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
package org.n52.oss.ui;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thanks to Matthes, based on
 * https://github.com/enviroCar/enviroCar-server/blob/master/event/src/main/java/org
 * /envirocar/server/event/SendVerificationCodeViaMailListener.java
 * 
 * @author Daniel
 * 
 */
public class WebsiteConfig {

    protected static Logger log = LoggerFactory.getLogger(WebsiteConfig.class);

    private static final String CONFIG_FILE = "org.n52.oss.website.properties";

    private static final String SIR_ENDPOINT = "oss.ui.sir.endpoint";

    private static final String API_ENDPOINT = "oss.ui.api.endpoint";

    private String sirEndpoint;

    private String apiEndpoint;

    public WebsiteConfig() {
        Properties props = new Properties();

        try {
            // load default values
            InputStream stream = openStreamForResource("/" + CONFIG_FILE);
            try (Reader r = new InputStreamReader(stream);) {
                props.load(r);
                log.debug("Loaded properties from /{}", CONFIG_FILE);
            }
        }
        catch (IOException e) {
            log.error("Could not load properties.", e);
        }

        String home = System.getProperty("user.home");
        log.debug("Used home directory: {}", home);

        if (home != null) {
            File homeDirectory = new File(home);

            try {
                if (homeDirectory != null && homeDirectory.isDirectory()) {
                    File configFile = new File(homeDirectory, CONFIG_FILE);
                    if (configFile != null && configFile.exists())
                        try (FileReader r = new FileReader(configFile);) {
                            props.load(r);
                            log.debug("Loaded properties (overwriting defaults) from {}", configFile);
                        }
                    else
                        log.info("No config file in user home ({}), let's see if the defaults work...", homeDirectory);
                }
            }
            catch (IOException e) {
                log.error("Could not load properties.", e);
            }
        }
        else
            log.warn("user.home is not specified. Will use default configuration.");

        load(props);

        log.info("NEW {}", this);
    }

    private void load(Properties props) {
        this.sirEndpoint = props.getProperty(SIR_ENDPOINT);
        this.apiEndpoint = props.getProperty(API_ENDPOINT);
    }

    private InputStream openStreamForResource(String string) throws IOException {
        URL resource = getClass().getResource(string);
        URLConnection conn = resource.openConnection();
        conn.setUseCaches(false);
        return conn.getInputStream();
    }

    public String getSirEndpoint() {
        return this.sirEndpoint;
    }

    public void setSirEndpoint(String sirEndpoint) {
        this.sirEndpoint = sirEndpoint;
    }

    public String getApiEndpoint() {
        return this.apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Config [");
        if (this.sirEndpoint != null) {
            builder.append("sirEndpoint=");
            builder.append(this.sirEndpoint);
            builder.append(", ");
        }
        if (this.apiEndpoint != null) {
            builder.append("apiEndpoint=");
            builder.append(this.apiEndpoint);
        }
        builder.append("]");
        return builder.toString();
    }
}

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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {

    private static Logger log = LoggerFactory.getLogger(Configuration.class);

    private static Configuration instance;

    static {
        try {
            instance = new Configuration();
        }
        catch (IOException e) {
            log.error("Could not create instance.", e);
        }
    }

    private static Properties properties;

    private Configuration() throws IOException {
        Properties props = new Properties();
        props.load(Configuration.class.getResourceAsStream("default.properties"));
        properties = props;

        log.info("NEW {}", this);
    }

    public static Configuration getInstance() {
        return instance;
    }

    public URL getSirURL() {
        URL url = null;
        try {
            url = new URL(properties.getProperty(""));
        }
        catch (MalformedURLException e) {
            log.error("Could not create URL", e);
        }
        return url;
    }

}

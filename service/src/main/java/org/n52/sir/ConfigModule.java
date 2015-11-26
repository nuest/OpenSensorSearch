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
package org.n52.sir;

import com.google.inject.AbstractModule;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Names;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class ConfigModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(ConfigModule.class);

    private static final String HOME_CONFIG_FILE = "org.n52.oss.service.sir.properties";

    @Override
    protected void configure() {
        try {
            Properties sirProps = loadProperties("/prop/default.properties");

            // update properties from home folder file
            sirProps = updateFromUserHome(sirProps, HOME_CONFIG_FILE);

            // bind properties class
            bind(Properties.class).annotatedWith(Names.named("sir_properties")).toInstance(sirProps);

            // bind alle properties as named properties
            Names.bindProperties(binder(), sirProps);

            log.debug("Loaded and bound properties:\n\t{}", sirProps);
        } catch (Exception e) {
            log.error("Could not load properties file.", e);
        }

        bind(SirConfigurator.class);

        // overwrite with properties from home folder
        // these don't work yet - use workaround with org.w3c.dom.Document
        // bind(OwsExMessageBodyWriter.class);
        // bind(EbRimMessageBodyWriter.class);
        log.info("Configured {}", this);
    }

    protected Properties updateFromUserHome(Properties props, String homeConfigFile) {
        log.debug("Updating properties {} from {}", props, homeConfigFile);

        String home = System.getProperty("user.home");
        log.debug("Used home directory: {}", home);

        if (home != null) {
            File homeDirectory = new File(home);

            try {
                if (homeDirectory != null && homeDirectory.isDirectory()) {
                    File configFile = new File(homeDirectory, homeConfigFile);
                    if (configFile != null && configFile.exists()) {
                        try (Reader r = new FileReader(configFile);) {
                            props.load(r);
                            log.info("Loaded properties (overwriting defaults) from {}", configFile);
                        }
                    } else {
                        log.debug("No config file in user home ({}), let's see if the defaults work...", homeDirectory);
                    }
                }
            } catch (IOException e) {
                log.error("Could not load properties.", e);
            }
        } else {
            log.warn("user.home is not specified. Will try to use fallback resources.");
        }

        return props;
    }

    protected Properties loadProperties(String name) throws IOException {
        log.debug("Loading properties for {}", name);

        Properties properties = new Properties();
        try (InputStream is = getClass().getResourceAsStream(name);) {
            if (is != null) {
                properties.load(is);
                log.debug("Loaded properties: {}", properties);
            } else {
                log.error("Could not load properties from {}", name);
            }
        }

        return properties;
    }

}

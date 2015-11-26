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
package org.n52.sir;

import java.util.Properties;

import org.n52.oss.common.AbstractConfigModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Names;

public class ConfigModule extends AbstractConfigModule {

    private static Logger log = LoggerFactory.getLogger(ConfigModule.class);

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
        }
        catch (Exception e) {
            log.error("Could not load properties file.", e);
        }

        bind(SirConfigurator.class);

        // overwrite with properties from home folder

        // these don't work yet - use workaround with org.w3c.dom.Document
        // bind(OwsExMessageBodyWriter.class);
        // bind(EbRimMessageBodyWriter.class);

        log.info("Configured {}", this);
    }

}

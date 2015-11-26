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
package org.n52.oss.guice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html
 * 
 * @author Daniel
 * 
 */
public class ServiceLoaderConfigurationModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoaderConfigurationModule.class);

    private static Collection<String> loadedModules = new ArrayList<>();

    @Override
    protected void configure() {
        for (Module m : ServiceLoader.load(Module.class)) {
            try {
                log.trace("Loading module {}...", m);
                install(m);
                loadedModules.add(m.toString());
                log.debug("Installed {}", m);
            }
            catch (Exception e) {
                log.error("Could not load module {}", m, e);
            }
        }

        log.info("Configured {}, modules: {}", this, Arrays.toString(loadedModules.toArray()));
    }
}

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
package org.n52.sir.catalogconnection;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.sir.catalogconnection.impl.CatalogConnectionSchedulerProvider;
import org.n52.sir.catalogconnection.impl.StartupThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;

public class CatalogConnectionModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(CatalogConnectionModule.class);

    private ExecutorService exec = Executors.newSingleThreadExecutor();

    @Override
    protected void configure() {
        // try {
        // // TODO move catalog connection properties to own file in own module
        // Properties properties = new Properties();
        // properties.load(getClass().getResourceAsStream("/prop/sir.properties"));
        // Names.bindProperties(binder(), properties);
        //
        // log.debug("Loaded and bound properties:\n\t{}", properties);
        // }
        // catch (IOException e) {
        // log.error("Could not load properties.", e);
        // }

        bind(CatalogConnectionScheduler.class).toProvider(CatalogConnectionSchedulerProvider.class);

        // having the exec here is not really nice... and does not work...
        // Provider<StartupThread> provider = getProvider(StartupThread.class);
        // this.exec.submit(provider.get());
        // FIXME start startup thread somehow...
        bind(StartupThread.class);

        log.debug("Configured {}", this);
    }
    
}

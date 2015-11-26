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
package org.n52.oss.guice.log;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * See also https://github.com/artgo/guice-slf4j
 * 
 * @author Daniel
 * 
 */
public class Slf4jTypeListener implements TypeListener {

    private static final Logger log = LoggerFactory.getLogger(Slf4jTypeListener.class);

    public Slf4jTypeListener() {
        log.info("NEW {}", this);
    }

    @Override
    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {
        Class< ? super I> cls = type.getRawType();

        do {
            for (final Field field : cls.getDeclaredFields()) {
                if ( (field.getType() == Logger.class) && field.isAnnotationPresent(InjectLogger.class)) {
                    log.debug("Adding logger to class {}", cls);
                    encounter.register(new Slf4jMembersInjector<I>(field));
                }
            }

            // Got through all parents as well
            cls = cls.getSuperclass();
        } while (cls != null);
    }
}
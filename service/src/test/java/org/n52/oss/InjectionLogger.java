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
package org.n52.oss;

import org.n52.oss.guice.log.InjectLogger;
import org.n52.oss.guice.log.Slf4jTypeListener;
import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.matcher.Matchers;

public class InjectionLogger {

    // @BeforeClass
    public static void configure() {
        Guice.createInjector(new AbstractModule() {

            @Override
            protected void configure() {
                bindListener(Matchers.any(), new Slf4jTypeListener());
            }
        });
    }

    // @Test
    public void loggerIsInjected() {
        ClassWithLogger cwl = new ClassWithLogger();
        cwl.doSomething();

        // TODO somehow check if the log is correctly added to stdout
    }

    private static class ClassWithLogger {

        @InjectLogger
        private static Logger log;

        public ClassWithLogger() {
            log.debug("NEW {}", this);
        }

        public void doSomething() {
            log.info("Do something with a logger...");

        }

    }

}

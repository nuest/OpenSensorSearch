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
package org.n52.sir.xml;

import java.util.HashSet;
import java.util.Set;

import org.n52.sir.xml.IProfileValidator.ValidatableFormatAndProfile;
import org.n52.sir.xml.impl.SensorML4DiscoveryValidatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class ValidatorModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(ValidatorModule.class);

    @Override
    protected void configure() {
        Multibinder<IProfileValidator> listenerBinder = Multibinder.newSetBinder(binder(), IProfileValidator.class);
        listenerBinder.addBinding().to(SensorML4DiscoveryValidatorImpl.class);

        log.debug("configured {}", this);
    }

    public static IProfileValidator getFirstMatchFor(Set<IProfileValidator> validators,
                                                     ValidatableFormatAndProfile profile) {
        Set<IProfileValidator> set = getFor(validators, profile);
        if ( !set.isEmpty())
            return set.iterator().next();

        return null;
    }

    public static Set<IProfileValidator> getFor(Set<IProfileValidator> validators, ValidatableFormatAndProfile profile) {
        Set<IProfileValidator> filtered = new HashSet<>();
        for (IProfileValidator v : validators) {
            if (v.validates(profile))
                filtered.add(v);
        }
        return filtered;
    }

}

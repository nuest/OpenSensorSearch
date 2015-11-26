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

import org.n52.sir.xml.ITransformer.TransformableFormat;
import org.n52.sir.xml.impl.SMLtoEbRIMTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class TransformerModule extends AbstractModule {

    private static Logger log = LoggerFactory.getLogger(TransformerModule.class);

    @Override
    protected void configure() {
        Multibinder<ITransformer> listenerBinder = Multibinder.newSetBinder(binder(), ITransformer.class);
        listenerBinder.addBinding().to(SMLtoEbRIMTransformer.class);

        log.debug("configured {}", this);
    }

    public static ITransformer getFirstMatchFor(Set<ITransformer> transformers,
                                                TransformableFormat input,
                                                TransformableFormat output) {
        Set<ITransformer> set = getForOutput(getForInput(transformers, input), output);
        if ( !set.isEmpty())
            return set.iterator().next();

        return null;
    }

    public static Set<ITransformer> getFor(Set<ITransformer> transformers,
                                           TransformableFormat input,
                                           TransformableFormat output) {
        return getForOutput(getForInput(transformers, input), output);
    }

    public static Set<ITransformer> getForOutput(Set<ITransformer> transformers, TransformableFormat output) {
        Set<ITransformer> filtered = new HashSet<>();
        for (ITransformer t : transformers) {
            if (t.producesOutput(output))
                filtered.add(t);
        }
        return filtered;
    }

    public static Set<ITransformer> getForInput(Set<ITransformer> transformers, TransformableFormat input) {
        Set<ITransformer> filtered = new HashSet<>();
        for (ITransformer t : transformers) {
            if (t.acceptsInput(input))
                filtered.add(t);
        }
        return filtered;
    }

}

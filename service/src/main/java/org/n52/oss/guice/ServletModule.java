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

import java.util.HashMap;
import java.util.Map;

import org.n52.oss.api.AutoCompleteResource;
import org.n52.oss.api.TransformationResource;
import org.n52.oss.api.UserAccessResource;
import org.n52.oss.api.ValidatorResource;
import org.n52.oss.opensearch.OpenSearch;
import org.n52.sir.SIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class ServletModule extends JerseyServletModule {

    private static final Logger log = LoggerFactory.getLogger(ServletModule.class);

    public ServletModule() {
        super();
    }

    @Override
    protected void configureServlets() {
        // String basepath = getServletContext().getRealPath("/");
        // bindConstant().annotatedWith(Names.named("context.basepath")).to(basepath);

        // bind(IValidatorFactory.class).to(ValidatorFactoryImpl.class);
        // bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Singleton.class);

        // bind the JAX-RS resources: http://code.google.com/p/google-guice/wiki/ServletModule
        // TODO split up service into modules, move the binding to the respective modules
        bind(AutoCompleteResource.class);
        bind(OpenSearch.class);
        bind(SIR.class);
        bind(TransformationResource.class);
        bind(ValidatorResource.class);
        bind(UserAccessResource.class);

        // bind(StatisticsResource.class);

        if (log.isDebugEnabled())
            filter("*").through(DebugFilter.class);

        filter("*").through(CorsFilter.class);

        Map<String, String> params = new HashMap<>();
        params.put("com.sun.jersey.config.property.JSPTemplatesBasePath", "/WEB-INF");

        params.put("com.sun.jersey.config.property.WebPageContentRegex", "/.*\\.(jpg|ico|png|gif|html|id|txt|css|js)");
        params.put("com.sun.jersey.config.property.packages",
                   "org.n52.oss.api;com.wordnik.swagger.jersey.listing;org.codehaus.jackson.jaxrs");
        // params.put("api.version","1.0.0");
        // filter("/doc/api/*").through(GuiceContainer.class,params);
        // filter("/api-docs/*").through(GuiceContainer.class,params);
        
        filter("/*").through(GuiceContainer.class, params);
        log.debug("configured {} with context {}", this, getServletContext());
    }

}

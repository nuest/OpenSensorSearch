/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.n52.sir.script.HarvestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class ServletModule extends JerseyServletModule {

    private static Logger log = LoggerFactory.getLogger(ServletModule.class);

    public ServletModule() {
        super();
    }

    @Override
    protected void configureServlets() {
        // String basepath = getServletContext().getRealPath("/");
        // bindConstant().annotatedWith(Names.named("context.basepath")).to(basepath);

        bind(IJSExecute.class).to(RhinoJSExecute.class);
        // bind(IValidatorFactory.class).to(ValidatorFactoryImpl.class);
        // bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Singleton.class);

        // bind the JAX-RS resources: http://code.google.com/p/google-guice/wiki/ServletModule
        // TODO split up service into modules, move the binding to the respective modules
        bind(HarvestResource.class);
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

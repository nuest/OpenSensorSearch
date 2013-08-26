/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n52.oss.guice;

import java.util.Properties;

import org.n52.oss.opensearch.OpenSearch;
import org.n52.sir.AutoCompleteSearch;
import org.n52.sir.SIR;
import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IInsertRemoteHarvestServer;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.pgsql.PGSQLInsertRemoteHarvestServer;
import org.n52.sir.ds.solr.SOLRInsertSensorInfoDAO;
import org.n52.sir.script.HarvestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Names;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class ServletModule extends JerseyServletModule {

	private static Logger log = LoggerFactory.getLogger(ServletModule.class);

	public ServletModule() {
		super();
	}

	@Override
	protected void configureServlets() {
		String basepath = getServletContext().getRealPath("/");
		bindConstant().annotatedWith(Names.named("context.basepath")).to(
				basepath);

		// install(new
		// FactoryModuleBuilder().implement(ApplicationConstants.class,
		// PropertyApplicationConstants.class).build(ConfigFactory.class));

		// bind(IJSExecute.class).to(RhinoJSExecute.class);
		// bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Singleton.class);
		// bind(IInsertSensorInfoDAO.class).to(SOLRInsertSensorInfoDAO.class);
		// bind(IInsertRemoteHarvestServer.class).to(PGSQLInsertRemoteHarvestServer.class);
		// // bind the JAX-RS resources
		// http://code.google.com/p/google-guice/wiki/ServletModule
		
		try {
			Properties sirProps = new Properties();
			Properties dbProps = new Properties();
			sirProps.load(HarvestResource.class
					.getResourceAsStream("/prop/sir.properties"));
			dbProps.load(HarvestResource.class
					.getResourceAsStream("/prop/db.properties"));
			Names.bindProperties(binder(), sirProps);
			Names.bindProperties(binder(), dbProps);
		} catch (Exception e) {
			log.error("Error loading props", e);
		}
		bind(SirConfigurator.class);
		bind(HelloGuice.class);
		bind(HarvestResource.class);
		bind(AutoCompleteSearch.class);
		bind(OpenSearch.class);
		bind(SIR.class);

		// filter("*").through(DebugFilter.class);
		serve("/*").with(GuiceContainer.class);

		log.info("configured {} with context {}", this, getServletContext());
	}

}
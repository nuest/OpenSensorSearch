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
package org.n52.sir.ds.pgsql;

import java.io.IOException;
import java.util.Properties;

import org.n52.oss.common.AbstractConfigModule;
import org.n52.oss.config.ConfigModule;
import org.n52.sir.ds.ICatalogStatusHandlerDAO;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.n52.sir.ds.IDAOFactory;
import org.n52.sir.ds.IDescribeSensorDAO;
import org.n52.sir.ds.IDisconnectFromCatalogDAO;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.n52.sir.ds.IGetSensorStatusDAO;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.n52.sir.ds.IInsertSensorStatusDAO;
import org.n52.sir.ds.ISearchSensorDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Names;

public class PGSQLModule extends AbstractConfigModule {

    private static Logger log = LoggerFactory.getLogger(PGSQLModule.class);

    private static final String HOME_CONFIG_FILE = "org.n52.oss.service.db.properties";

    @Override
    protected void configure() {
        try {
            Properties properties = new Properties();
            properties.load(ConfigModule.class.getResourceAsStream("/prop/db.properties"));

            // update properties from home folder file
            properties = updateFromUserHome(properties, HOME_CONFIG_FILE);

            Names.bindProperties(binder(), properties);
            log.debug("Loaded and bound properties:\n\t{}", properties);

            // leftover of old configuration: constants must be initialized
            PGDAOConstants.loadAndInstantiate(properties);
        }
        catch (IOException e) {
            log.error("Could not load properties.", e);
        }

        bind(IDAOFactory.class).to(DAOFactory.class);
        bind(PGConnectionPool.class).toProvider(DAOFactory.class);

        bind(ISearchSensorDAO.class).annotatedWith(Names.named(ISearchSensorDAO.FULL)).to(PGSQLSearchSensorDAO.class);
        bind(ISearchSensorDAO.class).annotatedWith(Names.named(ISearchSensorDAO.AUTOCOMPLETE)).to(PGSQLSearchSensorDAO.class);

        bind(IGetCapabilitiesDAO.class).to(PGSQLGetCapabilitiesDAO.class);
        bind(IConnectToCatalogDAO.class).to(PGSQLConnectToCatalogDAO.class);
        bind(IInsertSensorInfoDAO.class).to(PGSQLInsertSensorInfoDAO.class);
        bind(IDescribeSensorDAO.class).to(PGSQLDescribeSensorDAO.class);
        bind(IGetSensorStatusDAO.class).to(PGSQLGetSensorStatusDAO.class);
        bind(IHarvestServiceDAO.class).to(PGSQLHarvestServiceDAO.class);
        bind(IInsertSensorStatusDAO.class).to(PGSQLInsertSensorStatusDAO.class);
        bind(IDisconnectFromCatalogDAO.class).to(PGSQLDisconnetFromCatalogDAO.class);
        bind(ICatalogStatusHandlerDAO.class).to(PGSQLCatalogStatusHandlerDAO.class);

        bind(PGSQLObservedPropertyDAO.class);

        log.debug("Configured {}", this);
    }

}

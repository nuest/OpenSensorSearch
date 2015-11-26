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
package org.n52.sir.ds;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.n52.oss.db.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jan Schulte
 * 
 */
public abstract class AbstractConnectionPool implements ConnectionPool {

    private static final Logger log = LoggerFactory.getLogger(AbstractConnectionPool.class);

    protected BasicDataSource dataSource = new BasicDataSource();

    /**
     * constructor with necessary parameters as strings
     * 
     * @param connection
     *        connection url
     * @param user
     *        db username
     * @param password
     *        db password
     * @param driverName
     *        classname of the db driver
     * @param initConnections
     *        number of initial connections
     * @param maxConnections
     *        maximal number of connections in the pool
     */
    public AbstractConnectionPool(String connection,
                                  String user,
                                  String password,
                                  String driverName,
                                  int initConnections,
                                  int maxConnections) {
        this.dataSource.setDriverClassName(driverName);
        this.dataSource.setUsername(user);
        this.dataSource.setPassword(password);
        this.dataSource.setUrl(connection);

        // max connections active
        this.dataSource.setMaxActive(maxConnections);
        this.dataSource.setMaxIdle(maxConnections);

        // initial size of connection pool
        this.dataSource.setInitialSize(initConnections);

        this.dataSource.setMaxWait(5000);

        // important! allow access to underlying connection
        this.dataSource.setAccessToUnderlyingConnectionAllowed(true);

        log.info("NEW {}", this);
    }

    @Override
    public abstract Connection getConnection() throws SQLException;

}
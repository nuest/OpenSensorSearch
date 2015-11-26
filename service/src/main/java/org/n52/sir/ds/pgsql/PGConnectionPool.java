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
package org.n52.sir.ds.pgsql;

import java.sql.Connection;
import java.sql.SQLException;

import org.n52.sir.ds.AbstractConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection Pool for PostgreSQL databases. Class implements the abstract class ConnectionPool and implements
 * the getNewConnection method.
 * 
 * @author Jan Schulte
 * 
 */
public class PGConnectionPool extends AbstractConnectionPool {
    
    private static final Logger log = LoggerFactory.getLogger(PGConnectionPool.class);

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
     *        maximal number of connection pool failed
     */
    public PGConnectionPool(String connection,
                            String user,
                            String password,
                            String driverName,
                            int initConnections,
                            int maxConnections) {

        super(connection, user, password, driverName, initConnections, maxConnections);

        log.info(" ***** NEW connection pool: {} ****** ", this);
    }

    @Override
    public Connection getConnection() throws SQLException {
        // pooled connection
        Connection conn;

        try {
            conn = this.dataSource.getConnection();
        }
        catch (SQLException sqle) {
            if (this.dataSource.getNumActive() == this.dataSource.getMaxActive()) {
                // OwsExceptionReport se = new OwsExceptionReport();
                // se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                // "PGConnection.getConnection()",
                // "All db connections are in use. Please try again later! " + sqle.toString());

                log.debug("All db connections are in use. Please try again later! Error: {}", sqle.toString());
                // throw se;
            }

            // OwsExceptionReport se = new OwsExceptionReport();
            // se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
            // "PGConnection.getConnection()",
            // "Could not get connection from connection pool. Please make sure that your database server is running and configured properly. "
            // + sqle.toString());
            log.debug("Could not get connection from connection pool. Please make sure that your database server is running and configured properly. Error: {}",
                      sqle.toString());

            throw sqle;
        }

        return conn;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PGConnectionPool [");
        if (this.dataSource != null) {
            builder.append("dataSource user name=");
            builder.append(this.dataSource.getUsername());
        }
        builder.append("]");
        return builder.toString();
    }

}
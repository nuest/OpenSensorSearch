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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.catalog.CatalogConnectionImpl;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.ds.IConnectToCatalogDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 *
 */
public class PGSQLConnectToCatalogDAO implements IConnectToCatalogDAO {

    private static final Logger log = LoggerFactory.getLogger(PGSQLConnectToCatalogDAO.class);

    private PGConnectionPool cpool;

    @Inject
    public PGSQLConnectToCatalogDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    private String catalogConnectionsListQuery() {
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM ");
        query.append(PGDAOConstants.catalog);
        query.append(";");

        return query.toString();
    }

    @Override
    public List<ICatalogConnection> getCatalogConnectionList() throws OwsExceptionReport {
        ArrayList<ICatalogConnection> connections = new ArrayList<>();
        String catalogConnectionList = catalogConnectionsListQuery();

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", catalogConnectionList);
            try (ResultSet rs = stmt.executeQuery(catalogConnectionList);) {
                if (rs == null)
                    return connections;

                while (rs.next()) {
                    String connectionID = rs.getString(PGDAOConstants.catalogIdSir);
                    URL url = new URL(rs.getString(PGDAOConstants.catalogUrl));
                    int pushInterval = rs.getInt(PGDAOConstants.pushInterval);
                    String status = rs.getString(PGDAOConstants.catalogStatus);
                    connections.add(new CatalogConnectionImpl(connectionID, url, pushInterval, status));
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while requesting a connection to catalog from database: " + sqle.getMessage());
            log.error("Error while requesting a connection to catalog from database: " + sqle.getMessage());
        }
        catch (MalformedURLException e) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while requesting a connection to catalog from database: " + e.getMessage());
            PGSQLConnectToCatalogDAO.log.error("Error while requesting a connection to catalog from database: "
                    + e.getMessage());
        }

        return connections;
    }

    @Override
    public String getConnectionID(URL cswUrl, int pushInterval) throws OwsExceptionReport {
        String connectionID = null;
        String getConnectionQuery = getConnectionQuery(cswUrl);

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", getConnectionQuery);
            try (ResultSet rs = stmt.executeQuery(getConnectionQuery);) {
                while (rs.next()) {
                    connectionID = rs.getString(PGDAOConstants.catalogIdSir);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while requesting a connection to catalog from database: " + sqle.getMessage());
            PGSQLConnectToCatalogDAO.log.error("Error while requesting a connection to catalog from database: "
                    + sqle.getMessage());
        }

        return connectionID;
    }

    private String getConnectionQuery(URL cswUrl) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(" FROM ");
        query.append(PGDAOConstants.catalog);
        query.append(" WHERE (");
        query.append(PGDAOConstants.catalogUrl);
        query.append(" = '");
        query.append(cswUrl.toString());
        query.append("');");

        return query.toString();
    }

    private String insertCatalogQuery(URL cswUrl, int pushInterval) {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ");
        query.append(PGDAOConstants.catalog);
        query.append(" (");
        query.append(PGDAOConstants.catalogUrl);
        query.append(", ");
        query.append(PGDAOConstants.pushInterval);
        query.append(", ");
        query.append(PGDAOConstants.catalogStatus);
        query.append(") SELECT '");
        query.append(cswUrl.toString());
        query.append("', '");
        query.append(pushInterval);
        query.append("', '");
        query.append(ICatalogConnection.NEW_CONNECTION_STATUS);
        query.append("'  RETURNING ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(";");

        return query.toString();
    }

    @Override
    public String insertConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport {
        String connectionID = null;
        String insertCatalogQuery = insertCatalogQuery(cswUrl, pushInterval);

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            PGSQLConnectToCatalogDAO.log.debug(">>>Database Query: {}", insertCatalogQuery);
            try (ResultSet rs = stmt.executeQuery(insertCatalogQuery);) {
                while (rs.next()) {
                    connectionID = rs.getString(PGDAOConstants.catalogIdSir);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while adding a connection to catalog to database: " + sqle.getMessage());
            log.error("Error while adding a connection to catalog to database: " + sqle.getMessage());
        }

        return connectionID;
    }

    @Override
    public void updateConnection(URL cswUrl, int pushInterval) throws OwsExceptionReport {
        String updateConnectionQuery = updateConnectionQuery(cswUrl, pushInterval);

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", updateConnectionQuery);
            stmt.execute(updateConnectionQuery);
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while requesting a connection to catalog from database: " + sqle.getMessage());
            PGSQLConnectToCatalogDAO.log.error("Error while requesting a connection to catalog from database: "
                    + sqle.getMessage());
        }
    }

    private String updateConnectionQuery(URL cswUrl, int pushInterval) {
        StringBuilder query = new StringBuilder();

        query.append("UPDATE ");
        query.append(PGDAOConstants.catalog);
        query.append(" SET ");
        query.append(PGDAOConstants.pushInterval);
        query.append(" = '");
        query.append(pushInterval);
        query.append("' WHERE ");
        query.append(PGDAOConstants.catalogUrl);
        query.append(" = '");
        query.append(cswUrl);
        query.append("';");

        return query.toString();
    }
}

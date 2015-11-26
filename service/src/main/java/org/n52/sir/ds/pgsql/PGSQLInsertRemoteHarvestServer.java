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
/**
 * @author Yakoub
 */

package org.n52.sir.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.n52.sir.ds.IInsertRemoteHarvestServer;
import org.n52.sir.util.SHA1HashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Daniel, Moh-Yakoub
 * 
 */
public class PGSQLInsertRemoteHarvestServer implements IInsertRemoteHarvestServer {
    private static final Logger log = LoggerFactory.getLogger(PGSQLInsertRemoteHarvestServer.class);

    private PGConnectionPool cpool;

    public PGSQLInsertRemoteHarvestServer(PGConnectionPool cpool) {
        this.cpool = cpool;

    }

    @Override
    public String insertRemoteServer(String url) {
        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            String insertQuery = insertRemoteServerString(url);
            log.debug("Query: {}", insertQuery);

            stmt.execute(insertQuery);
            String authtoken = null;

            try (ResultSet rs = stmt.executeQuery(searchByURLQuery(url));) {
                if (rs.next()) {
                    authtoken = rs.getString(PGDAOConstants.AUTH_TOKEN);
                }
            }

            return authtoken;
        }
        catch (Exception e) {
            log.error("Cannot insert harvest Script", e);
            return null;
        }

    }

    public String getRemoteSensorServer(String auth_token) {
        String url = null;

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            try (ResultSet rs = stmt.executeQuery(searchByAuthTokenQuery(auth_token));) {
                if (rs.next()) {
                    url = rs.getString(PGDAOConstants.SERVER_URL);
                }
            }

            return url;
        }
        catch (Exception e) {
            log.error("Cannot insert harvest Script", e);
            return null;
        }
    }

    @Override
    public int getRemoteServerHarvestState(String authToken) {
        return 0;
    }

    @Override
    public String harvestRemoteServer(String authToken) {
        return getRemoteSensorServer(authToken);
    }

    private String insertRemoteServerString(String url) {
        String hash = new Date().getTime() + url;
        String _hash = new SHA1HashGenerator().generate(hash);
        if (_hash == null) {
            log.error("Cannot create SHA1 hash");
            return null;
        }
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ");
        query.append(PGDAOConstants.remoteHarvestSensor);
        query.append("(");
        query.append(PGDAOConstants.SERVER_URL);
        query.append(",");
        query.append(PGDAOConstants.AUTH_TOKEN);
        query.append(") values(");
        query.append("'");
        query.append(url);
        query.append("'");
        query.append(",");
        query.append("'");
        query.append(_hash);
        query.append("'");
        query.append(");");
        log.info(query.toString());
        return query.toString();
    }

    private String searchByAuthTokenQuery(String auth_token) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(PGDAOConstants.SERVER_URL);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.remoteHarvestSensor);
        builder.append(" WHERE ");
        builder.append(PGDAOConstants.AUTH_TOKEN);
        builder.append(" LIKE ");
        builder.append("'");
        builder.append(auth_token);
        builder.append("'");
        return builder.toString();
    }

    private String searchByURLQuery(String url) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(PGDAOConstants.AUTH_TOKEN);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.remoteHarvestSensor);
        builder.append(" WHERE ");
        builder.append(PGDAOConstants.SERVER_URL);
        builder.append(" LIKE ");
        builder.append("'");
        builder.append(url);
        builder.append("'");
        return builder.toString();
    }

    private String updateState(String auth_token, int state) {
        // TODO yakoub implement updating harvest state HAR31
        return "";
    }

}
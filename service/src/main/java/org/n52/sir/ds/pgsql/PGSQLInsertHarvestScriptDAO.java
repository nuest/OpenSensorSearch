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
/** @author Yakoub
 */

package org.n52.sir.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.n52.sir.ds.IInsertHarvestScriptDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Daniel, Moh-Yakoub
 * 
 */
public class PGSQLInsertHarvestScriptDAO implements IInsertHarvestScriptDAO {

    private static final Logger log = LoggerFactory.getLogger(PGSQLInsertHarvestScriptDAO.class);

    private PGConnectionPool cpool;

    public PGSQLInsertHarvestScriptDAO() {

    }

    public PGSQLInsertHarvestScriptDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public String insertScript(String path, String username, int version, int userid) {
        String insertId = null;

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            String insertQuery = insertScriptString(path, username, version, userid);

            log.debug("Insert query: {}", insertQuery);
            stmt.execute(insertQuery);

            try (ResultSet rs = stmt.executeQuery(searchByPath(path));) {
                if (rs.next())
                    insertId = rs.getString(PGDAOConstants.SCRIPTID);
            }
        }
        catch (Exception e) {
            log.error("Cannot insert harvest Script", e);
        }

        return insertId;
    }

    private String getPathById(String id) {
        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            String searchQuery = searchPathById(id);
            log.debug("Search query: {}", searchQuery);

            String path = null;
            String user = null;

            try (ResultSet rs = stmt.executeQuery(searchQuery);) {
                if (rs.next()) {
                    path = rs.getString(PGDAOConstants.PATH_URL);
                    user = rs.getString(PGDAOConstants.SCRIPT_OWNER_USERNAME);
                }
            }

            return user + "/" + path;
        }
        catch (Exception e) {
            log.error("Cannot search for harvest Script", e);
            return null;
        }
    }

    private String insertScriptString(String path, String username, int version, int userid) {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ");
        query.append(PGDAOConstants.harvestScript);
        query.append("(");
        query.append(PGDAOConstants.SCRIPT_OWNER_USERNAME);
        query.append(",");
        query.append(PGDAOConstants.PATH_URL);
        query.append(",");
        query.append(PGDAOConstants.SCRIPT_VERSION);
        query.append(",");
        query.append(PGDAOConstants.USER_ID);
        query.append(") values(");
        query.append("'");
        query.append(username);
        query.append("'");
        query.append(",");
        query.append("'");
        query.append(path);
        query.append("'");
        query.append(",");
        query.append("'");
        query.append(version);
        query.append("'");
        query.append(",");
        query.append(userid);
        query.append(");");
        log.info(query.toString());
        return query.toString();
    }

    private String searchByPath(String path) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(PGDAOConstants.SCRIPTID);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.harvestScript);
        builder.append(" WHERE ");
        builder.append(PGDAOConstants.PATH_URL);
        builder.append(" LIKE ");
        builder.append("'");
        builder.append(path);
        builder.append("'");
        return builder.toString();
    }

    private String searchPathById(String Id) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(PGDAOConstants.PATH_URL);
        builder.append(",");
        builder.append(PGDAOConstants.SCRIPT_OWNER_USERNAME);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.harvestScript);
        builder.append(" WHERE ");
        builder.append(PGDAOConstants.SCRIPTID);
        builder.append("=");
        builder.append(Id);
        return builder.toString();

    }

    private String userIdForScript(String id) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(PGDAOConstants.USER_ID);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.harvestScript);
        builder.append(" WHERE ");
        builder.append(PGDAOConstants.SCRIPTID);
        builder.append("=");
        builder.append(id);

        return builder.toString();

    }

    @Override
    public String getScriptPath(String identifier) {
        return getPathById(identifier);
    }

    @Override
    public String getScriptUserId(int scriptId) {
        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            String query = userIdForScript(scriptId + "");
            log.debug("Query: {}", query);

            String id = null;
            try (ResultSet rs = stmt.executeQuery(query);) {
                if (rs.next()) {
                    id = rs.getString(PGDAOConstants.USER_ID);
                }
            }
            return id;
        }
        catch (Exception e) {
            log.error("Cannot insert harvest Script", e);
            return null;
        }
    }

    @Override
    public String getScriptFileForID(int scriptId) {
        String path = getPathById(scriptId + "");
        return path;
    }

}

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
import java.util.Date;

import org.n52.sir.ds.IUserAccountDAO;
import org.n52.sir.util.SHA1HashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PGSQLUserAccountDAO implements IUserAccountDAO {
    /**
     * the logger, used to log exceptions and additionally information
     */
    private static final Logger log = LoggerFactory.getLogger(PGSQLUserAccountDAO.class);

    /**
     * Connection pool for creating connections to the DB
     */
    private PGConnectionPool cpool;

    public PGSQLUserAccountDAO() {

    }

    public PGSQLUserAccountDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    public String insertScript(String path,
            String username,
            int version)
    {
        String insert;
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String insertQuery = insertScriptString(path, username, version);
            System.out.println(insertQuery);
            log.info(insertQuery);
            stmt.execute(insertQuery);
            String id = null;
            ResultSet rs = stmt.executeQuery(searchByPath(path));
            if (rs.next()) {
                id = rs.getString(PGDAOConstants.SCRIPTID);
            }
            return id;
        } catch (Exception e) {
            log.error("Cannot insert harvest Script", e);
            return null;
        }
    }

    private String insertScriptString(String path,
            String username,
            int version)
    {
        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ");
        query.append(PGDAOConstants.harvestScript);
        query.append("(");
        query.append(PGDAOConstants.SCRIPT_OWNER_USERNAME);
        query.append(",");
        query.append(PGDAOConstants.PATH_URL);
        query.append(",");
        query.append(PGDAOConstants.SCRIPT_VERSION);
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
        query.append(");");
        log.info(query.toString());
        System.out.println(query.toString());
        return query.toString();
    }

    private String searchByPath(String path)
    {
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

    public boolean isAdmin(String username)
    {
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String query = isAdminQuery(username);
            log.debug(query);
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            boolean isadmin;
            if (rs.next()) {
                isadmin = rs.getBoolean(PGDAOConstants.USER_IS_ADMIN);
            } else
                return false;
            return isadmin;

        } catch (Exception e) {
            log.error("Cannot find admin status", e);
            return false;
        }
    }

    public String userNameForId(String id)
    {
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String query = userNameByIdQuery(id);
            log.debug(query);
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            String name = null;
            if (rs.next()) {
                name = rs.getString(PGDAOConstants.USER_NAME);
            } else
                return null;
            return name;
        } catch (Exception e) {
            log.error("Cannot find user name", e);
            return null;
        }
    }

    public boolean validate(String id)
    {
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String query = validateQuery(id);
            log.debug(query);
            System.out.println(query);
            return stmt.execute(query);
        } catch (Exception e) {
            log.error("Cannot find validate user status", e);
            return false;
        }
    }

    public boolean isValid(String username)
    {
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String query = isValidQuery(username);
            log.debug(query);
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            boolean isadmin;
            if (rs.next()) {
                isadmin = rs.getBoolean(PGDAOConstants.USER_IS_VALID);
            } else
                return false;
            return isadmin;

        } catch (Exception e) {
            log.error("Cannot find admin status", e);
            return false;
        }

    }

    @Override
    public String authenticateUser(String name,
            String password)
    {

        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String searchQuery = selectUserPassword(name, password);
            log.debug(searchQuery);
            System.out.println(searchQuery);
            ResultSet rs = stmt.executeQuery(searchQuery);
            String id = null;
            if (rs.next()) {
                id = rs.getObject(PGDAOConstants.USER_ID).toString();
            } else
                return null;
            stmt.execute(deleteUserWithID(id));
            stmt.execute(insertAuthToken(name, id));
            rs = stmt.executeQuery(authTokenForUser(id));
            if (rs.next()) {
                return rs.getString(PGDAOConstants.USER_AUTH_TOKEN);
            } else
                return null;
        } catch (Exception e) {
            log.error("Cannot insert harvest Script", e);
            return null;
        }

    }

    private String selectUserPassword(String name,
            String password)
    {
        String hash = new SHA1HashGenerator().generate(password);
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(PGDAOConstants.USER_ID);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.USER_ACCOUNT_TABLE);
        builder.append(" WHERE ");
        builder.append(PGDAOConstants.USER_NAME);
        builder.append(" like '");
        builder.append(name);
        builder.append("' AND ");
        builder.append(PGDAOConstants.PASSWORD_HASH);
        builder.append(" like '");
        builder.append(hash);
        builder.append("'");
        return builder.toString();
    }

    private String insertAuthToken(String name,
            String id)
    {
        String seed = name + (new Date().getTime());
        String hash = new SHA1HashGenerator().generate(seed);
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(PGDAOConstants.AUTH_TOKEN_TABLE);
        builder.append("(");
        builder.append(PGDAOConstants.USER_ID);
        builder.append(",");
        builder.append(PGDAOConstants.USER_AUTH_TOKEN);
        builder.append(") values(");
        builder.append(id);
        builder.append(",");
        builder.append("'");
        builder.append(hash);
        builder.append("'");
        builder.append(");");
        return builder.toString();
    }

    private String deleteUserWithID(String id)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ");
        builder.append(PGDAOConstants.AUTH_TOKEN_TABLE);
        builder.append(" WHERE ");
        builder.append(PGDAOConstants.USER_ID);
        builder.append("=");
        builder.append(id);
        return builder.toString();
    }

    private String authTokenForUser(String id)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT  ");
        builder.append(PGDAOConstants.USER_AUTH_TOKEN);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.AUTH_TOKEN_TABLE);
        builder.append(" WHERE ");
        builder.append(PGDAOConstants.USER_ID);
        builder.append(" = ");
        builder.append(id);
        return builder.toString();
    }

    private String UserIDForAuthToken(String authToken)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT  ");
        builder.append(PGDAOConstants.USER_ID);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.AUTH_TOKEN_TABLE);
        builder.append(" WHERE ");
        builder.append(PGDAOConstants.USER_AUTH_TOKEN);
        builder.append(" like ");
        builder.append("'");
        builder.append(authToken);
        builder.append("'");
        return builder.toString();
    }

    private String UserIDForUsernameQuery(String userName)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT  ");
        builder.append(PGDAOConstants.USER_ID);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.USER_ACCOUNT_TABLE);
        builder.append(" WHERE ");
        builder.append(PGDAOConstants.USER_NAME);
        builder.append(" like ");
        builder.append("'");
        builder.append(userName);
        builder.append("'");
        return builder.toString();
    }

    @Override
    public String getUserIDForToken(String token)
    {
        Connection con = null;
        Statement stmt = null;

        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String searchQuery = UserIDForAuthToken(token);
            log.info(searchQuery);
            System.out.println(searchQuery);
            ResultSet rs = stmt.executeQuery(searchQuery);
            String id = null;
            if (rs.next()) {
                id = rs.getObject(PGDAOConstants.USER_ID).toString();
            } else
                return null;
            return id;
        } catch (Exception e) {
            log.error("Cannot getUserIf", e);
            return null;
        }
    }

    private String isAdminQuery(String username)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(PGDAOConstants.USER_IS_ADMIN);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.USER_ACCOUNT_TABLE);
        builder.append(" WHERE  ");
        builder.append(PGDAOConstants.USER_NAME);
        builder.append(" like '");
        builder.append(username);
        builder.append("'");
        return builder.toString();
    }

    private String isValidQuery(String username)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(PGDAOConstants.USER_IS_VALID);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.USER_ACCOUNT_TABLE);
        builder.append(" WHERE  ");
        builder.append(PGDAOConstants.USER_NAME);
        builder.append(" like '");
        builder.append(username);
        builder.append("'");
        return builder.toString();
    }

    private String validateQuery(String id)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ");
        builder.append(PGDAOConstants.USER_ACCOUNT_TABLE);
        builder.append(" SET ");
        builder.append(PGDAOConstants.USER_IS_VALID);
        builder.append(" = 'true'  WHERE ");
        builder.append(PGDAOConstants.USER_ID);
        builder.append(" = ");
        builder.append(id);
        return builder.toString();
    }

    private String userNameByIdQuery(String id)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(PGDAOConstants.USER_NAME);
        builder.append(" FROM ");
        builder.append(PGDAOConstants.USER_ACCOUNT_TABLE);
        builder.append("  WHERE ");
        builder.append(PGDAOConstants.USER_ID);
        builder.append(" = ");
        builder.append(id);
        return builder.toString();
    }

    @Override
    public boolean nameExists(String name)
    {
        Connection con = null;
        Statement stmt = null;
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String query = isValidQuery(name);
            log.debug(query);
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            return rs.next();
        } catch (Exception e) {
            log.error("Cannot find admin status", e);
            return false;
        }

    }

    @Override
    public boolean register(String name,
            String passwordHash)
    {
        Connection con = null;
        Statement stmt = null;
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String query = registerQuery(name, passwordHash);
            log.debug(query);
            System.out.println(query);
            stmt.execute(query);
            return true;
        } catch (Exception e) {
            log.error("Cannot find admin status", e);
            return false;
        }

    }

    private String registerQuery(String name,
            String passwordHash)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO  ");
        builder.append(PGDAOConstants.USER_ACCOUNT_TABLE);
        builder.append(" ( ");
        builder.append(PGDAOConstants.USER_NAME);
        builder.append(",");
        builder.append(PGDAOConstants.PASSWORD_HASH);
        builder.append(",");
        builder.append(PGDAOConstants.USER_IS_ADMIN);
        builder.append(",");
        builder.append(PGDAOConstants.USER_IS_VALID);
        builder.append(") VALUES (");
        builder.append("'");
        builder.append(name);
        builder.append("'");
        builder.append(",");
        builder.append("'");
        builder.append(passwordHash);
        builder.append("'");
        builder.append(',');
        builder.append("'");
        builder.append("false");
        builder.append("'");
        builder.append(",");
        builder.append("'");
        builder.append("false");
        builder.append("')");

        return builder.toString();

    }

    @Override
    public String getUserIDForUsername(String username)
    {
        Connection con = null;
        Statement stmt = null;
        try {
            con = this.cpool.getConnection();
            stmt = con.createStatement();
            String query = UserIDForUsernameQuery(username);
            log.debug(query);
            System.out.println(query);
            String id = null;
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                id = rs.getString(PGDAOConstants.USER_ID);
            }
            return id;
        } catch (Exception e) {
            log.error("Cannot find admin status", e);
            return null;
        }
    }

}

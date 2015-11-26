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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.sir.api.SirService;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sir.catalog.CatalogConnectionImpl;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.ds.IGetCapabilitiesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class PGSQLGetCapabilitiesDAO implements IGetCapabilitiesDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLGetCapabilitiesDAO.class);

    private PGConnectionPool cpool;

    @Inject
    public PGSQLGetCapabilitiesDAO(PGConnectionPool cpool) {
        this.cpool = cpool;

        log.debug("NEW {}", this);
    }

    @Override
    public Collection<ICatalogConnection> getAllCatalogConnections() throws OwsExceptionReport {
        ArrayList<ICatalogConnection> result = new ArrayList<>();

        StringBuffer query = new StringBuffer();
        query.append("SELECT ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(", ");
        query.append(PGDAOConstants.catalogUrl);
        query.append(", ");
        query.append(PGDAOConstants.pushInterval);
        query.append(", ");
        query.append(PGDAOConstants.catalogStatus);
        query.append(" FROM ");
        query.append(PGDAOConstants.catalog);
        query.append(";");

        try (Connection con = this.cpool.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query.toString());) {
            log.debug(">>>Database Query: {}", query.toString());

            // if no phenomenon available give back empty list
            if (rs == null) {
                return result;
            }

            // get result as string
            while (rs.next()) {
                result.add(new CatalogConnectionImpl(rs.getString(PGDAOConstants.catalogIdSir),
                                                     new URL(rs.getString(PGDAOConstants.catalogUrl)),
                                                     rs.getInt(PGDAOConstants.pushInterval),
                                                     rs.getString(PGDAOConstants.catalogStatus)));
            }

            con.close();
        }
        catch (SQLException | MalformedURLException e) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query catalog connections for the getCapabilities from database!", e);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, e);
            throw se;
        }

        return result;
    }

    @Override
    public long getPhenomenonCount() throws OwsExceptionReport {
        String tableName = PGDAOConstants.phenomenon;
        return getTableSize(tableName);
    }

    @Override
    public Collection<String> getAllPhenomenonAllURNs() throws OwsExceptionReport {
        ArrayList<String> result = new ArrayList<>();

        StringBuffer query = new StringBuffer();

        // build query
        query.append("SELECT " + PGDAOConstants.phenomenonUrn + " FROM " + PGDAOConstants.phenomenon + ";");

        // execute query
        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", query.toString());

            try (ResultSet rs = stmt.executeQuery(query.toString());) {
                // if no phenomenon available give back empty list
                if (rs == null) {
                    return result;
                }

                // get result as string
                while (rs.next()) {
                    String phenom = rs.getString(PGDAOConstants.phenomenonUrn);
                    result.add(phenom);
                }
            }
        }
        catch (SQLException e) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query phenomenon for the getCapabilities from database!", e);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, e);
            throw se;
        }

        return result;
    }

    @Override
    public long getSensorCount() throws OwsExceptionReport {
        String tableName = PGDAOConstants.sensor;
        return getTableSize(tableName);
    }

    @Override
    public long getServiceCount() throws OwsExceptionReport {
        String tableName = PGDAOConstants.service;
        return getTableSize(tableName);
    }

    @Override
    public Collection<SirService> getAllServices() throws OwsExceptionReport {
        ArrayList<SirService> result = new ArrayList<>();

        StringBuffer query = new StringBuffer();
        query.append("SELECT ");
        query.append(PGDAOConstants.serviceUrl);
        query.append(", ");
        query.append(PGDAOConstants.serviceType);
        query.append(", ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(";");

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", query.toString());

            try (ResultSet rs = stmt.executeQuery(query.toString());) {
                if (rs == null) {
                    return result;
                }

                while (rs.next()) {
                    SirService serv = new SirService(rs.getString(PGDAOConstants.serviceId),
                                                     rs.getString(PGDAOConstants.serviceUrl),
                                                     rs.getString(PGDAOConstants.serviceType));
                    result.add(serv);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query services for the getCapabilities from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

        return result;
    }

    @Override
    public SirService getService(String id) throws OwsExceptionReport {
        StringBuffer query = new StringBuffer();
        query.append("SELECT ");
        query.append(PGDAOConstants.serviceUrl);
        query.append(", ");
        query.append(PGDAOConstants.serviceType);
        query.append(", ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE ");
        query.append(PGDAOConstants.serviceId);
        query.append(" = '");
        query.append(id);
        query.append("';");

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", query.toString());

            try (ResultSet rs = stmt.executeQuery(query.toString());) {
                if (rs == null) {
                    return null;
                }

                rs.next();
                SirService serv = new SirService(rs.getString(PGDAOConstants.serviceId),
                                                 rs.getString(PGDAOConstants.serviceUrl),
                                                 rs.getString(PGDAOConstants.serviceType));
                return serv;
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query services for the getCapabilities from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }
    }

    private long getTableSize(String tableName) throws OwsExceptionReport {
        long result = Long.MIN_VALUE;

        StringBuffer query = new StringBuffer();
        query.append("SELECT COUNT(*) FROM ");
        query.append(tableName);
        query.append(";");

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", query.toString());

            try (ResultSet rs = stmt.executeQuery(query.toString());) {
                // if no phenomenon available give back empty list
                if (rs == null) {
                    return result;
                }

                // get result as long
                while (rs.next()) {
                    result = rs.getLong(1);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query services for " + tableName + " from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

        return result;
    }

}
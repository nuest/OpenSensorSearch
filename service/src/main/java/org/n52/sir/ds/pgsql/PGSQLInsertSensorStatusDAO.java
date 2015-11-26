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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import org.n52.oss.sir.api.InternalSensorID;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.api.SirStatus;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IInsertSensorStatusDAO;
import org.n52.sir.util.GMLDateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class PGSQLInsertSensorStatusDAO implements IInsertSensorStatusDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLInsertSensorStatusDAO.class);

    private PGConnectionPool cpool;

    @Inject
    public PGSQLInsertSensorStatusDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    private String getSensorIdByInternalID(InternalSensorID ident) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensorId);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorId);
        query.append(" = '");
        query.append(ident.getId());
        query.append("');");

        return query.toString();
    }

    private String getSensorIdByServiceDescription(SirServiceReference ident) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append(" = '");
        query.append(ident.getService().getUrl());
        query.append("' AND ");
        query.append(PGDAOConstants.serviceType);
        query.append(" = '");
        query.append(ident.getService().getType());
        query.append("')) AND ");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.sensorId);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" = '");
        query.append(ident.getServiceSpecificSensorId());
        query.append("')));");

        return query.toString();
    }

    private String getStatusId(SirStatus status, String sensorId) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.statusId);
        query.append(" FROM ");
        query.append(PGDAOConstants.status);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(" = '");
        query.append(sensorId);
        query.append("' AND ");
        query.append(PGDAOConstants.propertyName);
        query.append(" = '");
        query.append(status.getPropertyName());
        query.append("' AND ");
        query.append(PGDAOConstants.uom);
        query.append(" ='");
        query.append(status.getUom());
        query.append("');");

        return query.toString();
    }

    @Override
    public String insertSensorStatus(SirSensorIdentification ident, Collection<SirStatus> status) throws OwsExceptionReport {
        String sensorId = null;

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {

            // query identification
            // check sensorID in SIR
            if (ident instanceof InternalSensorID) {
                String sensorIdQuery = getSensorIdByInternalID((InternalSensorID) ident);
                log.debug(">>>Database Query: {}", sensorIdQuery);

                try (ResultSet rs = stmt.executeQuery(sensorIdQuery);) {
                    while (rs.next()) {
                        sensorId = rs.getString(PGDAOConstants.sensorId);
                    }
                }
            }
            // check service description
            if (ident instanceof SirServiceReference) {
                String sensorIdQuery = getSensorIdByServiceDescription((SirServiceReference) ident);
                log.debug(">>>Database Query: {}", sensorIdQuery);

                try (ResultSet rs = stmt.executeQuery(sensorIdQuery);) {
                    while (rs.next()) {
                        sensorId = rs.getString(PGDAOConstants.sensorId);
                    }
                }
            }
            if (sensorId == null) {
                OwsExceptionReport se = new OwsExceptionReport();
                se.addCodedException(ExceptionCode.NoApplicableCode, null, "Unknown sensor identification!");
                log.error("Unknown sensor identification!");
            }

            for (SirStatus sirStatus : status) {
                // get status id
                String statusID = null;
                String statusIdQuery = getStatusId(sirStatus, sensorId);
                log.debug(">>> Database Query: {}", statusIdQuery);

                try (ResultSet rs = stmt.executeQuery(statusIdQuery);) {
                    while (rs.next()) {
                        statusID = rs.getString(PGDAOConstants.statusId);
                    }
                    if (statusID == null) {
                        // insert status
                        String insertSensorStatus = insertStatus(sirStatus, sensorId);
                        log.debug(">>> Database Query: {}", insertSensorStatus);

                        try (ResultSet rs2 = stmt.executeQuery(insertSensorStatus);) {
                            while (rs2.next()) {
                                sensorId = rs2.getString(PGDAOConstants.sensorIdSirOfStatus);
                            }
                        }
                    }
                    else {
                        // update status
                        String updateSensorStatus = updateStatus(sirStatus, statusID);
                        log.debug(">>> Database Query: " + updateSensorStatus);
                        stmt.execute(updateSensorStatus);
                    }
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while adding a sensor status to database: " + sqle.getMessage());
            log.error("Error while adding a sensor status to database: " + sqle.getMessage());
            throw se;
        }

        return sensorId;
    }

    private String insertStatus(SirStatus status, String sensorId) {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ");
        query.append(PGDAOConstants.status);
        query.append(" (");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(", ");
        query.append(PGDAOConstants.propertyName);
        query.append(", ");
        query.append(PGDAOConstants.propertyValue);
        query.append(", ");
        query.append(PGDAOConstants.time);
        query.append(", ");
        query.append(PGDAOConstants.uom);
        query.append(") SELECT '");
        query.append(sensorId);
        query.append("', '");
        query.append(status.getPropertyName());
        query.append("', '");
        query.append(status.getPropertyValue());
        query.append("', '");
        query.append(GMLDateParser.getInstance().parseDate(status.getTimestamp()));
        query.append("', '");
        query.append(status.getUom());
        query.append("' WHERE NOT EXISTS (SELECT ");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(", ");
        query.append(PGDAOConstants.propertyName);
        query.append(", ");
        query.append(PGDAOConstants.uom);
        query.append(" FROM ");
        query.append(PGDAOConstants.status);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(" = '");
        query.append(sensorId);
        query.append("' AND ");
        query.append(PGDAOConstants.propertyName);
        query.append(" = '");
        query.append(status.getPropertyName());
        query.append("' AND ");
        query.append(PGDAOConstants.uom);
        query.append(" ='");
        query.append(status.getUom());
        query.append("')) RETURNING ");
        query.append(PGDAOConstants.sensorIdSirOfStatus);
        query.append(";");

        return query.toString();
    }

    private String updateStatus(SirStatus status, String statusID) {
        StringBuilder query = new StringBuilder();

        query.append("UPDATE ");
        query.append(PGDAOConstants.status);
        query.append(" SET ");
        query.append(PGDAOConstants.propertyValue);
        query.append(" = '");
        query.append(status.getPropertyValue());
        query.append("', ");
        query.append(PGDAOConstants.time);
        query.append(" = '");
        query.append(GMLDateParser.getInstance().parseDate(status.getTimestamp()));
        query.append("' WHERE ");
        query.append(PGDAOConstants.statusId);
        query.append(" = '");
        query.append(statusID);
        query.append("';");

        return query.toString();
    }
}

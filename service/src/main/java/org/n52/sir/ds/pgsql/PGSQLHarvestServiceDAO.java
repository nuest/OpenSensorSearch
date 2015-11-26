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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.n52.oss.sir.api.ObservedProperty;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IHarvestServiceDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class PGSQLHarvestServiceDAO implements IHarvestServiceDAO {

    private static final Logger log = LoggerFactory.getLogger(PGSQLHarvestServiceDAO.class);

    private PGConnectionPool cpool;

    private PGSQLObservedPropertyDAO obsPropDao;

    @Inject
    public PGSQLHarvestServiceDAO(PGConnectionPool cpool, PGSQLObservedPropertyDAO obsPropDao) {
        this.cpool = cpool;
        this.obsPropDao = obsPropDao;
    }

    @Override
    public String addService(String serviceUrl, String serviceType) throws OwsExceptionReport {
        String result = "";

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            // insert service
            String insertService = insertServiceCommand(serviceUrl, serviceType);
            log.debug(">>>Database Query: {}", insertService.toString());

            stmt.execute(insertService.toString());

            // service ID query
            String serviceIDQuery = serviceIDQuery(serviceUrl, serviceType);
            log.debug(">>>Database Query: {}", serviceIDQuery.toString());

            try (ResultSet rs = stmt.executeQuery(serviceIDQuery.toString());) {
                if (rs == null)
                    return result;

                while (rs.next())
                    result = rs.getString(PGDAOConstants.serviceId);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while adding service to database: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode, null, "Error while adding service to database: "
                    + sqle.getMessage());
            throw se;
        }

        return result;
    }

    @Override
    public SirSensor insertSensor(SirSensor sensor) throws OwsExceptionReport {
        String id = sensor.getInternalSensorID();
        if (id == null || id.isEmpty()) {
            log.error("internal ID must be set outside of dao.");
            throw new RuntimeException("internal ID must be set before inserting sensor into DAO.");
        }

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            // insert in sensor table
            String insertSensor = insertSensorCommand(sensor);

            log.debug(">>>Database Query (partial): {}",
                      insertSensor.toString().substring(0, Math.min(500, insertSensor.toString().length())));

            try (ResultSet rs = stmt.executeQuery(insertSensor);) {
                String sensorIdSir = null;
                if (rs.next())
                    sensorIdSir = rs.getString(PGDAOConstants.sensorIdSir);
                // sensor.setInternalSensorId(rs.getString(PGDAOConstants.sensorId));
                else
                    log.warn("Did not receive result set when inserting sensor!");

                // if (sensor.getInternalSensorID() != null) {
                if (sensorIdSir != null) {
                    for (ObservedProperty obsProp : sensor.getObservedProperties()) {

                        // FIXME "add or get id" procedure for observed properties
                        String obsPropId = "";
                        String insertObsProp = this.obsPropDao.insertObservedPropertyCommand(obsProp);
                        log.debug(">>>Database Query: {}", insertObsProp);

                        try (ResultSet rs2 = stmt.executeQuery(insertObsProp);) {
                            while (rs2.next()) {
                                obsPropId = rs.getString(PGDAOConstants.obsPropId);
                            }
                            if (obsPropId.isEmpty()) {
                                String idQuery = this.obsPropDao.getIdQuery(obsProp);
                                log.debug(">>>Database Query: {}", idQuery);
                                try (ResultSet rs3 = stmt.executeQuery(idQuery);) {
                                    while (rs3.next()) {
                                        obsPropId = rs.getString(PGDAOConstants.obsPropId);
                                    }
                                }
                            }

                            String insertSensorObsProp = this.obsPropDao.insertSensorObsPropCommand(sensorIdSir,
                                                                                                       obsPropId);
                            log.debug(">>>Database Query: {}", insertSensorObsProp);
                            stmt.execute(insertSensorObsProp);
                        }
                    }

                    // insert in sensor/service table
                    String insertSensorService = insertSensorServiceCommand(sensorIdSir, sensor);
                    log.debug(">>>Database Query: {}", insertSensorService);
                    stmt.execute(insertSensorService);

                    // end transaction to insert service and sensors
                    log.debug(">>>Database Query: COMMIT;");
                    stmt.execute("COMMIT;");
                }
                else {
                    log.error("Error while adding sensor to database! Possible it is already there...");
                    // OwsExceptionReport se = new OwsExceptionReport();
                    // se.addCodedException(ExceptionCode.NoApplicableCode, null,
                    // "Error while adding sensor to database!");
                    // throw se;
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while adding sensor to database: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while adding sensor to database: " + sqle.getMessage());
            throw se;
        }

        return sensor;
    }

    /**
     * @param sensor
     * @return the database id of the sensor
     */
    private String insertSensorCommand(SirSensor sensor) {
        StringBuffer cmd = new StringBuffer();

        String start = SqlTools.getStartDate(sensor.getTimePeriod());
        String end = SqlTools.getEndDate(sensor.getTimePeriod());

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.sensor);
        cmd.append(" ( ");
        cmd.append(PGDAOConstants.sensorId);
        cmd.append(", ");
        cmd.append(PGDAOConstants.bBox);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeStart);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeEnd);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorml);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorText);
        cmd.append(", ");
        cmd.append(PGDAOConstants.lastUpdate);

        // the content of the fields
        cmd.append(") SELECT '");
        cmd.append(sensor.getInternalSensorID());
        cmd.append("', ");
        cmd.append("ST_GeometryFromText('POLYGON((");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getEast());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getEast());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getSouth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getSouth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append("))',-1), '");
        cmd.append(start);
        cmd.append("', '");
        cmd.append(end);
        cmd.append("', '");

        // check if the document contains characters, namely ' which could break the insert command
        String sensorMLDocument = SqlTools.escapeSQLString(sensor.getSensorMLDocument().xmlText());
        cmd.append(sensorMLDocument);

        cmd.append("', '{");
        if ( !sensor.getText().isEmpty()) {
            for (String text : sensor.getText()) {
                if ( !text.isEmpty()) {
                    cmd.append(SqlTools.escapeSQLString(text));
                    cmd.append(",");
                }
            }
            cmd.deleteCharAt(cmd.length() - 1);
        }
        cmd.append("}', '");
        cmd.append(sensor.getLastUpdate());
        cmd.append("'");

        // check if sensor exists already
        // FIXME this check of non-existence is not complete, should check service_spec_id/service combination
        // as well
        cmd.append(" WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.bBox);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeStart);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeEnd);
        cmd.append(", ");
        // cmd.append(PGDAOConstants.sensorml);
        // cmd.append(", ");
        cmd.append(PGDAOConstants.sensorText);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.sensor);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.bBox);
        cmd.append(" = ST_GeometryFromText('POLYGON((");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getEast());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getEast());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getSouth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getSouth());
        cmd.append(",");
        cmd.append(sensor.getbBox().getWest());
        cmd.append(" ");
        cmd.append(sensor.getbBox().getNorth());
        cmd.append("))',-1)) AND (");
        cmd.append(PGDAOConstants.sensorTimeStart);
        cmd.append(" = '");
        cmd.append(start);
        cmd.append("') AND (");
        cmd.append(PGDAOConstants.sensorTimeEnd);
        cmd.append(" = '");
        cmd.append(end);
        cmd.append("')");

        // do not check sml content
        // cmd.append("AND (");
        // cmd.append(PGDAOConstants.sensorml);
        // cmd.append(" = '");
        // cmd.append(sensorMLDocument);
        // cmd.append("')");

        cmd.append(" AND (");
        cmd.append(PGDAOConstants.sensorText);
        cmd.append(" = '{");
        if ( !sensor.getText().isEmpty()) {
            for (String text : sensor.getText()) {
                cmd.append(SqlTools.escapeSQLString(text));
                cmd.append(",");
            }
            cmd.deleteCharAt(cmd.length() - 1);
        }
        cmd.append("}')) RETURNING ");
        cmd.append(PGDAOConstants.sensorIdSir);

        return cmd.toString();
    }

    private String insertSensorServiceCommand(String sensorId, SirSensor sensor) {
        StringBuffer cmd = new StringBuffer();

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.sensorService);
        cmd.append(" (");
        cmd.append(PGDAOConstants.serviceId);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorIdSir);
        cmd.append(", ");
        cmd.append(PGDAOConstants.serviceSpecId);
        cmd.append(") SELECT (SELECT ");
        cmd.append(PGDAOConstants.serviceId);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.service);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.serviceUrl);
        cmd.append(" = '");
        cmd.append(sensor.getServDescs().iterator().next().getService().getUrl());
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.serviceType);
        cmd.append(" = '");
        cmd.append(sensor.getServDescs().iterator().next().getService().getType());
        cmd.append("')), '");
        // cmd.append(sensor.getInternalSensorID());
        cmd.append(sensorId);
        cmd.append("', '");
        cmd.append(sensor.getServDescs().iterator().next().getServiceSpecificSensorId());
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.serviceId);
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorId);
        cmd.append(", ");
        cmd.append(PGDAOConstants.serviceSpecId);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.sensorService);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.serviceId);
        cmd.append("=(SELECT ");
        cmd.append(PGDAOConstants.serviceId);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.service);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.serviceUrl);
        cmd.append(" = '");
        cmd.append(sensor.getServDescs().iterator().next().getService().getUrl());
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.serviceType);
        cmd.append(" = '");
        cmd.append(sensor.getServDescs().iterator().next().getService().getType());
        cmd.append("')) AND ");
        cmd.append(PGDAOConstants.sensorId);
        cmd.append(" = '");
        cmd.append(sensor.getInternalSensorID());
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.serviceSpecId);
        cmd.append(" = '");
        cmd.append(sensor.getServDescs().iterator().next().getServiceSpecificSensorId());
        cmd.append("'));");

        return cmd.toString();
    }

    /**
     * Builds a command, which insert a service
     * 
     * @param serviceUrl
     *        The service url
     * @param serviceType
     *        The service Type
     * @return Returns an insert service command
     */
    private String insertServiceCommand(String serviceUrl, String serviceType) {
        StringBuilder cmd = new StringBuilder();

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.service);
        cmd.append(" (");
        cmd.append(PGDAOConstants.serviceUrl);
        cmd.append(",");
        cmd.append(PGDAOConstants.serviceType);
        cmd.append(") SELECT '");
        cmd.append(serviceUrl);
        cmd.append("','");
        cmd.append(serviceType);
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.serviceUrl);
        cmd.append(",");
        cmd.append(PGDAOConstants.serviceType);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.service);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.serviceUrl);
        cmd.append("='");
        cmd.append(serviceUrl);
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.serviceType);
        cmd.append("='");
        cmd.append(serviceType);
        cmd.append("'));");

        return cmd.toString();
    }

    /**
     * Builds a query, which request the ID of a service by given service URL and service Type
     * 
     * @param serviceUrl
     *        The service URL
     * @param serviceType
     *        The service type
     * @return Returns an service query
     */
    private String serviceIDQuery(String serviceUrl, String serviceType) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append("='");
        query.append(serviceUrl);
        query.append("' AND ");
        query.append(PGDAOConstants.serviceType);
        query.append("='");
        query.append(serviceType);
        query.append("');");

        return query.toString();
    }
}

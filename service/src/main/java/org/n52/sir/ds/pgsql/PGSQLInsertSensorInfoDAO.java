/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n52.sir.ds.pgsql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.n52.oss.sir.api.InternalSensorID;
import org.n52.oss.sir.api.ObservedProperty;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IInsertSensorInfoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class PGSQLInsertSensorInfoDAO implements IInsertSensorInfoDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLInsertSensorInfoDAO.class);

    private PGConnectionPool cpool;

    private PGSQLObservedPropertyDAO obsPropDao;

    @Inject
    public PGSQLInsertSensorInfoDAO(PGConnectionPool cpool, PGSQLObservedPropertyDAO obsPropDao) {
        this.cpool = cpool;
        this.obsPropDao = obsPropDao;
    }

    @Override
    public String addNewReference(SirSensorIdentification sensIdent, SirServiceReference servDesc) throws OwsExceptionReport {
        String insertedSensorId = null;

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            String id = getDatabaseSensorId(sensIdent);

            // build add service query
            String addService = addServiceCommand(servDesc);
            log.debug(">>>Database Query: {}", addService);
            stmt.execute(addService);

            // build add reference query
            String addReference = addReferenceCommand(id, servDesc);
            log.debug(">>>Database Query: {}", addReference);
            try (ResultSet rs = stmt.executeQuery(addReference);) {
                if (rs.next()) {
                    String result = rs.getString(PGDAOConstants.databaseSensorId);

                    if (result.equals(id))
                        insertedSensorId = getInternalSensorId(sensIdent);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while adding a service reference: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "InsertSensorInfoDAO",
                                 "Error while adding a service reference: " + sqle.getMessage());
            throw se;
        }

        return insertedSensorId;
    }

    private String addReferenceCommand(String sensorId, SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ");
        query.append(PGDAOConstants.sensorService);
        query.append(" (");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(",");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(",");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(") SELECT (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append("='");
        query.append(servDesc.getService().getUrl());
        query.append("' AND ");
        query.append(PGDAOConstants.serviceType);
        query.append("='");
        query.append(servDesc.getService().getType());
        query.append("')), '");
        query.append(sensorId);
        query.append("','");
        query.append(servDesc.getServiceSpecificSensorId());
        query.append("' WHERE NOT EXISTS (SELECT ");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(",");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(",");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE ");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append("='");
        query.append(servDesc.getService().getUrl());
        query.append("' AND ");
        query.append(PGDAOConstants.serviceType);
        query.append("='");
        query.append(servDesc.getService().getType());
        query.append("')) AND ");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(" = '");
        query.append(sensorId);
        query.append("' AND ");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" = '");
        query.append(servDesc.getServiceSpecificSensorId());
        query.append("') RETURNING ");
        query.append(PGDAOConstants.databaseSensorId);
        query.append(";");

        return query.toString();
    }

    private String addServiceCommand(SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ");
        query.append(PGDAOConstants.service);
        query.append(" (");
        query.append(PGDAOConstants.serviceUrl);
        query.append(",");
        query.append(PGDAOConstants.serviceType);
        query.append(") SELECT '");
        query.append(servDesc.getService().getUrl());
        query.append("','");
        query.append(servDesc.getService().getType());
        query.append("' WHERE NOT EXISTS (SELECT ");
        query.append(PGDAOConstants.serviceUrl);
        query.append(",");
        query.append(PGDAOConstants.serviceType);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append("='");
        query.append(servDesc.getService().getUrl());
        query.append("' AND ");
        query.append(PGDAOConstants.serviceType);
        query.append(" = '");
        query.append(servDesc.getService().getType());
        query.append("')); ");

        return query.toString();
    }

    @Override
    public String deleteReference(SirSensorIdentification sensIdent, SirServiceReference servDesc) throws OwsExceptionReport {
        String sensorIdWithDeletedReference;

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            String id = getInternalSensorId(sensIdent);
            // build remove reference query
            String removeReference = removeReferenceString(id, servDesc);
            log.debug(">>>Database Query: {}", removeReference);
            try (ResultSet rs = stmt.executeQuery(removeReference);) {
                if (rs.next()) {
                    sensorIdWithDeletedReference = rs.getString(PGDAOConstants.sensorId);
                }
                else {
                    sensorIdWithDeletedReference = null;
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while removing a service reference: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "InsertSensorInfoDAO",
                                 "Error while removing a service reference: " + sqle.getMessage());
            throw se;
        }

        return sensorIdWithDeletedReference;
    }

    @Override
    public String deleteSensor(SirSensorIdentification sensIdent) throws OwsExceptionReport {
        log.debug("Deleting sensor {}", sensIdent);

        String sensorId;

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            sensorId = getInternalSensorId(sensIdent);
            String removeSensor = removeSensorString(sensorId);
            log.debug(">>>Database Query: {}", removeSensor);

            try (ResultSet rs = stmt.executeQuery(removeSensor);) {
                if (rs.next())
                    sensorId = rs.getString(PGDAOConstants.sensorId);
                else
                    sensorId = null;
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while removing a sensor: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "InsertSensorInfoDAO",
                                 "Error while removing a sensor: " + sqle.getMessage());
            throw se;
        }

        return sensorId;
    }

    private String getInternalSensorId(SirSensorIdentification sensIdent) throws OwsExceptionReport {
        String sensorID = null;

        if (sensIdent instanceof SirServiceReference) {
            try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
                String getSensorID = getInternalSensorIdString((SirServiceReference) sensIdent);
                log.debug(">>> Database Query: {}", getSensorID);
                try (ResultSet rs = stmt.executeQuery(getSensorID);) {
                    if (rs == null) {
                        return sensorID;
                    }
                    while (rs.next()) {
                        sensorID = rs.getString(PGDAOConstants.sensorIdSirSensServ);
                    }
                }
            }
            catch (SQLException sqle) {
                OwsExceptionReport se = new OwsExceptionReport();
                log.error("Error while requesting a sensor ID: " + sqle.getMessage());
                se.addCodedException(ExceptionCode.NoApplicableCode,
                                     "InsertSensorInfoDAO",
                                     "Error while requesting a sensor ID: " + sqle.getMessage());
                throw se;
            }
        }
        else {
            InternalSensorID temp = (InternalSensorID) sensIdent;
            return temp.getId();
        }

        return sensorID;
    }

    private String getDatabaseSensorId(SirSensorIdentification sensIdent) throws OwsExceptionReport {
        String sensorID = null;

        String getSensorID = null;
        if (sensIdent instanceof SirServiceReference) {
            getSensorID = getDatabaseSensorIdString((SirServiceReference) sensIdent);
        }
        else {
            InternalSensorID temp = (InternalSensorID) sensIdent;
            getSensorID = getDatabaseSensorIdString(temp);
        }

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {

            log.debug(">>> Database Query: {}", getSensorID);
            try (ResultSet rs = stmt.executeQuery(getSensorID);) {
                if (rs == null) {
                    return sensorID;
                }
                while (rs.next()) {
                    sensorID = rs.getString(PGDAOConstants.sensorIdSirSensServ);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while requesting a sensor ID: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "InsertSensorInfoDAO",
                                 "Error while requesting a sensor ID: " + sqle.getMessage());
            throw se;
        }

        return sensorID;
    }

    private String getInternalSensorIdString(SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensorId);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" = '");
        query.append(servDesc.getServiceSpecificSensorId());
        query.append("') AND (");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append(" = '");
        query.append(servDesc.getService().getUrl());
        query.append("') AND (");
        query.append(PGDAOConstants.serviceType);
        query.append(" = '");
        query.append(servDesc.getService().getType());
        query.append("')));");

        return query.toString();
    }

    private String getDatabaseSensorIdString(InternalSensorID sensorId) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.databaseSensorId);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorId);
        query.append(" = '");
        query.append(sensorId.getId());
        query.append("');");

        return query.toString();
    }

    private String getDatabaseSensorIdString(SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.databaseSensorId);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" = '");
        query.append(servDesc.getServiceSpecificSensorId());
        query.append("') AND (");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append(" = '");
        query.append(servDesc.getService().getUrl());
        query.append("') AND (");
        query.append(PGDAOConstants.serviceType);
        query.append(" = '");
        query.append(servDesc.getService().getType());
        query.append("')));");

        return query.toString();
    }

    @Override
    public String insertSensor(SirSensor sensor) throws OwsExceptionReport {
        String id = sensor.getInternalSensorID();
        if (id == null | id.isEmpty()) {
            log.error("internal ID must be set outside of dao.");
            throw new RuntimeException("internal ID must be set before inserting sensor into DAO.");
        }

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {

            // insert in sensor table
            String insertSensor = insertSensorCommand(sensor);
            log.debug(">>>Database Query: {}", insertSensor.toString());
            try (ResultSet rs = stmt.executeQuery(insertSensor);) {

                String dbId = null;
                if (rs.next()) {
                    dbId = rs.getString(PGDAOConstants.databaseSensorId);
                    log.debug(">>>Query successful, database id: {}", dbId);
                }

                // use database id for relation tables
                if (dbId != null) {
                    for (ObservedProperty obsProp : sensor.getObservedProperties()) {
                        String obsPropId = "";
                        String insertObsProp = this.obsPropDao.insertObservedPropertyCommand(obsProp);
                        log.debug(">>>Database Query: {}", insertObsProp);
                        try (ResultSet rs2 = stmt.executeQuery(insertObsProp);) {
                            while (rs.next()) {
                                obsPropId = rs.getString(PGDAOConstants.obsPropId);
                            }
                            if (obsPropId.isEmpty()) {
                                String iDQuery = this.obsPropDao.getIdQuery(obsProp);
                                log.debug(">>>Database Query: {}", iDQuery);
                                try (ResultSet rs3 = stmt.executeQuery(iDQuery);) {
                                    while (rs.next()) {
                                        obsPropId = rs.getString(PGDAOConstants.obsPropId);
                                    }
                                }
                            }
                            String insertSensorObsProp = this.obsPropDao.insertSensorObsPropCommand(dbId, obsPropId);
                            log.debug(">>>Database Query: {}", insertSensorObsProp);
                            stmt.execute(insertSensorObsProp);
                        }
                    }
                }
                else
                    log.error("Error inserting sensor, but no DB exception? dbId: {}", dbId);
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

        return id;
    }

    private String insertSensorCommand(SirSensor sensor) {
        StringBuffer cmd = new StringBuffer();
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
        cmd.append(") SELECT '");
        cmd.append(sensor.getInternalSensorID());
        cmd.append("', ");
        cmd.append("ST_GeomFromText('POLYGON((");
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
        cmd.append(sensor.getTimePeriod().getStartTime());
        cmd.append("', '");
        cmd.append(sensor.getTimePeriod().getEndTime());
        cmd.append("', '");
        cmd.append(SqlTools.escapeSQLString(sensor.getSensorMLDocument().xmlText()));
        cmd.append("', '{");
        if ( !sensor.getText().isEmpty()) {
            for (String text : sensor.getText()) {
                cmd.append(text);
                cmd.append(",");
            }
            cmd.deleteCharAt(cmd.length() - 1);
        }
        cmd.append("}', '");
        cmd.append(sensor.getLastUpdate());
        cmd.append("' WHERE NOT EXISTS (SELECT ");
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
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.sensor);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.sensorId);
        cmd.append(" = '");
        cmd.append(sensor.getInternalSensorID());
        cmd.append("') AND (");
        cmd.append(PGDAOConstants.bBox);
        cmd.append(" = ST_GeomFromText('POLYGON((");
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
        cmd.append(sensor.getTimePeriod().getStartTime());
        cmd.append("') AND (");
        cmd.append(PGDAOConstants.sensorTimeEnd);
        cmd.append(" = '");
        cmd.append(sensor.getTimePeriod().getEndTime());
        cmd.append("') AND (");
        cmd.append(PGDAOConstants.sensorml);
        cmd.append(" = '");
        cmd.append(SqlTools.escapeSQLString(sensor.getSensorMLDocument().xmlText()));
        cmd.append("') AND (");
        cmd.append(PGDAOConstants.sensorText);
        cmd.append(" = '{");
        if ( !sensor.getText().isEmpty()) {
            for (String text : sensor.getText()) {
                cmd.append(text);
                cmd.append(",");
            }
            cmd.deleteCharAt(cmd.length() - 1);
        }
        cmd.append("}')) RETURNING ");
        cmd.append(PGDAOConstants.databaseSensorId);

        return cmd.toString();
    }

    private String removeReferenceString(String sensorId, SirServiceReference servDesc) {
        StringBuilder query = new StringBuilder();

        query.append("DELETE FROM ");
        query.append(PGDAOConstants.sensorService);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorIdSirSensServ);
        query.append(" = '");
        query.append(sensorId);
        query.append("') AND (");
        query.append(PGDAOConstants.serviceSpecId);
        query.append(" = '");
        query.append(servDesc.getServiceSpecificSensorId());
        query.append("') AND (");
        query.append(PGDAOConstants.serviceIdOfSensServ);
        query.append(" = (SELECT ");
        query.append(PGDAOConstants.serviceId);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(" WHERE (");
        query.append(PGDAOConstants.serviceUrl);
        query.append(" = '");
        query.append(servDesc.getService().getUrl());
        query.append("') AND (");
        query.append(PGDAOConstants.serviceType);
        query.append(" = '");
        query.append(servDesc.getService().getType());
        query.append("'))) RETURNING ");
        query.append(PGDAOConstants.sensorId);
        query.append(";");

        return query.toString();
    }

    private String removeSensorString(String sensorId) {
        StringBuilder query = new StringBuilder();

        query.append("DELETE FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" WHERE (");
        query.append(PGDAOConstants.sensorId);
        query.append(" = '");
        query.append(sensorId);
        query.append("') RETURNING ");
        query.append(PGDAOConstants.sensorId);
        query.append(";");

        return query.toString();
    }

    @Override
    public String updateSensor(SirSensorIdentification sensIdent, SirSensor sensor) throws OwsExceptionReport {
        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            String updateSensor = updateSensorCommand(sensor);

            log.debug(">>>Database Query: {}",
                      updateSensor.toString().substring(0, Math.min(500, updateSensor.toString().length())));

            boolean sensorUpdate = stmt.execute(updateSensor);

            if (sensorUpdate) {
                log.warn("Wanted to do an update, but got a result set as response.");
            }
            else {
                log.debug("Updated sensor: {} !", sensor);

                if (sensor.getInternalSensorID() != null) {
                    for (ObservedProperty obsProp : sensor.getObservedProperties()) {
                        String iDQuery = this.obsPropDao.getIdQuery(obsProp);
                        log.debug(">>>Database Query: {}", iDQuery);
                        try (ResultSet rs = stmt.executeQuery(iDQuery);) {
                            while (rs.next()) {
                                String obsPropId = rs.getString(PGDAOConstants.obsPropId);
                                obsProp.setId(obsPropId);
                            }

                            String updateObsProp = this.obsPropDao.updateObservedPropertyCommand(obsProp);
                            log.debug(">>>Database Query: {}", updateObsProp);
                            boolean phenUpdate = stmt.execute(updateObsProp);

                            if (phenUpdate)
                                log.warn("Wanted to only update observed property, but got a result set.");
                            else
                                log.debug("Updated observedProperty: {} ", obsProp);
                        }
                    }
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while updating sensor in database: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode, null, "Error while updating sensor in database: "
                    + sqle.getMessage());
            throw se;
        }

        return sensor.getInternalSensorID();
    }

    private String updateSensorCommand(SirSensor sensor) {
        StringBuffer cmd = new StringBuffer();

        cmd.append("UPDATE ");
        cmd.append(PGDAOConstants.sensor);
        cmd.append(" SET ");
        cmd.append(PGDAOConstants.bBox);
        cmd.append(" = ");
        cmd.append("ST_GeomFromText('POLYGON((");
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
        cmd.append("))',-1)");
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeStart);
        cmd.append(" = '");
        cmd.append(sensor.getTimePeriod().getStartTime());
        cmd.append("'");
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorTimeEnd);
        cmd.append(" = '");
        cmd.append(sensor.getTimePeriod().getEndTime());
        cmd.append("'");
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorml);
        cmd.append(" = '");
        cmd.append(SqlTools.escapeSQLString(sensor.getSensorMLDocument().xmlText()));
        cmd.append("'");
        cmd.append(", ");
        cmd.append(PGDAOConstants.sensorText);
        cmd.append(" = '{");
        if ( !sensor.getText().isEmpty()) {
            for (String text : sensor.getText()) {
                cmd.append(text);
                cmd.append(",");
            }
            cmd.deleteCharAt(cmd.length() - 1);
        }
        cmd.append("}' ");
        cmd.append(", ");
        cmd.append(PGDAOConstants.lastUpdate);
        cmd.append(" = '");
        cmd.append(sensor.getLastUpdate());
        cmd.append("' WHERE ");
        cmd.append(PGDAOConstants.sensorId);
        cmd.append(" = ");
        cmd.append(sensor.getInternalSensorID());
        cmd.append(";");

        return cmd.toString();
    }

}

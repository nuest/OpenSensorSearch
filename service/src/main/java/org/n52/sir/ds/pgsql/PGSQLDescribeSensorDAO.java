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
import java.sql.Timestamp;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IDescribeSensorDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte, Daniel Nüst
 *
 */
public class PGSQLDescribeSensorDAO implements IDescribeSensorDAO {

    private static final Logger log = LoggerFactory.getLogger(PGSQLDescribeSensorDAO.class);

    private PGConnectionPool cpool;

    @Inject
    public PGSQLDescribeSensorDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public XmlObject getSensorDescription(String sensorId) throws OwsExceptionReport {
        XmlObject sensorML = null;

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            // sensorML query by sensorId
            String sensorMlQuery = sensorMlQuery(sensorId);
            log.debug(">>>Database Query: {}", sensorMlQuery);
            try (ResultSet rs = stmt.executeQuery(sensorMlQuery);) {
                Timestamp timestamp = null;

                while (rs.next()) {
                    sensorML = XmlObject.Factory.parse(rs.getString(PGDAOConstants.sensorml));
                    timestamp = rs.getTimestamp(PGDAOConstants.lastUpdate);
                }

                log.debug("Got SensorML from datbase, last update: {}", timestamp);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while quering sensorMLDocument: " + sqle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "DescribeSensorDAO",
                                 "Error while quering sensorMLDocument: " + sqle.getMessage());
            throw se;
        }
        catch (XmlException xmle) {
            OwsExceptionReport se = new OwsExceptionReport();
            log.error("Error while parsing sensorMLDocument: " + xmle.getMessage());
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 "DescribeSensorDAO",
                                 "Error while parsing sensorMLDocument: " + xmle.getMessage());
            throw se;
        }

        return sensorML;
    }

    private String sensorMlQuery(String sensorId) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.sensorml);
        query.append(", ");
        query.append(PGDAOConstants.lastUpdate);
        query.append(" FROM ");
        query.append(PGDAOConstants.sensor);
        query.append(" WHERE ");
        query.append(PGDAOConstants.sensorId);
        query.append(" = '");
        query.append(sensorId);
        query.append("'");

        return query.toString();
    }
}

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
import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.sir.api.SirService;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionLevel;
import org.n52.sir.ds.IGetAllServicesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class PGSQLGetAllServicesDAO implements IGetAllServicesDAO {

    private static final Logger log = LoggerFactory.getLogger(PGSQLGetAllServicesDAO.class);

    private PGConnectionPool cpool;

    public PGSQLGetAllServicesDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public Collection<SirService> getServices() throws OwsExceptionReport {
        ArrayList<SirService> result = new ArrayList<>();

        StringBuffer query = new StringBuffer();
        query.append("SELECT ");
        query.append(PGDAOConstants.serviceUrl);
        query.append(", ");
        query.append(PGDAOConstants.serviceType);
        query.append(" FROM ");
        query.append(PGDAOConstants.service);
        query.append(";");

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
                    SirService serv = new SirService(rs.getString(PGDAOConstants.serviceUrl),
                                                     rs.getString(PGDAOConstants.serviceType));
                    result.add(serv);
                }
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport(ExceptionLevel.DetailedExceptions);
            log.error("Error while query services for the getAllServices from database!", sqle);
            se.addCodedException(ExceptionCode.NoApplicableCode, null, sqle);
            throw se;
        }

        return result;
    }

}

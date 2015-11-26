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
import java.sql.SQLException;
import java.sql.Statement;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.ICatalogStatusHandlerDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class PGSQLCatalogStatusHandlerDAO implements ICatalogStatusHandlerDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLCatalogStatusHandlerDAO.class);

    private PGConnectionPool cpool;

    @Inject
    public PGSQLCatalogStatusHandlerDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public void setNewStatus(String connectionID, String status) throws OwsExceptionReport {
        String setNewStatusQuery = setNewStatusQuery(connectionID, status);

        try (Connection con = this.cpool.getConnection(); Statement stmt = con.createStatement();) {
            log.debug(">>>Database Query: {}", setNewStatusQuery);
            stmt.execute(setNewStatusQuery);

            con.close();
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode, null, "Error while set a new Status for ID "
                    + connectionID + " in database: " + sqle.getMessage());
            log.error("Error while set a new Status for ID " + connectionID
                    + " in database: " + sqle.getMessage());
        }
    }

    private String setNewStatusQuery(String connectionID, String status) {
        StringBuilder query = new StringBuilder();

        query.append("UPDATE ");
        query.append(PGDAOConstants.catalog);
        query.append(" SET ");
        query.append(PGDAOConstants.catalogStatus);
        query.append(" = '");
        query.append(status);
        query.append("' WHERE ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(" = ");
        query.append(connectionID);
        query.append("");

        return query.toString();
    }

}

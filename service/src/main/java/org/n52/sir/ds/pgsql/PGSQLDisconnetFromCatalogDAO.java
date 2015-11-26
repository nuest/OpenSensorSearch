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

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.ds.IDisconnectFromCatalogDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author Jan Schulte
 * 
 */
public class PGSQLDisconnetFromCatalogDAO implements IDisconnectFromCatalogDAO {

    private static Logger log = LoggerFactory.getLogger(PGSQLDisconnetFromCatalogDAO.class);

    private PGConnectionPool cpool;

    @Inject
    public PGSQLDisconnetFromCatalogDAO(PGConnectionPool cpool) {
        this.cpool = cpool;
    }

    @Override
    public String deleteConnection(String cswURL) throws OwsExceptionReport {
        String connectionID = null;
        String deleteConnectionQuery = deleteConnectionQuery(cswURL);

        try (Connection con = this.cpool.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(deleteConnectionQuery);) {
            log.debug(">>>Database Query: {}", deleteConnectionQuery);

            while (rs.next()) {
                connectionID = rs.getString(PGDAOConstants.catalogIdSir);
            }
        }
        catch (SQLException sqle) {
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error while deleting a connection from catalog from database: " + sqle.getMessage());
            log.error("Error while deleting a connection from catalog from database: " + sqle.getMessage());
        }

        return connectionID;
    }

    private String deleteConnectionQuery(String cswURL) {
        StringBuilder query = new StringBuilder();

        query.append("DELETE FROM ");
        query.append(PGDAOConstants.catalog);
        query.append(" WHERE ");
        query.append(PGDAOConstants.catalogUrl);
        query.append(" = '");
        query.append(cswURL);
        query.append("' RETURNING ");
        query.append(PGDAOConstants.catalogIdSir);
        query.append(";");

        return query.toString();
    }

}

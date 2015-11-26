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

import org.n52.oss.sir.api.ObservedProperty;

public class PGSQLObservedPropertyDAO {

    protected String insertObservedPropertyCommand(ObservedProperty obsProp) {
        StringBuffer cmd = new StringBuffer();

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.phenomenon);
        cmd.append(" (");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(") SELECT '");
        cmd.append(obsProp.getUrn());
        cmd.append("', '");
        cmd.append(obsProp.getUom());
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.phenomenon);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(" = '");
        cmd.append(obsProp.getUrn());
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(" = '");
        cmd.append(obsProp.getUom());
        cmd.append("')) RETURNING ");
        cmd.append(PGDAOConstants.obsPropId);

        return cmd.toString();
    }

    protected String getIdQuery(ObservedProperty obsProp) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT ");
        query.append(PGDAOConstants.obsPropId);
        query.append(" FROM ");
        query.append(PGDAOConstants.phenomenon);
        query.append(" WHERE (");
        query.append(PGDAOConstants.phenomenonUrn);
        query.append("='");
        query.append(obsProp.getUrn());
        query.append("' AND ");
        query.append(PGDAOConstants.phenomenonUom);
        query.append("='");
        query.append(obsProp.getUom());
        query.append("');");

        return query.toString();
    }

    protected String insertSensorObsPropCommand(String sensorDbId, String obsPropDbId) {
        StringBuilder cmd = new StringBuilder();

        cmd.append("INSERT INTO ");
        cmd.append(PGDAOConstants.sensorPhen);
        cmd.append(" (");
        cmd.append(PGDAOConstants.sensorIdSirOfSensPhen);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomeonIdOfSensPhen);
        cmd.append(") SELECT '");
        cmd.append(sensorDbId);
        cmd.append("', '");
        cmd.append(obsPropDbId);
        cmd.append("' WHERE NOT EXISTS (SELECT ");
        cmd.append(PGDAOConstants.sensorIdSirOfSensPhen);
        cmd.append(", ");
        cmd.append(PGDAOConstants.phenomeonIdOfSensPhen);
        cmd.append(" FROM ");
        cmd.append(PGDAOConstants.sensorPhen);
        cmd.append(" WHERE (");
        cmd.append(PGDAOConstants.sensorIdSirOfSensPhen);
        cmd.append("='");
        cmd.append(sensorDbId);
        cmd.append("' AND ");
        cmd.append(PGDAOConstants.phenomeonIdOfSensPhen);
        cmd.append("='");
        cmd.append(obsPropDbId);
        cmd.append("'));");

        return cmd.toString();
    }

    protected String updateObservedPropertyCommand(ObservedProperty obsProp) {
        StringBuilder cmd = new StringBuilder();

        cmd.append("UPDATE ");
        cmd.append(PGDAOConstants.phenomenon);
        cmd.append(" SET ");
        cmd.append(PGDAOConstants.phenomenonUrn);
        cmd.append(" = '");
        cmd.append(obsProp.getUrn());
        cmd.append("', ");
        cmd.append(PGDAOConstants.phenomenonUom);
        cmd.append(" = '");
        cmd.append(obsProp.getUom());
        cmd.append("' WHERE ");
        cmd.append(PGDAOConstants.obsPropId);
        cmd.append(" = ");
        cmd.append(obsProp.getId());
        cmd.append(";");

        return cmd.toString();
    }

}

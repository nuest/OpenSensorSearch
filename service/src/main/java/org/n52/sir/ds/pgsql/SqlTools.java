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

import org.n52.oss.sir.api.TimePeriod;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime.IndeterminateTimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlTools {

    private static final Logger log = LoggerFactory.getLogger(SqlTools.class);

    public static String escapeSQLString(String text) {
        String s = text;
        if (text.contains("'")) {
            log.debug("Text contains character that has to be escaped before database insertion, namely ' .");
            s = s.replace("'", "''"); // http://www.postgresql.org/docs/9.3/static/sql-syntax-lexical.html
        }
        return s;
    }

    public static String getStartDate(final TimePeriod period) {
        IndeterminateTime startTime = period.getStartTime();
        if (startTime.isIndeterminate() && startTime.itt.equals(IndeterminateTimeType.UNKNOWN))
            return "-infinity";

        return getDateStringForDB(startTime);
    }

    private static String getDateStringForDB(IndeterminateTime t) {
        if (t.isDeterminate())
            return t.d.toString();

        log.error("Cannot create DB time string from time {}", t);
        return null;
    }

    public static String getEndDate(TimePeriod period) {
        IndeterminateTime endTime = period.getEndTime();
        if (endTime.isIndeterminate() && endTime.itt.equals(IndeterminateTimeType.UNKNOWN))
            return "infinity";

        return getDateStringForDB(endTime);
    }

}

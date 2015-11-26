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
package org.n52.sir.ds.solr;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SolrUtils {
    public static final String ISO8061UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static String getISO8601UTCString(Date d) {
        // Convert date to UTC date
        try {
            SimpleDateFormat formatter = new SimpleDateFormat();
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat simple = new SimpleDateFormat("M/d/yy h:mm a");
            Date UTCDate = simple.parse(formatter.format(d.getTime()));

            SimpleDateFormat ISO8061Formatter = new SimpleDateFormat(ISO8061UTC);
            return ISO8061Formatter.format(UTCDate);
        }
        catch (Exception e) {
            return null;
        }

    }

}

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
package org.n52.oss.opensearch;

import java.util.ArrayList;
import java.util.Collection;


/**
 * TODO see what is constant, and what should be in {@link OpenSearchConfigurator}.
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 * 
 */
public class OpenSearchConstants {

    public static final String BOX_PARAM = "box";

    public static final String CDATA_END_TAG = "]";

    public static final String CDATA_START_TAG = "![CDATA[";

    public static final double DEFAULT_RADIUS = 1000.0d;

    public static final double EARTH_RADIUS_METERS = 6.3675 * 1000000;

    public static final String GEOMETRY_PARAM = "geometry";

    public static final String LAT_PARAM = "lat";

    public static final String LON_PARAM = "lon";

    public static final int MAX_GET_URL_CHARACTER_COUNT = 2000; // http://stackoverflow.com/questions/417142/what-is-the-maximum-length-of-a-url

    public static final String MIME_TYPE_PLAIN = "text/plain";

    public static final String NAME_PARAM = "name";

    public static final String QUERY_PARAM = "q";

    public static final String TIME_START_PARAM = "dtstart";

    public static final String TIME_END_PARAM = "dtend";

    public static final String RADIUS_PARAM = "radius";

    public static Collection<String> TIME_SERIES_SERVICE_TYPES = new ArrayList<>();

    public static final String X_DEFAULT_MIME_TYPE = "text/html";

    public static final String FORMAT_PARAM = "format";

    public static final String APPLICATION_RSS_XML = "application/rss+xml";

    public static final String URL_DECODE_ENCODING = "UTF-8";

    public static final String APPLICATION_VND_KML = "application/vnd.google-earth.kml+xml";

    static {
        TIME_SERIES_SERVICE_TYPES.add("SOS");
        TIME_SERIES_SERVICE_TYPES.add("OGC:SOS");
    }

}

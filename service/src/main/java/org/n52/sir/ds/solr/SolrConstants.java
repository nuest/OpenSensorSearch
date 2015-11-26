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
package org.n52.sir.ds.solr;

public class SolrConstants {
    // All the fields names of the Solr Index

    public static final String ID = "id";
    public static final String KEYWORD = "keyword";
    public static final String UNIQUE_ID = "uniqueID";
    public static final String LONG_NAME = "longname";
    public static final String SHORT_NAME = "shortname";
    public static final String LOCATION = "location";
    public static final String BBOX_CENTER = "bboxcenter";
    public static final String START_DATE = "dtstart";
    public static final String END_DATE = "dtend";
    public static final String DESCRIPTION = "description";
    public static final String CLASSIFIER = "classifier";
    public static final String IDENTIFICATION = "identification";
    public static final String CONTACTS = "contact";
    public static final String INTERFACE = "interface";
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    public static final String EDISMAX = "keyword^1 uniqueID^1 location^1 bboxcenter^1 description^1 classifier^1 identification^1 contact^1 interface^1 input^1 output^1";
    public static final String AND_OP ="AND";
    public static final String OR_OP = "OR";
    
}

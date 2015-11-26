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
package org.n52.oss.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 *
 */
public class Tools {

    private static final Logger log = LoggerFactory.getLogger(Tools.class);

    public static boolean atLeastOneIsNotEmpty(String[] strings) {
        for (String string : strings) {
            if (!string.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static String getStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder("STACKTRACE: ");
        sb.append(t.toString());
        sb.append("\n");

        for (StackTraceElement element : t.getStackTrace()) {
            sb.append(element);
            sb.append("\n");
        }
        return sb.toString();
    }

    public static boolean noneEmpty(String[] strings) {
        for (String string : strings) {
            if (string.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /*
     * remove trailing and leading white spaces, replace newline characters with space character.
     */
    public static String simplifyString(String stringToSimplify) {
        String s = stringToSimplify.trim();
        s = s.replaceAll("\n", " ");
        return s;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        is.close();
        return sb.toString();
    }
}

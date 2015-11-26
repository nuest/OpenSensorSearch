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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.n52.sir.json.MapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);

    public static String readResourceFile(String s) {
        URL resource = Util.class.getResource(s);
        Path path;
        try {
            path = Paths.get(resource.toURI());
        }
        catch (URISyntaxException e) {
            log.error("Could not read from resource path " + s, e);
            return "";
        }
        return readResourceFile(path);
    }

    public static String readResourceFile(Path p) {
        log.debug("Reading {}", p);
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new FileReader(p.toFile());) {

            int data = reader.read();
            while (data != -1) {
                char dataChar = (char) data;
                sb.append(dataChar);
                data = reader.read();
            }
            reader.close();
        }
        catch (IOException e) {
            log.error("Could not read from file " + p, e);
        }

        return sb.toString();
    }

    public static String entityToString(Response response) throws JsonGenerationException,
            JsonMappingException,
            IOException {
        StringWriter writer = new StringWriter();
        MapperFactory.getMapper().writeValue(writer, response.getEntity());
        return writer.toString();
    }

    public static String getResponsePayload(HttpResponse response) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String s;
        while ( (s = reader.readLine()) != null)
            builder.append(s);

        return builder.toString().trim();
    }

}

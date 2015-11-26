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
package org.n52.oss.sir;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SirClient extends Client {

    private static Logger log = LoggerFactory.getLogger(SirClient.class);
    private String sirVersion;

    @Inject
    public SirClient(@Named("oss.sir.sirClient.url")
    String sirUrl, @Named("oss.sir.version")
    String sirVersion) {
        super(sirUrl);
        this.sirVersion = sirVersion;
    }

    /**
     * 
     * creates a GET request to retrieve the sensor description of the given sensor,
     * 
     * @param sensorId
     * @param encodeURLs
     *        for usage in XML documents
     * @return
     * @throws UnsupportedEncodingException
     */
    public String createDescribeSensorURL(String sensorId, boolean encodeURLs) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append(this.uri);
        sb.append("?");
        sb.append(SirConstants.SERVICEPARAM);
        sb.append("=");
        sb.append(SirConstants.SERVICE_NAME);
        sb.append("&");
        sb.append(SirConstants.GETVERSIONPARAM);
        sb.append("=");
        sb.append(this.sirVersion);
        sb.append("&");
        sb.append(SirConstants.GETREQUESTPARAM);
        sb.append("=");
        sb.append(SirConstants.Operations.DescribeSensor.name());
        sb.append("&");
        sb.append(SirConstants.GetDescSensorParams.SENSORIDINSIR.name());
        sb.append("=");
        sb.append(sensorId);

        log.debug("Created description URL for sensor {}: {}", sensorId, sb.toString());

        // URL must be encoded for usage in XML documents
        if (encodeURLs)
            return URLEncoder.encode(sb.toString(), "UTF-8");

        return sb.toString();
    }

}

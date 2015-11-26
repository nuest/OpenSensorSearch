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
package org.n52.sir.decode;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.request.AbstractSirRequest;

/**
 * interface offers parsing method to parse the String representing a Get request and create a Sir request
 * 
 * @author Jan Schulte
 * 
 */
public interface IHttpGetRequestDecoder {

    /**
     * parses the String representing the Get-request and returns an internal SIR representation of the
     * request
     * 
     * @param capString
     *        String with the getCapabilities parameters
     * @return Returns SirGetCapabilitiesRequest representing the request
     * @throws OwsExceptionReport
     *         If parsing the string failed
     */
    public AbstractSirRequest receiveRequest(String capString) throws OwsExceptionReport;

}
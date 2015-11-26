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
package org.n52.sir.ds;

import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the harvestService
 * operation
 * 
 * @author Jan Schulte
 * 
 */
public interface IHarvestServiceDAO {

    /**
     * Inserts a service in the database
     * 
     * @param serviceUrl
     *        The Service Url
     * @param serviceType
     *        The Service type
     * @return Returns the ServiceID in the Database
     * @throws OwsExceptionReport
     */
    public String addService(String serviceUrl, String serviceType) throws OwsExceptionReport;

    /**
     * Inserts a sensor in the database
     * 
     * @param sensor
     *        the inserted sensor
     * @return Returns the SensorID in the Database
     * @throws OwsExceptionReport
     */
    public SirSensor insertSensor(SirSensor sensor) throws OwsExceptionReport;

}

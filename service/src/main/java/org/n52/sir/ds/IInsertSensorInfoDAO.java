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
package org.n52.sir.ds;

import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the
 * insertSensorInfo operation
 * 
 * @author Jan Schulte
 * 
 */
public interface IInsertSensorInfoDAO {

    /**
     * Adds a service reference to a given sensor identification in SIR
     * 
     * @param sensIdent
     *        the sensor identification
     * @param servRef
     *        the added service description
     * @throws OwsExceptionReport
     */
    public String addNewReference(SirSensorIdentification sensIdent, SirServiceReference servRef) throws OwsExceptionReport;

    /**
     * Removes a service reference by a given sensor ID in SIR an the service description
     * 
     * @param sensIdent
     *        the sensor Identification
     * @param servRef
     *        the removed service description
     * @throws OwsExceptionReport
     */
    public String deleteReference(SirSensorIdentification sensIdent, SirServiceReference servRef) throws OwsExceptionReport;

    /**
     * Deletes a sensor by SensorIdentification
     * 
     * @param sensIdent
     *        the sensorIdentification
     * @return Returns the former SensorID in SIR
     * @throws OwsExceptionReport
     */
    public String deleteSensor(SirSensorIdentification sensIdent) throws OwsExceptionReport;

    /**
     * Inserts a sensor with service description and sensor information
     * 
     * @param sensor
     *        the sensor
     * @return Returns the sensorID in the Database
     * @throws OwsExceptionReport
     */
    public String insertSensor(SirSensor sensor) throws OwsExceptionReport;

    /**
     * Updates the sensor document by SensorIdentification with given sensorML document
     * 
     * @param sensIdent
     *        the sensorIdentification
     * @param sensor
     *        the sensor
     * @return Returns the SensorID in SIR
     * @throws OwsExceptionReport
     */
    public String updateSensor(SirSensorIdentification sensIdent, SirSensor sensor) throws OwsExceptionReport;

}

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

import java.util.Collection;

import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * Interface for the specific DAOFactories, offers methods to create the matching DAOs for the searchSensor
 * operation
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public interface ISearchSensorDAO {

    public String FULL = "full";

    public String AUTOCOMPLETE = "autocomplete";

    /**
     * Get all available sensors.
     * 
     * @return Returns the sensors
     * @throws OwsExceptionReport
     */
    public Collection<SirSearchResultElement> getAllSensors(boolean simpleReponse) throws OwsExceptionReport;

    /**
     * Search a sensor by given internal sensor identification and returns the sensor
     * 
     * @param sensorId
     *        the identifier provided by this service
     * @param b
     * @return Returns the Sensor
     * @throws OwsExceptionReport
     */
    public SirSearchResultElement getSensorBySensorID(String sensorId, boolean simpleReponse) throws OwsExceptionReport;

    /**
     * Search a sensor by given service description and returns the sensor
     * 
     * @param servDesc
     *        the service description
     * @return Returns the sensor
     * @throws OwsExceptionReport
     */
    public SirSearchResultElement getSensorByServiceDescription(SirServiceReference servDesc, boolean simpleReponse) throws OwsExceptionReport;

    /**
     * Search sensors by a given searchCriteria and returns a collection of SearchResultElements
     * 
     * @param searchCriteria
     *        the searchCriteria
     * @return Returns a Collection of SearchResultElements
     * @throws OwsExceptionReport
     */
    public Collection<SirSearchResultElement> searchSensor(SirSearchCriteria searchCriteria, boolean simpleReponse) throws OwsExceptionReport;

    /**
     * 
     * @return a list of all the sensor ids
     * @throws OwsExceptionReport
     */
    public Collection<String> getAllSensorIds() throws OwsExceptionReport;

}

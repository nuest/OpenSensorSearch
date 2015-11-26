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

import java.util.Collection;

import org.n52.oss.sir.api.InternalSensorID;
import org.n52.oss.sir.api.SirPropertyFilter;
import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.api.SirStatusDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * @author Jan Schulte
 *
 */
public interface IGetSensorStatusDAO {

    /**
     * Search the status of sensors by a given search criteria and a filter, than returns status information
     *
     * @param searchCriteria
     *        the search criteria
     * @param propertyFilter
     *        the filter for status filtering
     * @return Returns a collection of status descriptions
     * @throws OwsExceptionReport
     */
    public Collection<SirStatusDescription> getSensorStatusBySearchCriteria(SirSearchCriteria searchCriteria,
                                                                            Collection<SirPropertyFilter> propertyFilter) throws OwsExceptionReport;

    /**
     * Search the status of a sensor by a given sensorID and a filter, than returns a status information
     *
     * @param sensorId
     *        the sensorId of the given sensor
     * @param propertyFilter
     *        the filter for status filtering
     * @return Returns a collection of status descriptions
     * @throws OwsExceptionReport
     */
    public Collection<SirStatusDescription> getSensorStatusBySensorID(InternalSensorID sensorId,
                                                                      Collection<SirPropertyFilter> propertyFilter) throws OwsExceptionReport;

    /**
     * Search the status of a sensor by a given service description and a filter, than returns a status
     * information
     *
     * @param servDesc
     *        the service description of the given sensor
     * @param propertyFilter
     *        the filter for status filtering
     * @return Returns a collection of status descriptions
     * @throws OwsExceptionReport
     */
    public Collection<SirStatusDescription> getSensorStatusByServiceDescription(SirServiceReference servDesc,
                                                                                Collection<SirPropertyFilter> propertyFilter) throws OwsExceptionReport;

}

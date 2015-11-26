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
package org.n52.sir.request;

import java.util.Collection;

import org.n52.oss.sir.api.SirPropertyFilter;
import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirSensorIdentification;

/**
 * Internal request to get sensor status
 *
 * @author Jan Schulte
 *
 */
public class SirGetSensorStatusRequest extends AbstractSirRequest {

    private Collection<SirPropertyFilter> propertyFilter;

    private SirSearchCriteria searchCriteria;

    private Collection<SirSensorIdentification> sensIdent;

    /**
     * @return the propertyFilter
     */
    public Collection<SirPropertyFilter> getPropertyFilter() {
        return this.propertyFilter;
    }

    /**
     * @return the searchCriteria
     */
    public SirSearchCriteria getSearchCriteria() {
        return this.searchCriteria;
    }

    /**
     * @return the sensIdent
     */
    public Collection<SirSensorIdentification> getSensIdent() {
        return this.sensIdent;
    }

    /**
     * @param propertyFilter
     *        the propertyFilter to set
     */
    public void setPropertyFilter(Collection<SirPropertyFilter> propertyFilter) {
        this.propertyFilter = propertyFilter;
    }

    /**
     * @param searchCriteria
     *        the searchCriteria to set
     */
    public void setSearchCriteria(SirSearchCriteria searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    /**
     * @param sensIdent
     *        the sensIdent to set
     */
    public void setSensIdent(Collection<SirSensorIdentification> sensIdent) {
        this.sensIdent = sensIdent;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("GetSensorStatusRequest: ");
        sb.append("SensorIdentifications: " + this.sensIdent);
        sb.append(" SearchCriteria: " + this.searchCriteria);
        sb.append(" PropertyFilter: " + this.propertyFilter);
        return sb.toString();
    }
}

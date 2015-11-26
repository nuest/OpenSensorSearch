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
package org.n52.oss.sir.api;

import java.util.Collection;
import java.util.Date;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class SirSearchResultElement {

    private Date lastUpdate;

    private SirSensorDescription sensorDescription;

    private String sensorId;

    private Collection<SirServiceReference> serviceReferences;

    public SirSearchResultElement() {
        // empty constructor for deserialization
    }

    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    public SirSensorDescription getSensorDescription() {
        return this.sensorDescription;
    }

    public String getSensorId() {
        return this.sensorId;
    }

    public Collection<SirServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setSensorDescription(SirSensorDescription sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    public void setSensorId(String id) {
        this.sensorId = id;
    }

    public void setServiceReferences(Collection<SirServiceReference> serviceReferences) {
        this.serviceReferences = serviceReferences;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SirSearchResultElement [");
        if (this.sensorId != null) {
            builder.append("sensorId=");
            builder.append(this.sensorId);
            builder.append(", ");
        }
        if (this.lastUpdate != null) {
            builder.append("lastUpdate=");
            builder.append(this.lastUpdate);
            builder.append(", ");
        }
        if (this.sensorDescription != null) {
            builder.append("sensorDescription=");
            builder.append(this.sensorDescription);
            builder.append(", ");
        }
        if (this.serviceReferences != null) {
            builder.append("serviceReferences=");
            builder.append(this.serviceReferences);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( (this.sensorId == null) ? 0 : this.sensorId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SirSearchResultElement other = (SirSearchResultElement) obj;
        if (this.sensorId == null) {
            if (other.sensorId != null)
                return false;
        }
        else if ( !this.sensorId.equals(other.sensorId))
            return false;
        return true;
    }

}

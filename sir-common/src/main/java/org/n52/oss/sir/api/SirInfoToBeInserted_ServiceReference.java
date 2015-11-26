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

/**
 * @author Daniel Nüst
 * 
 */
public class SirInfoToBeInserted_ServiceReference extends SirInfoToBeInserted {

    private InternalSensorID id;

    private Collection<SirServiceReference> serviceReferences;

    public InternalSensorID getID() {
        return this.id;
    }

    public Collection<SirServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    public void setID(InternalSensorID id) {
        this.id = id;
    }

    public void setServiceReferences(Collection<SirServiceReference> serviceReferences) {
        this.serviceReferences = serviceReferences;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("InfoToBeInserted_ServiceReference:\nSensorID:");
        sb.append(this.id);
        sb.append("\nServiceReferences: ");
        sb.append(this.serviceReferences);
        return sb.toString();
    }

}

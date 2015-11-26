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
package org.n52.oss.sir.api;

/**
 * Represents an internal service description, which includes the service and a service specific sensor ID
 *
 * @author Jan Schulte
 *
 */
public class SirServiceReference extends SirSensorIdentification {

    private SirService service;

    private String serviceSpecificSensorId;

    public SirServiceReference() {
        //
    }

    public SirServiceReference(SirService service, String serviceSpecificSensorId) {
        this.service = service;
        this.serviceSpecificSensorId = serviceSpecificSensorId;
    }

    /**
     * @return the service
     */
    public SirService getService() {
        return this.service;
    }

    /**
     * @return the serviceSpecificSensorId
     */
    public String getServiceSpecificSensorId() {
        return this.serviceSpecificSensorId;
    }

    /**
     * @param service
     *        the service to set
     */
    public void setService(SirService service) {
        this.service = service;
    }

    /**
     * @param serviceSpecificSensorId
     *        the serviceSpecificSensorId to set
     */
    public void setServiceSpecificSensorId(String serviceSpecificSensorId) {
        this.serviceSpecificSensorId = serviceSpecificSensorId;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ServiceReference: ");
        sb.append(this.service);
        sb.append(" Service specific ID: " + this.serviceSpecificSensorId);
        return sb.toString();
    }

}

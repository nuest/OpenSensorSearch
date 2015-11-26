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
package org.n52.sir.json;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 *
 */
@XmlRootElement
public class ServiceReference { // extends SirSensorIdentification {

    private Service service;

    private String serviceSpecificSensorId;

    public ServiceReference() {
        // empty constructor for deserialization
    }

    public ServiceReference(Service service, String serviceSpecificSensorId) {
        this.service = service;
        this.serviceSpecificSensorId = serviceSpecificSensorId;
    }

    @XmlElement
    public String getServiceType() {
        return this.service.getType();
    }

    @XmlElement
    public String getServiceUrl() {
        return this.service.getUrl();
    }

    @XmlElement
    public String getServiceSpecificSensorId() {
        return this.serviceSpecificSensorId;
    }

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

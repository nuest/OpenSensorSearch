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
 * @author Jan Schulte, Daniel Nüst
 *
 */
public class ObservedProperty {

    String id;

    String uom;

    String urn;

    public ObservedProperty() {
        //
    }

    public ObservedProperty(String urn, String uom, String phenomenonId) {
        this.urn = urn;
        this.uom = uom;
        this.id = phenomenonId;
    }

    public String getId() {
        return this.id;
    }

    public String getUom() {
        return this.uom;
    }

    public String getUrn() {
        return this.urn;
    }

    public void setId(String phenomenonId) {
        this.id = phenomenonId;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SirPhenomenon [");
        if (id != null) {
            builder.append("phenomenonId=");
            builder.append(id);
            builder.append(", ");
        }
        if (uom != null) {
            builder.append("uom=");
            builder.append(uom);
            builder.append(", ");
        }
        if (urn != null) {
            builder.append("urn=");
            builder.append(urn);
        }
        builder.append("]");
        return builder.toString();
    }

}

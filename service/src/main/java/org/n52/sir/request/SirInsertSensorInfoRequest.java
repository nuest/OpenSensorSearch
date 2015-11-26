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

import org.n52.oss.sir.api.SirInfoToBeInserted;

/**
 * @author Jan Schulte
 *
 */
public class SirInsertSensorInfoRequest extends AbstractSirRequest {

    private Collection<SirInfoToBeInserted> infoToBeInserted;

    /**
     * @return the infoToBeInserted
     */
    public Collection<SirInfoToBeInserted> getInfoToBeInserted() {
        return this.infoToBeInserted;
    }

    /**
     * @param infoToBeInserted
     *        the infoToBeInserted to set
     */
    public void setInfoToBeInserted(Collection<SirInfoToBeInserted> infoToBeInserted) {
        this.infoToBeInserted = infoToBeInserted;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("InsertSensorStatusRequest: ");
        sb.append(" Inserted Infos: " + this.infoToBeInserted);
        return sb.toString();
    }

}

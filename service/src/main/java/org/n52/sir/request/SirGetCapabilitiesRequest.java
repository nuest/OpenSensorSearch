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

/**
 * class represents a GetCapabilities request and encapsulates the parameters
 *
 * @author Jan Schulte
 *
 */
public class SirGetCapabilitiesRequest extends AbstractSirRequest {

    private String[] acceptFormats;

    private String[] acceptVersions;

    private String[] sections;

    private String updateSequence;

    /**
     * @return the acceptFormats
     */
    public String[] getAcceptFormats() {
        return this.acceptFormats;
    }

    /**
     * @return the acceptVersions
     */
    public String[] getAcceptVersions() {
        return this.acceptVersions;
    }

    /**
     * @return the sections
     */
    public String[] getSections() {
        return this.sections;
    }

    /**
     * @return the updateSequence
     */
    public String getUpdateSequence() {
        return this.updateSequence;
    }

    /**
     * @param acceptFormats
     *        the acceptFormats to set
     */
    public void setAcceptFormats(String[] acceptFormats) {
        this.acceptFormats = acceptFormats;
    }

    /**
     * @param acceptVersions
     *        the acceptVersions to set
     */
    public void setAcceptVersions(String[] acceptVersions) {
        this.acceptVersions = acceptVersions;
    }

    /**
     * @param sections
     *        the sections to set
     */
    public void setSections(String[] sections) {
        this.sections = sections;
    }

    /**
     * @param updateSequence
     *        the updateSequence to set
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

}

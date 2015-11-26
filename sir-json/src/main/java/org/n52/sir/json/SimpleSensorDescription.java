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
package org.n52.sir.json;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Daniel Nüst
 * 
 */
@XmlRootElement
public class SimpleSensorDescription {

    private BoundingBox boundingBox;

    private String text;

    private String url;

    public SimpleSensorDescription() {
        // empty constructor to allow deserialization
    }

    public SimpleSensorDescription(String url, String text, BoundingBox boundingBox) {
        this.url = url;
        this.text = text;
        this.boundingBox = boundingBox;
    }

    /**
     * @return the boundingBox
     */
    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    /**
     * @return the descriptionText
     */
    public String getText() {
        return this.text;
    }

    /**
     * @return the sensorDescriptionURL
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @param boundingBox
     *        the boundingBox to set
     */
    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * @param descriptionText
     *        the descriptionText to set
     */
    public void setText(String descriptionText) {
        this.text = descriptionText;
    }

    /**
     * @param sensorDescriptionURL
     *        the sensorDescriptionURL to set
     */
    public void setUrl(String sensorDescriptionURL) {
        this.url = sensorDescriptionURL;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SimpleSensorDescription [url: ");
        sb.append(this.url);
        sb.append(", descriptionText: ");
        sb.append(this.text);
        return sb.toString();
    }

}

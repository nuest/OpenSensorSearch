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

import java.util.Collection;
import java.util.Date;

public class SirDetailedSensorDescription extends SirSensorDescription {

    private String id;
    private Collection<String> keywords;
    private Date begineDate;
    private String location;

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String description;
    private double bbox_x;

    public double getbbox_x() {
        return this.bbox_x;
    }

    public void setbbox_x(double bbox_x) {
        this.bbox_x = bbox_x;
    }

    public double getbbox_y() {
        return this.bbox_y;
    }

    public void setbbox_y(double bbox_y) {
        this.bbox_y = bbox_y;
    }

    private double bbox_y;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getBegineDate() {
        return this.begineDate;
    }

    public void setBegineDate(Date begineDate) {
        this.begineDate = begineDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    private Date endDate;
    private Collection<String> classifiers;
    private Collection<String> identifiers;
    private Collection<String> contacts;
    private Collection<String> inputs;
    private Collection<String> outputs;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection<String> getKeywords() {
        return this.keywords;
    }

    public void setKeywords(Collection<String> keywords) {
        this.keywords = keywords;
    }

    public SirDetailedSensorDescription() {
        //
    }

    public void setClassifiers(Collection<String> classifiers) {
        this.classifiers = classifiers;
    }

    public Collection<String> getClassifiers() {
        return this.classifiers;
    }

    public Collection<String> getIdentifiers() {
        return this.identifiers;
    }

    public void setIdentifiers(Collection<String> identifiers) {
        this.identifiers = identifiers;
    }

    public void setContacts(Collection<String> contacts) {
        this.contacts = contacts;
    }

    public Collection<String> getContacts() {
        return this.contacts;

    }

    public void setInputs(Collection<String> inputs) {
        this.inputs = inputs;
    }

    public Collection<String> getInputs() {
        return this.inputs;
    }

    public void setOutputs(Collection<String> outputs) {
        this.outputs = outputs;
    }

    public Collection<String> getOutputs() {
        return this.outputs;
    }

}

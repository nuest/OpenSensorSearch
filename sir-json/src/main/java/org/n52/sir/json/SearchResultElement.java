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

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class SearchResultElement {

    private Date lastUpdate;

    private SimpleSensorDescription sensorDescription;

    private String sensorId;

    private Collection<ServiceReference> serviceReferences;

    private Collection<String> classifiers;

    private Collection<String> keywords;

    public Collection<String> getKeywords() {
        return this.keywords;
    }

    public void setKeywords(Collection<String> keywords) {
        this.keywords = keywords;
    }

    public Collection<String> getClassifiers() {
        return this.classifiers;
    }

    public void setClassifiers(Collection<String> classifiers) {
        this.classifiers = classifiers;
    }

    public Collection<String> getIdentifiers() {
        return this.identifiers;
    }

    public void setIdentifiers(Collection<String> identifiers) {
        this.identifiers = identifiers;
    }

    public Collection<String> getContacts() {
        return this.contacts;
    }

    public void setContacts(Collection<String> contacts) {
        this.contacts = contacts;
    }

    public Collection<String> getInputs() {
        return this.inputs;
    }

    public void setInputs(Collection<String> inputs) {
        this.inputs = inputs;
    }

    public Collection<String> getOutputs() {
        return this.outputs;
    }

    public void setOutputs(Collection<String> outputs) {
        this.outputs = outputs;
    }

    private Collection<String> identifiers;
    private Collection<String> contacts;
    private Collection<String> inputs;
    private Collection<String> outputs;

    public Date getBeginDate() {
        return this.beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    private Date beginDate;

    private Date endDate;

    public SearchResultElement() {
        // empty constructor for deserialization
    }

    public Date getLastUpdate() {
        return this.lastUpdate;
    }

    public SimpleSensorDescription getSensorDescription() {
        return this.sensorDescription;
    }

    public String getSensorId() {
        return this.sensorId;
    }

    @XmlElement(name = "serviceReferences")
    public Collection<ServiceReference> getServiceReferences() {
        return this.serviceReferences;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setSensorDescription(SimpleSensorDescription sensorDescription) {
        this.sensorDescription = sensorDescription;
    }

    public void setSensorId(String id) {
        this.sensorId = id;
    }

    public void setServiceReferences(Collection<ServiceReference> serviceReferences) {
        this.serviceReferences = serviceReferences;
        // this.serviceReferences.addAll(serviceReferences);
    }

    @Override
    public String toString() {
        final int maxLen = 3;
        StringBuilder builder = new StringBuilder();
        builder.append("SearchResultElement [");
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
        if (this.sensorId != null) {
            builder.append("sensorId=");
            builder.append(this.sensorId);
            builder.append(", ");
        }
        if (this.serviceReferences != null) {
            builder.append("serviceReferences=");
            builder.append(toString(this.serviceReferences, maxLen));
            builder.append(", ");
        }
        if (this.classifiers != null) {
            builder.append("classifiers=");
            builder.append(toString(this.classifiers, maxLen));
            builder.append(", ");
        }
        if (this.keywords != null) {
            builder.append("keywords=");
            builder.append(toString(this.keywords, maxLen));
            builder.append(", ");
        }
        if (this.identifiers != null) {
            builder.append("identifiers=");
            builder.append(toString(this.identifiers, maxLen));
            builder.append(", ");
        }
        if (this.contacts != null) {
            builder.append("contacts=");
            builder.append(toString(this.contacts, maxLen));
            builder.append(", ");
        }
        if (this.inputs != null) {
            builder.append("inputs=");
            builder.append(toString(this.inputs, maxLen));
            builder.append(", ");
        }
        if (this.outputs != null) {
            builder.append("outputs=");
            builder.append(toString(this.outputs, maxLen));
            builder.append(", ");
        }
        if (this.beginDate != null) {
            builder.append("beginDate=");
            builder.append(this.beginDate);
            builder.append(", ");
        }
        if (this.endDate != null) {
            builder.append("endDate=");
            builder.append(this.endDate);
        }
        builder.append("]");
        return builder.toString();
    }

    private String toString(Collection< ? > collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator< ? > iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }

}

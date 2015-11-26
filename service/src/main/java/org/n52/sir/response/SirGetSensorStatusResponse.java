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
package org.n52.sir.response;

import java.util.Collection;

import net.opengis.gml.x32.TimeInstantType;
import net.opengis.gml.x32.TimePositionType;
import net.opengis.swe.x101.UomPropertyType;

import org.n52.oss.sir.api.SirStatusDescription;
import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.n52.sir.util.GMLDateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.GetSensorStatusResponseDocument;
import org.x52North.sir.x032.GetSensorStatusResponseDocument.GetSensorStatusResponse;
import org.x52North.sir.x032.StatusDescriptionDocument.StatusDescription;
import org.x52North.sir.x032.StatusDocument.Status;

/**
 * Internal response to get the sensor status
 * 
 * was orginally opengis.gml.x32.TimeInstantType
 * 
 * @author Jan Schulte
 * 
 */
public class SirGetSensorStatusResponse extends AbstractXmlResponse {

    private static final Logger log = LoggerFactory.getLogger(SirGetSensorStatusResponse.class);

    /**
     * the sensor status descriptions
     */
    private Collection<SirStatusDescription> statusDescs;

    @Override
    public GetSensorStatusResponseDocument createXml() {
        GetSensorStatusResponseDocument document = GetSensorStatusResponseDocument.Factory.newInstance();
        GetSensorStatusResponse getSensStatResp = document.addNewGetSensorStatusResponse();

        for (SirStatusDescription statDesc : this.statusDescs) {
            StatusDescription statusDescription = getSensStatResp.addNewStatusDescription();
            statusDescription.setSensorIDInSIR(statDesc.getSensorId());
            Status status = statusDescription.addNewStatus();

            // property name
            status.setPropertyName(statDesc.getStatus().getPropertyName());

            // property value
            status.setPropertyValue(statDesc.getStatus().getPropertyValue());

            // uom
            if ( !statDesc.getStatus().getUom().isEmpty()) {
                UomPropertyType uom = UomPropertyType.Factory.newInstance();
                uom.setCode(statDesc.getStatus().getUom());
                status.setUom(uom);
            }

            TimeInstantType timeInstantType = TimeInstantType.Factory.newInstance();
            TimePositionType timePosition = timeInstantType.addNewTimePosition();
            timePosition.setStringValue(GMLDateParser.getInstance().parseDate(statDesc.getStatus().getTimestamp()));
            timeInstantType.setId(XmlTools.generateGmlID(timeInstantType));

            status.setTimestamp(timeInstantType);
        }

        XmlTools.addSirAndSensorMLSchemaLocation(getSensStatResp);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the statusDescs
     */
    public Collection<SirStatusDescription> getStatusDescs() {
        return this.statusDescs;
    }

    /**
     * @param statusDescs
     *        the statusDescs to set
     */
    public void setStatusDescs(Collection<SirStatusDescription> statusDescs) {
        this.statusDescs = statusDescs;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirGetSensorStatusResponse: ");
        sb.append("StatusDescriptions: " + this.statusDescs);
        return sb.toString();
    }

}

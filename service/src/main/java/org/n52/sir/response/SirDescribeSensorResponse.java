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
package org.n52.sir.response;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.oss.util.XmlTools;
import org.n52.sir.sml.SmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte
 * 
 */
public class SirDescribeSensorResponse extends AbstractXmlResponse {

    private static Logger log = LoggerFactory.getLogger(SirDescribeSensorResponse.class);

    private XmlObject sensorML;

    private boolean validateResponse;

    public SirDescribeSensorResponse(boolean validateResponse) {
        this.validateResponse = validateResponse;
    }

    @Override
    public SensorMLDocument createXml() {
        SensorMLDocument document = null;

        if (this.sensorML instanceof SystemType) {
            log.debug("Have SystemType in response, wrapping in SensorMLDocument.");

            document = SmlTools.wrapSystemTypeInSensorMLDocument((SystemType) this.sensorML);
        }
        else if (this.sensorML instanceof SensorMLDocument) {
            log.debug("Returning SensorMLDocument from database.");

            document = (SensorMLDocument) this.sensorML;
        }
        else if (this.sensorML instanceof XmlAnyTypeImpl) {
            log.debug("Have XmlAnyTypeImpl, trying to parse.");
            // try parsing
            try {
                document = SensorMLDocument.Factory.parse(this.sensorML.getDomNode());
            }
            catch (XmlException e) {
                throw new UnsupportedOperationException("Sensor description was XmlAnyType but could not be parsed!");
            }
        }
        else {
            throw new UnsupportedOperationException("Sensor description was not a SystemType nor a SensorMLDocument with a System as the first member - case not implemented!");
        }

        // add schema location for validation
        XmlCursor cursor = document.newCursor();
        cursor.toFirstChild();
        cursor.setAttributeText(XmlTools.SCHEMA_LOCATION_ATTRIBUTE_QNAME, XmlTools.getSensorMLSchemaLocation());
        cursor.dispose();

        if (this.validateResponse) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the sensorML
     */
    public XmlObject getSensorML() {
        return this.sensorML;
    }

    /**
     * @param sensorML
     *        the sensorML to set
     */
    public void setSensorML(XmlObject sensorML) {
        this.sensorML = sensorML;
    }

}

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
package org.n52.oss.testdata.sml;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.opengis.sensorML.x101.ContactDocument.Contact;

/**
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 *
 */
public class TestComponent extends TestSensor {

    protected String parentSystemUniqueID;

    /**
     *
     * @param gmlDescription
     * @param keywords
     * @param uniqueId
     * @param longName
     * @param shortName
     * @param intendedApplication
     * @param sensorType
     * @param validTimeBegin
     * @param validTimeEnd
     * @param bboxLowerCorner
     * @param bboxUpperCorner
     * @param locationId
     * @param contact
     * @param latLonPosition
     * @param altitude
     * @param interfaces
     * @param inputs
     * @param output
     * @param parentSystemUniqueID
     */
    public TestComponent(String gmlDescription,
                         List<String> keywords,
                         String uniqueId,
                         String longName,
                         String shortName,
                         String intendedApplication,
                         String sensorType,
                         String validTimeBegin,
                         String validTimeEnd,
                         double[] bboxLowerCorner,
                         double[] bboxUpperCorner,
                         String locationId,
                         Contact contact,
                         double[] latLonPosition,
                         double altitude,
                         List<Map<String, String>> interfaces,
                         List<Map<String, String>> inputs,
                         List<Map<String, String>> output,
                         String parentSystemUniqueID) {
        super(gmlDescription,
              keywords,
              uniqueId,
              longName,
              shortName,
              intendedApplication,
              sensorType,
              validTimeBegin,
              validTimeEnd,
              bboxLowerCorner,
              bboxUpperCorner,
              locationId,
              contact,
              latLonPosition,
              altitude,
              interfaces,
              inputs,
              output,
              null);
        this.parentSystemUniqueID = parentSystemUniqueID;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.n52.sir.data.TestSensor#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TestComponent [uniqueId: ");
        sb.append(this.uniqueId);
        sb.append(", parent: ");
        sb.append(this.parentSystemUniqueID);
        sb.append(", keywords: ");
        sb.append(Arrays.toString(this.keywords.toArray()));
        sb.append(", gmlDescription: ");
        sb.append(this.gmlDescription);
        sb.append(", intendedApplication: ");
        sb.append(this.intendedApplication);
        sb.append(", sensorType: ");
        sb.append(this.sensorType);
        sb.append(", validTime (begin, end): (");
        sb.append(this.validTimeBegin);
        sb.append(", ");
        sb.append(this.validTimeEnd);
        sb.append(", latLonPosition, altitude: ");
        sb.append(Arrays.toString(this.latLonPosition));
        sb.append(", ");
        sb.append(this.altitude);
        sb.append(", bbox (upperCorner, lowerCorner): (");
        sb.append(Arrays.toString(this.bboxUpperCorner));
        sb.append(", ");
        sb.append(Arrays.toString(this.bboxLowerCorner));
        sb.append("]");
        return sb.toString();
    }

}

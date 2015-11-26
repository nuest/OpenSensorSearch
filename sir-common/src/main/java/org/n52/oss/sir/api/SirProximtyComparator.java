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
/**
 * @author Yakoub
 */

package org.n52.oss.sir.api;


import java.util.Comparator;

public class SirProximtyComparator implements Comparator<SirSearchResultElement> {
    private double longitude;

    private double latitude;

    public SirProximtyComparator(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public int compare(SirSearchResultElement o1,
            SirSearchResultElement o2)
    {
        SirBoundingBox b1 = ((SirSimpleSensorDescription) o1.getSensorDescription()).getBoundingBox();
        SirBoundingBox b2 = ((SirSimpleSensorDescription) o2.getSensorDescription()).getBoundingBox();
        double[] c1 = b1.getCenter();
        double[] c2 = b2.getCenter();
        
        double d1 =Math.pow((c1[0]-this.longitude),2)+Math.pow(c1[1]-this.latitude,2);
        double d2 =Math.pow((c2[0]-this.longitude),2)+Math.pow(c2[1]-this.latitude,2);
        return (int)(d1-d2);
    }
}

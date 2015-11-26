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
package org.n52.oss.ui.beans;

import org.x52North.sir.x032.VersionAttribute;
import org.x52North.sir.x032.VersionAttribute.Version.Enum;

/**
 * TODO consider moving this to common module
 * 
 * @author Daniel
 * 
 */
public class ClientConstants {

    public enum CapabilitiesSection {
        All, Contents, OperationsMetadata, ServiceIdentification, ServiceProvider
    }

    public static final String SERVICE_NAME = "SIR";

    public static String[] getAcceptedServiceVersions() {
        return new String[] {"0.3.0", "0.3.1", "0.3.2"};
    }

    public static Enum getServiceVersionEnum() {
        return VersionAttribute.Version.X_0_3_2;
    }

}

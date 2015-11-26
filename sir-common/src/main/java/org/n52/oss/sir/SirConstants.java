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
package org.n52.oss.sir;

/**
 * @author Jan Schulte
 * 
 */
public class SirConstants {

    public enum CapabilitiesSection {
        All, Contents, OperationsMetadata, ServiceIdentification, ServiceProvider
    }

    /**
     * enum with parameter names for GetCapabilities HTTP GET request
     */
    public enum GetCapGetParams {
        ACCEPTFORMATS, ACCEPTVERSIONS, REQUEST, SECTIONS, SERVICE, UPDATESEQUENCE;
    }

    /**
     * enum with parameters of DescribeSensor HTTP GET request
     */
    public enum GetDescSensorParams {
        REQUEST, SENSORIDINSIR;
    }

    /**
     * enum for all supported request operations by the SIR
     */
    public enum Operations {
        CancelSensorStatusSubscription, ConnectToCatalog, DeleteSensorInfo, DescribeSensor, DisconnectFromCatalog, GetCapabilities, GetSensorStatus, HarvestService, InsertSensorInfo, InsertSensorStatus, RenewSensorStatusSubscription, SearchSensor, SubscribeSensorStatus, UpdateSensorDescription
    }

    public static final String CHARSET_NAME = "UTF-8";

    public static final String CONTENT_TYPE_XML = "text/xml";

    /**
     * Name of the DescribeSensor operation in a capabitilities document
     */
    public static final String DESCRIBE_SENSOR_OPERATION_NAME = "DescribeSensor";

    public static final String GETREQUESTPARAM = "REQUEST";

    public static final String GETVERSIONPARAM = "version";

    public static final String IOOSCATAL0G_SERVICE_TYPE = "IOOSCATALOG";

    public static final String REQUEST_CONTENT_CHARSET = CHARSET_NAME;

    public static final String REQUEST_CONTENT_TYPE = CONTENT_TYPE_XML;

    public static final String RESPONSE_CONTENT_CHARSET = CHARSET_NAME;

    public static final String SERVICE_NAME = "SIR";

    public static final String SERVICE_VERSION_0_3_0 = "0.3.0";

    public static final String SERVICE_VERSION_0_3_1 = "0.3.1";

    public static final String SERVICE_VERSION_0_3_2 = "0.3.2";

    public static final Object SERVICEPARAM = "service";

    public static final String SOS_SERVICE_TYPE = "SOS";

    public static final String SOS_VERSION = "1.0.0";

    public static final String SPS_SERVICE_TYPE = "SPS";

}
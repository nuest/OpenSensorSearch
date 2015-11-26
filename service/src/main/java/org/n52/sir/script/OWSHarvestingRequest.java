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
package org.n52.sir.script;

import org.n52.oss.sir.Client;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceRequestDocument.HarvestServiceRequest;
import org.x52North.sir.x032.HarvestServiceResponseDocument;
import org.x52North.sir.x032.HarvestServiceResponseDocument.HarvestServiceResponse;

import com.google.inject.Inject;

public class OWSHarvestingRequest {

    private static Logger log = LoggerFactory.getLogger(OWSHarvestingRequest.class);

    @Inject
    Client client;

    public OWSHarvestingRequest() {
        //
    }

    public int harvestOWSService(String url, String serviceType, String interval) {
        log.info("Harvesting server at:" + url + " : " + serviceType);

        String request = buildRequest(url, serviceType, interval);
        try {

            String response = this.client.sendPostRequest(request);
            HarvestServiceResponseDocument respDoc = HarvestServiceResponseDocument.Factory.parse(response);

            HarvestServiceResponse harvestResponse = respDoc.getHarvestServiceResponse();

            return harvestResponse.getNumberOfInsertedSensors();

        }
        catch (Exception e) {
            log.error("Error harvesting {} @ {}", serviceType, url, e);
            return -1;
        }

    }

    private String buildRequest(String url, String serviceType, String interval) {
        String responseString = "";

        HarvestServiceRequestDocument requestDoc = HarvestServiceRequestDocument.Factory.newInstance();
        HarvestServiceRequest request = requestDoc.addNewHarvestServiceRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SirConfigurator.getInstance().getServiceVersionEnum());

        if (url != null && !url.equals("")) {
            request.setServiceURL(url);
        }
        if (serviceType != null && !serviceType.equals("")) {
            request.setServiceType(serviceType);
        }
        // interval
        if (interval != null && !interval.equals("")) {
            request.setHarvestIntervalSeconds(Integer.parseInt(interval));
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if (requestDoc.validate())
            responseString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
        else
            responseString = XmlTools.validateAndIterateErrors(requestDoc);

        return responseString;
    }

}

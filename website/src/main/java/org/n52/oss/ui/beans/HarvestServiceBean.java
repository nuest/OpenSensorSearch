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

import org.n52.oss.util.XmlTools;
import org.x52North.sir.x032.HarvestServiceRequestDocument;
import org.x52North.sir.x032.HarvestServiceRequestDocument.HarvestServiceRequest;


/**
 * @author Jan Schulte, Daniel Nüst
 * 
 */
public class HarvestServiceBean extends TestClientBean {

    private String harvestInterval = "";

    private String serviceType = "";

    private String serviceUrl = "";

    public HarvestServiceBean() {
        // empty constructor required for JSPs
    }

    public HarvestServiceBean(String serviceUrl, String serviceType) {
        this.serviceUrl = serviceUrl;
        this.serviceType = serviceType;
    }

    @Override
    public void buildRequest() {
        this.responseString = "";

        HarvestServiceRequestDocument requestDoc = HarvestServiceRequestDocument.Factory.newInstance();
        HarvestServiceRequest request = requestDoc.addNewHarvestServiceRequest();
        request.setService(ClientConstants.SERVICE_NAME);
        request.setVersion(ClientConstants.getServiceVersionEnum());

        // url
        if (this.serviceUrl != null && !this.serviceUrl.equals("")) {
            request.setServiceURL(this.serviceUrl);
        }
        // type
        if (this.serviceType != null && !this.serviceType.equals("")) {
            request.setServiceType(this.serviceType);
        }
        // interval
        if (this.harvestInterval != null && !this.harvestInterval.equals("")) {
            request.setHarvestIntervalSeconds(Integer.parseInt(this.harvestInterval));
        }

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if (requestDoc.validate())
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
        else
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
    }

    /**
     * @return the harvestInterval
     */
    public String getHarvestInterval() {
        return this.harvestInterval;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return this.serviceType;
    }

    /**
     * @return the serviceUrl
     */
    public String getServiceUrl() {
        return this.serviceUrl;
    }

    /**
     * @param harvestInterval
     *        the harvestInterval to set
     */
    public void setHarvestInterval(String harvestInterval) {
        this.harvestInterval = harvestInterval;
    }

    /**
     * @param serviceType
     *        the serviceType to set
     */
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    /**
     * @param serviceUrl
     *        the serviceUrl to set
     */
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

}

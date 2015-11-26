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

import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.DisconnectFromCatalogResponseDocument;
import org.x52North.sir.x032.DisconnectFromCatalogResponseDocument.DisconnectFromCatalogResponse;

/**
 * @author Jan Schulte
 *
 */
public class SirDisconnectFromCatalogResponse extends AbstractXmlResponse {

    private static final Logger log = LoggerFactory.getLogger(SirDisconnectFromCatalogResponse.class);

    /**
     * the url to the catalog service
     */
    private String catalogUrl;

    @Override
    public DisconnectFromCatalogResponseDocument createXml() {
        DisconnectFromCatalogResponseDocument document = DisconnectFromCatalogResponseDocument.Factory.newInstance();
        DisconnectFromCatalogResponse disconCat = document.addNewDisconnectFromCatalogResponse();

        XmlTools.addSirAndSensorMLSchemaLocation(disconCat);

        // set csw url
        disconCat.setCatalogURL(this.catalogUrl);

        if (SirConfigurator.getInstance().isValidateResponses()) {
            if ( !document.validate())
                log.warn("Service created invalid document!\n" + XmlTools.validateAndIterateErrors(document));
        }

        return document;
    }

    /**
     * @return the cswUrl
     */
    public String getCatalogUrl() {
        return this.catalogUrl;
    }

    /**
     * @param cswUrl
     *        the cswUrl to set
     */
    public void setCatalogUrl(String catalogUrl) {
        this.catalogUrl = catalogUrl;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SirDisconnectFromCatalogResponse: ");
        sb.append("CatalogUrl: " + this.catalogUrl);
        return sb.toString();
    }

}

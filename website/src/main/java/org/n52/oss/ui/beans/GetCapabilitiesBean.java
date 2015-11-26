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

import net.opengis.ows.x11.AcceptVersionsType;
import net.opengis.ows.x11.SectionsType;

import org.n52.oss.ui.beans.ClientConstants.CapabilitiesSection;
import org.n52.oss.util.XmlTools;
import org.x52North.sir.x032.GetCapabilitiesDocument;
import org.x52North.sir.x032.GetCapabilitiesDocument.GetCapabilities;

/**
 * @author Jan Schulte
 * 
 */
public class GetCapabilitiesBean extends TestClientBean {

    private String acceptVersions;

    private boolean all;

    private boolean contents;

    private boolean operationsMetadata;

    private String service;

    private boolean serviceIdentification;

    private boolean serviceProvider;

    private String updateSequence = "";

    /**
     * 
     */
    public GetCapabilitiesBean() {
        super();

        StringBuilder sb = new StringBuilder();
        String[] acceptedServiceVersions = ClientConstants.getAcceptedServiceVersions();
        for (String s : acceptedServiceVersions) {
            sb.append(s);
            sb.append(",");
        }
        sb.replace(sb.length() - 1, sb.length(), "");
        this.acceptVersions = sb.toString();
    }

    /**
     * @param service
     * @param updateSequence
     * @param acceptVersions
     * @param serviceIdentification
     * @param serviceProvider
     * @param operationsMetadata
     * @param contents
     * @param all
     */
    public GetCapabilitiesBean(String service,
                               String updateSequence,
                               String acceptVersions,
                               boolean serviceIdentification,
                               boolean serviceProvider,
                               boolean operationsMetadata,
                               boolean contents,
                               boolean all) {
        super();

        this.service = service;
        this.updateSequence = updateSequence;
        this.acceptVersions = acceptVersions;
        this.serviceIdentification = serviceIdentification;
        this.serviceProvider = serviceProvider;
        this.operationsMetadata = operationsMetadata;
        this.contents = contents;
        this.all = all;
    }

    @Override
    public void buildRequest() {
        this.responseString = "";

        GetCapabilitiesDocument requestDoc = GetCapabilitiesDocument.Factory.newInstance();
        GetCapabilities request = requestDoc.addNewGetCapabilities();

        // service
        request.setService(this.service);

        // update sequence
        if ( !this.updateSequence.isEmpty()) {
            request.setUpdateSequence(this.updateSequence);
        }

        // accept versions
        if ( !this.acceptVersions.isEmpty()) {
            AcceptVersionsType acceptVersion = request.addNewAcceptVersions();
            String[] versions = this.acceptVersions.split(",");
            for (String s : versions) {
                acceptVersion.addVersion(s);
            }
        }

        // Sections
        if (this.all || this.contents || this.operationsMetadata || this.serviceIdentification || this.serviceProvider) {
            SectionsType sections = request.addNewSections();

            // all
            if (this.all) {
                sections.addSection(CapabilitiesSection.ServiceIdentification.name());
                sections.addSection(CapabilitiesSection.ServiceProvider.name());
                sections.addSection(CapabilitiesSection.OperationsMetadata.name());
                sections.addSection(CapabilitiesSection.Contents.name());
            }

            else {
                // service Identification
                if (this.serviceIdentification) {
                    sections.addSection(CapabilitiesSection.ServiceIdentification.name());
                }
                // service provider
                if (this.serviceProvider) {
                    sections.addSection(CapabilitiesSection.ServiceProvider.name());
                }
                // operations Metadata
                if (this.operationsMetadata) {
                    sections.addSection(CapabilitiesSection.OperationsMetadata.name());
                }
                // Contents
                if (this.contents) {
                    sections.addSection(CapabilitiesSection.Contents.name());
                }
            }
        }

        // TODO implement handling of acceptFormats

        XmlTools.addSirAndSensorMLSchemaLocation(request);

        if (requestDoc.validate())
            this.requestString = requestDoc.xmlText(XmlTools.xmlOptionsForNamespaces());
        else
            this.requestString = XmlTools.validateAndIterateErrors(requestDoc);
    }

    /**
     * @return the acceptVersions
     */
    public String getAcceptVersions() {
        return this.acceptVersions;
    }

    /**
     * @return the service
     */
    public String getService() {
        return this.service;
    }

    /**
     * @return the updateSequence
     */
    public String getUpdateSequence() {
        return this.updateSequence;
    }

    /**
     * @return the all
     */
    public boolean isAll() {
        return this.all;
    }

    /**
     * @return the contents
     */
    public boolean isContents() {
        return this.contents;
    }

    /**
     * @return the operationsMetadata
     */
    public boolean isOperationsMetadata() {
        return this.operationsMetadata;
    }

    /**
     * @return the serviceIdentification
     */
    public boolean isServiceIdentification() {
        return this.serviceIdentification;
    }

    /**
     * @return the serviceProvider
     */
    public boolean isServiceProvider() {
        return this.serviceProvider;
    }

    /**
     * @param acceptVersions
     *        the acceptVersions to set
     */
    public void setAcceptVersions(String acceptVersions) {
        this.acceptVersions = acceptVersions;
    }

    /**
     * @param all
     *        the all to set
     */
    public void setAll(boolean all) {
        this.all = all;
    }

    /**
     * @param contents
     *        the contents to set
     */
    public void setContents(boolean contents) {
        this.contents = contents;
    }

    /**
     * @param operationsMetadata
     *        the operationsMetadata to set
     */
    public void setOperationsMetadata(boolean operationsMetadata) {
        this.operationsMetadata = operationsMetadata;
    }

    /**
     * @param service
     *        the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @param serviceIdentification
     *        the serviceIdentification to set
     */
    public void setServiceIdentification(boolean serviceIdentification) {
        this.serviceIdentification = serviceIdentification;
    }

    /**
     * @param serviceProvider
     *        the serviceProvider to set
     */
    public void setServiceProvider(boolean serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    /**
     * @param updateSequence
     *        the updateSequence to set
     */
    public void setUpdateSequence(String updateSequence) {
        this.updateSequence = updateSequence;
    }

}

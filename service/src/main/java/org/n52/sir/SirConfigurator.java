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
package org.n52.sir;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.IDAOFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.CapabilitiesDocument;
import org.x52North.sir.x032.VersionAttribute;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * Singleton class reads the config file and builds the RequestOperator and DAO
 * 
 * @author Jan Schulte, Daniel Nüst
 * 
 */
@Singleton
public class SirConfigurator {

    private static final String ACCEPTED_SERVICE_VERSIONS = "oss.sir.acceptedVersions";

    private static final String CAPABILITIESSKELETON_FILENAME = "oss.sir.capabilities.skeleton";

    private static final String GMLDATEFORMAT = "oss.sir.gml.dateformat";

    private static SirConfigurator instance = null;

    protected static Logger log = LoggerFactory.getLogger(SirConfigurator.class);

    private static final String SERVICEVERSION = "oss.sir.version";

    private static final String VALIDATE_XML_REQUESTS = "oss.sir.requests.validate";

    private static final String VALIDATE_XML_RESPONSES = "oss.sir.responses.validate";

    private static final String VERSION_SPLIT_CHARACTER = ",";

    /**
     * @deprecated use injection instead
     * @return Returns the instance of the SirConfigurator. Null will be returned if the parameterized
     *         getInstance method was not invoked before. Usuallex this will be done in the SIR
     */
    @Deprecated
    public static SirConfigurator getInstance() {
        return instance;
    }

    private String[] acceptedVersions;

    private CapabilitiesDocument capabilitiesSkeleton;

    private IDAOFactory factory;

    private String gmlDateFormat;

    private Properties props;

    private String serviceVersion;

    private String ScriptsPath;

    private String updateSequence;

    private boolean validateRequests;

    private boolean validateResponses;

    @Inject
    public SirConfigurator(IDAOFactory daoFactory, @Named("sir_properties")
    Properties props) throws OwsExceptionReport {
        this.factory = daoFactory;
        this.props = props;

        initialize();

        log.info("NEW {}", this);
    }

    public String[] getAcceptedServiceVersions() {
        return this.acceptedVersions;
    }

    public CapabilitiesDocument getCapabilitiesSkeleton() {
        return this.capabilitiesSkeleton;
    }

    @Deprecated
    public IDAOFactory getFactory() {
        return this.factory;
    }

    public String getGmlDateFormat() {
        return this.gmlDateFormat;
    }

    public String getServiceVersion() {
        return this.serviceVersion;
    }

    /**
     * does the translation from String representation of version number (to be optained by
     * getServiceVersion()) to enum of schema.
     * 
     * @return
     */
    public org.x52North.sir.x032.VersionAttribute.Version.Enum getServiceVersionEnum() {
        String sv = getServiceVersion();

        if (sv.equals(SirConstants.SERVICE_VERSION_0_3_0))
            return VersionAttribute.Version.X_0_3_0;

        if (sv.equals(SirConstants.SERVICE_VERSION_0_3_1))
            return VersionAttribute.Version.X_0_3_1;

        if (sv.equals(SirConstants.SERVICE_VERSION_0_3_2))
            return VersionAttribute.Version.X_0_3_2;

        throw new RuntimeException("Not a supported version!");
    }

    public String getUpdateSequence() {
        return this.updateSequence;
    }

    private void initialize() throws OwsExceptionReport {
        log.info(" * Initializing SirConfigurator ... ");

        this.serviceVersion = this.props.getProperty(SERVICEVERSION);
        this.gmlDateFormat = this.props.getProperty(GMLDATEFORMAT);

        this.acceptedVersions = this.props.getProperty(ACCEPTED_SERVICE_VERSIONS).split(VERSION_SPLIT_CHARACTER);
        this.validateRequests = Boolean.parseBoolean(this.props.getProperty(VALIDATE_XML_REQUESTS));
        this.validateResponses = Boolean.parseBoolean(this.props.getProperty(VALIDATE_XML_RESPONSES));

        newUpdateSequence();
        loadCapabilitiesSkeleton(this.props);

        log.info(" ***** Initialized SirConfigurator successfully! ***** ");
    }

    public String getScriptsPath() {
        return this.ScriptsPath;
    }

    public boolean isValidateRequests() {
        return this.validateRequests;
    }

    public boolean isValidateResponses() {
        return this.validateResponses;
    }

    private void loadCapabilitiesSkeleton(Properties sirProps) throws OwsExceptionReport {
        String skeletonPath = sirProps.getProperty(CAPABILITIESSKELETON_FILENAME);

        try (InputStream resource = SirConfigurator.class.getResourceAsStream(skeletonPath);) {

            log.info("Loading capabilities skeleton from " + skeletonPath);

            this.capabilitiesSkeleton = CapabilitiesDocument.Factory.parse(resource);
        }
        catch (Exception e) {
            log.error("Error on loading capabilities skeleton file: " + e.getMessage());
            OwsExceptionReport se = new OwsExceptionReport();
            se.addCodedException(OwsExceptionReport.ExceptionCode.NoApplicableCode,
                                 null,
                                 "Error on loading capabilities skeleton file: " + e.getMessage());

            throw se;
        }
    }

    public String newUpdateSequence() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(this.gmlDateFormat);
        this.updateSequence = dateFormat.format(new Date());
        return this.updateSequence;
    }

}
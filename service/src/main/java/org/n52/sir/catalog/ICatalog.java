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
package org.n52.sir.catalog;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * 
 * Interface for connections to a catalog. This can be a web service that is regularly updated from a SIR
 * instance. The main method is {@link ICatalog#pushAllDataToCatalog()}, that transfers all available
 * information in a format (that is accepted by the catalog) into the catalog. Methods for checking the
 * capabilites of the encapsulated catalog and the correctness of documents beforehand are available as well.
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 * 
 */
public interface ICatalog {

    public static final Date OLDEST_PUSH_DATE = new Date(0l);

    /**
     * Checks if the given document is conform with the required schema or profile of this catalog.
     * 
     * @param doc
     * @return
     * @throws OwsExceptionReport
     * @throws IOException 
     */
    public abstract boolean acceptsDocument(XmlObject doc) throws OwsExceptionReport, IOException;

    /**
     * 
     * Checks if the catalog instance is fit for the purpose of storing sensor information and tries to update
     * the catalog with the required elements.
     * 
     * @return
     * @throws OwsExceptionReport
     */
    public abstract boolean ensureSufficientCapabilities() throws OwsExceptionReport;

    /**
     * Returns an array with the number of {inserted, updated, deleted} sensors during the last
     * {@link ICatalog#pushAllDataToCatalog()} call.
     * 
     * @return
     */
    public abstract int[] getSummaryOfLastPush();

    /**
     * Checks if this catalog instance is fit for the purpose of storing sensor information, e.g. if the given
     * URL points at a valid catalog service that supports the required document profiles.
     * 
     * @return
     * @throws OwsExceptionReport
     */
    public abstract boolean hasSufficientCapabilities() throws OwsExceptionReport;

    /**
     * Saves all sensor information data that is stored within this instance of the SIR after
     * {@link ICatalog#OLDEST_PUSH_DATE} into this catalog instance. As the push might include a set of
     * transactions so the return type can be a list of errors that occurred.
     * 
     * @throws OwsExceptionReport
     */
    public abstract List<OwsExceptionReport> pushAllDataToCatalog() throws OwsExceptionReport;

    /**
     * 
     * Saves all sensor information data that is stored within this instance of the SIR and was updated after
     * the given Date into this catalog instance. As the push might include a set of transactions so the
     * return type can be a list of errors that occurred.
     * 
     * @param lastPush
     *        The {@link Date} after which the updates must have happened to be included in the push.
     * @return
     * @throws OwsExceptionReport
     */
    List<OwsExceptionReport> pushAllDataToCatalog(Date lastPush) throws OwsExceptionReport;

}

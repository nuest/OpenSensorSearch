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
package org.n52.sir.util;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.sir.api.SirSearchCriteria_Phenomenon;
import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 *
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 */
public class SORTools {

    private static final Logger log = LoggerFactory.getLogger(SORTools.class);

    @Inject
    SORClient client;

    public Collection<SirSearchCriteria_Phenomenon> getMatchingPhenomena(Collection<SirSearchCriteria_Phenomenon> phenomena) {
        Collection<SirSearchCriteria_Phenomenon> newPhenomena = new ArrayList<>();

        for (SirSearchCriteria_Phenomenon p : phenomena) {
            if (p.usesSOR()) {
                log.debug("Try to resolve matching phenomena based on {}", p);

                Collection<SirSearchCriteria_Phenomenon> currentNewPhenomena = null;
                try {
                    currentNewPhenomena = this.client.getMatchingTypes(p,
                            SirConfigurator.getInstance().isValidateRequests());
                } catch (Exception e) {
                    log.error("Could not get matching phenomena for " + p);
                }
                if (currentNewPhenomena != null) {
                    log.debug("Matched {} phenomena based on {}", currentNewPhenomena.size(), p);
                    newPhenomena.addAll(currentNewPhenomena);
                }
            }
        }
        return newPhenomena;
    }

}

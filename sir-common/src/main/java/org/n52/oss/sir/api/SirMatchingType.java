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
package org.n52.oss.sir.api;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType;
import org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest;

/**
 *
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 *
 */
public enum SirMatchingType {
    EQUIVALENT_TYPE, SUB_TYPE, SUPER_TYPE;

    public static SirMatchingType getSirMatchingType(MatchingType schemaMatchingType) throws OwsExceptionReport {
        if (schemaMatchingType.equals(MatchingType.SUPER_TYPE)) {
            return SirMatchingType.SUPER_TYPE;
        }
        else if (schemaMatchingType.equals(MatchingType.EQUIVALENT_TYPE)) {
            return SirMatchingType.EQUIVALENT_TYPE;
        }
        else if (schemaMatchingType.equals(MatchingType.SUB_TYPE)) {
            return SirMatchingType.SUB_TYPE;
        }
        else {
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         "MatchingType",
                                         "Your request was invalid: MatchingType parameter is missing or wrong!");
        }
    }

    public static SirMatchingType getSirMatchingType(String string) throws OwsExceptionReport {
        if (string.equalsIgnoreCase(SirMatchingType.SUPER_TYPE.toString())) {
            return SirMatchingType.SUPER_TYPE;
        }
        else if (string.equalsIgnoreCase(SirMatchingType.EQUIVALENT_TYPE.toString())) {
            return SirMatchingType.EQUIVALENT_TYPE;
        }
        else if (string.equalsIgnoreCase(SirMatchingType.SUB_TYPE.toString())) {
            return SirMatchingType.SUB_TYPE;
        }
        else {
            throw new OwsExceptionReport(OwsExceptionReport.ExceptionCode.InvalidRequest,
                                         "MatchingType",
                                         "Your request was invalid: MatchingType parameter is missing or wrong!");
        }
    }

    public static org.x52North.sor.x031.GetMatchingDefinitionsRequestDocument.GetMatchingDefinitionsRequest.MatchingType.Enum getSorMatchingType(SirMatchingType sirMatchingType) throws OwsExceptionReport {
        if (sirMatchingType.equals(SirMatchingType.SUPER_TYPE)) {
            return GetMatchingDefinitionsRequest.MatchingType.SUPER_TYPE;
        }
        else if (sirMatchingType.equals(SirMatchingType.EQUIVALENT_TYPE)) {
            return GetMatchingDefinitionsRequest.MatchingType.EQUIVALENT_TYPE;
        }
        else if (sirMatchingType.equals(SirMatchingType.SUB_TYPE)) {
            return GetMatchingDefinitionsRequest.MatchingType.SUB_TYPE;
        }

        OwsExceptionReport er = new OwsExceptionReport();
        er.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                             "MatchingType",
                             "MatchingType not supported!");
        throw er;
    }

    public org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType.Enum getSchemaMatchingType() throws OwsExceptionReport {
        if (this.equals(SirMatchingType.SUPER_TYPE)) {
            return org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType.SUPER_TYPE;
        }
        else if (this.equals(SirMatchingType.EQUIVALENT_TYPE)) {
            return org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType.EQUIVALENT_TYPE;
        }
        else if (this.equals(SirMatchingType.SUB_TYPE)) {
            return org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters.MatchingType.SUB_TYPE;
        }

        OwsExceptionReport er = new OwsExceptionReport();
        er.addCodedException(OwsExceptionReport.ExceptionCode.InvalidParameterValue,
                             "MatchingType",
                             "MatchingType not supported!");
        throw er;
    }
}

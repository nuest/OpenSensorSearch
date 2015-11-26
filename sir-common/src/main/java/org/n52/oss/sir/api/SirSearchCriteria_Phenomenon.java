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
import org.n52.oss.util.Tools;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon;
import org.x52North.sir.x032.SearchCriteriaDocument.SearchCriteria.Phenomenon.SORParameters;

/**
 *
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 *
 */
public class SirSearchCriteria_Phenomenon {

    private SirMatchingType matchingType;

    private String phenomenonName;

    private int searchDepth;

    private String sorUrl;

    public SirSearchCriteria_Phenomenon(Phenomenon phenomenon) throws OwsExceptionReport {

        this.phenomenonName = phenomenon.getPhenomenonName();

        if (phenomenon.isSetSORParameters()) {
            SORParameters sorParams = phenomenon.getSORParameters();
            this.sorUrl = sorParams.getSORURL();
            this.searchDepth = sorParams.getSearchDepth();
            // TODO test if this still works after schema overhaul
            this.matchingType = SirMatchingType.getSirMatchingType(sorParams.getMatchingType().toString());
        }
    }

    public SirSearchCriteria_Phenomenon(String phenomenonName) {
        this.phenomenonName = phenomenonName;
    }

    public SirSearchCriteria_Phenomenon(String phenomenonName,
            String sorUrl,
            SirMatchingType matchingType,
            int searchDepth) {
        this.phenomenonName = phenomenonName;
        this.sorUrl = sorUrl;
        this.matchingType = matchingType;
        this.searchDepth = searchDepth;
    }

    public SirMatchingType getMatchingType() {
        return this.matchingType;
    }

    public String getPhenomenonName() {
        return this.phenomenonName;
    }

    public int getSearchDepth() {
        return this.searchDepth;
    }

    public String getSorUrl() {
        return this.sorUrl;
    }

    public void setMatchingType(SirMatchingType matchingType) {
        this.matchingType = matchingType;
    }

    public void setPhenomenonName(String phenomenonName) {
        this.phenomenonName = phenomenonName;
    }

    public void setSearchDepth(int searchDepth) {
        this.searchDepth = searchDepth;
    }

    public void setSorUrl(String sorUrl) {
        this.sorUrl = sorUrl;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SirSearchCriteria_Phenomenon [phenomenonName: ");
        sb.append(this.phenomenonName);
        sb.append(", SOR parameters: URL = ");
        sb.append(this.sorUrl);
        sb.append(", matching type = ");
        sb.append(this.matchingType);
        sb.append(", search depth = ");
        sb.append(this.searchDepth);
        sb.append("]");
        return sb.toString();
    }

    /**
     * @return true if all parameters for SOR are given
     */
    public boolean usesSOR() {
        if (this.sorUrl == null || this.matchingType == null) {
            return false;
        }
        return Tools.noneEmpty(new String[]{this.sorUrl,
            this.matchingType.toString(),
            Integer.toString(this.searchDepth)});
    }

}

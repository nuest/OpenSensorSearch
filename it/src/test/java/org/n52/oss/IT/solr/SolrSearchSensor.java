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
package org.n52.oss.IT.solr;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.n52.sir.ds.solr.SolrConnection;

public class SolrSearchSensor {

    private SOLRSearchSensorDAO dao;

    @Before
    public void prepare() {
        SolrConnection c = new SolrConnection("http://localhost:8983/solr", 2000);
        this.dao = new SOLRSearchSensorDAO(c);
    }

    @Test
    public void wordslistIsCreatedCorrectlyFromSearchCriteria() {

        SirSearchCriteria searchCriteria = new SirSearchCriteria();
        searchCriteria.setSearchText(Arrays.asList(new String[] {"this", "is my", "searchText"}));
        String actual = this.dao.createWordslist(searchCriteria);
        String expected = "this+is my+searchText";

        assertThat("wordslist is correct", actual, is(equalTo(expected)));
    }

    @Test
    public void wordslistForNullSearchCriteria() {
        SirSearchCriteria searchCriteria = new SirSearchCriteria();
        String actual = this.dao.createWordslist(searchCriteria);
        String expected = "";

        assertThat("wordslist is correct", actual, is(equalTo(expected)));
    }

    @Test
    public void wordslistForEmptySearchCriteria() {
        SirSearchCriteria searchCriteria = new SirSearchCriteria();
        searchCriteria.setSearchText(Arrays.asList(new String[] {}));
        String actual = this.dao.createWordslist(searchCriteria);
        String expected = "";

        assertThat("wordslist is correct", actual, is(equalTo(expected)));
    }

    @Test
    public void wordslistForEmptyStringSearchCriteria() {
        SirSearchCriteria searchCriteria = new SirSearchCriteria();
        searchCriteria.setSearchText(Arrays.asList(new String[] {"", "", ""}));
        String actual = this.dao.createWordslist(searchCriteria);
        String expected = "";

        assertThat("wordslist is correct", actual, is(equalTo(expected)));
    }

}

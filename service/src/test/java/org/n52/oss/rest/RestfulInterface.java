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
package org.n52.oss.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSimpleSensorDescription;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.util.IgnoreDateValueComparator;
import org.n52.oss.util.Util;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.json.SimpleSensorDescription;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class RestfulInterface {

    private SensorResource resource;
    private static ISearchSensorDAO searchDao;
    private static String testId = "1";
    private static String testDesription = "test description";
    private static String testUrl = "http://a.test.url";
    private static String classifier01 = "myClass01";
    private static String identifier01 = "myId01";
    private static String keyword02 = "testkeyword02";
    private static String keyword01 = "testkeyword01";
    private static String output01 = "myOutput";
    private static String contact01 = "some@contact";
    private static String input02 = "anInput02";
    private static String input01 = "input01";

    @BeforeClass
    public static void createMockups() throws OwsExceptionReport {
        searchDao = mock(ISearchSensorDAO.class);
        SirSearchResultElement elem = new SirSearchResultElement();
        elem.setLastUpdate(new Date(0l));
        elem.setSensorId(testId);
        elem.setSensorDescription(new SirSimpleSensorDescription(null, testDesription, testUrl));
        when(searchDao.getSensorBySensorID(eq(testId), eq(true))).thenReturn(elem);

        SirSearchResultElement elemDetailed = new SirSearchResultElement();
        elemDetailed.setLastUpdate(new Date(0l));
        elemDetailed.setSensorId(testId);
        SirDetailedSensorDescription descr = new SirDetailedSensorDescription();
        descr.setClassifiers(Arrays.asList(new String[] {classifier01}));
        descr.setIdentifiers(Arrays.asList(new String[] {identifier01}));
        descr.setKeywords(Arrays.asList(new String[] {keyword01, keyword02}));
        descr.setId(testId);
        descr.setDescription(testDesription);
        descr.setOutputs(Arrays.asList(new String[] {output01}));
        descr.setContacts(Arrays.asList(new String[] {contact01}));
        descr.setBegineDate(new Date(17));
        descr.setEndDate(new Date(42));
        descr.setInputs(Arrays.asList(new String[] {input01, input02}));
        elemDetailed.setSensorDescription(descr);
        when(searchDao.getSensorBySensorID(eq(testId), eq(false))).thenReturn(elemDetailed);
    }

    @Before
    public void prepare() throws URISyntaxException {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getBaseUri()).thenReturn(new URI("TEST_URL"));
        this.resource = new SensorResource(searchDao, uriInfo);
    }

    @Test
    public void sensorResourceIsCorrect() {
        Response response = this.resource.getSensor(testId, false);
        SearchResultElement actual = (SearchResultElement) response.getEntity();

        assertThat("id matches", actual.getSensorId(), is(equalTo(testId)));
        SimpleSensorDescription actualDescription = actual.getSensorDescription();
        assertThat("id matches", actualDescription.getText(), is(equalTo(testDesription)));
        assertThat("id matches", actualDescription.getUrl(), is(equalTo(testUrl)));
    }

    @Test
    public void sensorResourceMatchesTestfile() throws JsonGenerationException,
            JsonMappingException,
            IOException,
            JSONException {
        Response response = this.resource.getSensor(testId, false);
        String actual = Util.entityToString(response);
        String expected = Util.readResourceFile("/responses/json/sensor1.json");

        JSONAssert.assertEquals(expected, actual, new IgnoreDateValueComparator(JSONCompareMode.LENIENT));
    }

    @Test
    public void sensorResourceMatchesDetailedTestfile() throws JsonGenerationException,
            JsonMappingException,
            IOException,
            JSONException {
        Response response = this.resource.getSensor(testId, true);
        String actual = Util.entityToString(response);
        String expected = Util.readResourceFile("/responses/json/sensor1_detailed.json");

        JSONAssert.assertEquals(expected, actual, new IgnoreDateValueComparator(JSONCompareMode.LENIENT));
    }
}

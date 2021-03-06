/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * author Yakoub
 */

package org.n52.sir.IT;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.n52.oss.util.GuiceUtil;
import org.x52North.sir.x032.SearchSensorRequestDocument;
import org.x52North.sir.x032.SearchSensorResponseDocument;

public class SearchSensorIT {

    private static Client client;

    @BeforeClass
    public static void setUp() {
        client = GuiceUtil.configureSirClient();
    }

    public SearchSensorResponseDocument searchSensor(String file) throws Exception {
        File f = new File( (ClassLoader.getSystemResource(file).getFile()));
        SearchSensorRequestDocument doc = SearchSensorRequestDocument.Factory.parse(f);

        XmlObject response = null;

        response = client.xSendPostRequest(doc);
        SearchSensorResponseDocument resp_doc = SearchSensorResponseDocument.Factory.parse(response.getDomNode());

        assertThat("response is valid (XMLBeans validate)", resp_doc.validate(), is(true));

        return resp_doc;
        // int send = doc.getSearchSensorRequest().getSensorIdentificationArray().length;
        // int response_count = resp_doc.getSearchSensorResponse().sizeOfSearchResultElementArray();
        // assertEquals(send, response_count);
    }

    @Test
    public void searchSensorbyIDInSIR() throws Exception {
        SearchSensorResponseDocument responseDocument = searchSensor("Requests/SearchSensor_bySensorIDInSIR.xml");

        // FIXME test must check if the returned sensor is the one with the used ID
    }

    @Test
    public void searchSensorbySearchCriteria() throws Exception {
        SearchSensorResponseDocument responseDocument = searchSensor("Requests/SearchSensor_bySearchCriteria.xml");

        // FIXME test must check if the returned sensor is the one with the used ID
    }

    @Test
    public void searchSensorByDescription() throws Exception {
        SearchSensorResponseDocument responseDocument = searchSensor("Requests/SearchSensor_byServiceDescription.xml");

        // FIXME test must check if the returned sensor contains the used service description
    }

}

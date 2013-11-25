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

package org.n52.oss.autocomplete;

/**
 * @author Yakoub 
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchCriteria;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.ds.solr.SOLRSearchSensorDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.api.core.HttpContext;

@Path("/suggest")
@RequestScoped
public class AutoCompleteSearch {

    private static Logger log = LoggerFactory.getLogger(SOLRSearchSensorDAO.class);

    private static final String REQUEST_PARAM_AUTOCOMPLETE = "q";

    @Context
    HttpServletRequest servletRequest;

    @Context
    private HttpContext servletContext;

    private ISearchSensorDAO searchSensorDao;

    @Inject
    public AutoCompleteSearch(@Named("autocomplete")
    ISearchSensorDAO dao) {
        this.searchSensorDao = dao;

        log.debug("NEW {}", this);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGet(@QueryParam(REQUEST_PARAM_AUTOCOMPLETE)
    String query) {
        log.debug("Autocomplete request: '{}'", query);

        if (query == null || query.isEmpty())
            return Response.status(Status.BAD_REQUEST).entity(" { error: \"Query parameter "
                    + REQUEST_PARAM_AUTOCOMPLETE + " must be given.\" }").build();

        Collection<String> results = new HashSet<>();

        SirSearchCriteria crit = new SirSearchCriteria();
        Collection<String> text = new ArrayList<>();
        text.add(query);
        crit.setSearchText(text);

        Collection<SirSearchResultElement> searchResults;
        try {
            searchResults = this.searchSensorDao.searchSensor(crit, false);
        }
        catch (OwsExceptionReport e) {
            return Response.serverError().entity(e).build();
        }

        for (SirSearchResultElement element : searchResults) {
            SirDetailedSensorDescription desc = (SirDetailedSensorDescription) element.getSensorDescription();
            results.addAll(desc.getKeywords());
            if (desc.getContacts() != null)
                results.addAll(desc.getContacts());
            if (desc.getDescription() != null)
                results.add(desc.getDescription().toString());
            if (desc.getInputs() != null)
                results.addAll(desc.getInputs());
            if (desc.getOutputs() != null)
                results.addAll(desc.getOutputs());
            if (desc.getIdentifiers() != null)
                results.addAll(desc.getIdentifiers());
            if (desc.getClassifiers() != null)
                results.addAll(desc.getClassifiers());
        }

        String result = null;

        if (results.size() == 0)
            result = "[]";
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("{ [");
            for (String s : results) {
                if (s.contains(query)) {
                    sb.append(s);
                    sb.append(", ");
                }
                else
                    log.debug("Got a result that is not contained in the query: {}", s);
            }
            if (sb.length() > 4)
                sb.replace(sb.length() - 2, sb.length(), ""); // remove last comma
            sb.append("] } ");
        }

        log.debug("Done serving autocomplete, response: {}", result);

        return Response.status(200).entity(result).header(HttpHeaders.CONTENT_LENGTH, result.length()).build();
    }

}

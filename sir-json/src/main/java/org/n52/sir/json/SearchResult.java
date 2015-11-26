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
package org.n52.sir.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement
@JsonInclude(Include.NON_NULL)
public class SearchResult {

    private String source;

    private String query;

    private String url;

    private String description;

    private String author;

    private Date date;

    private Collection<SearchResultElement> results;

    public SearchResult() {
        super();
        this.results = new ArrayList<>();
    }

    public SearchResult(String source, String query, String url, String description, String author, Date date) {
        this();
        this.source = source;
        this.query = query;
        this.url = url;
        this.description = description;
        this.author = author;
        this.date = date;
    }

    public SearchResult(String source,
                        String query,
                        String url,
                        String description,
                        String author,
                        Date date,
                        Collection<SearchResultElement> results) {
        this(source, query, url, description, author, date);
        this.results = results;
    }

    public void addResult(SearchResultElement result) {
        this.results.add(result);
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getQuery() {
        return this.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Collection<SearchResultElement> getResults() {
        return this.results;
    }

    public void setResults(Collection<SearchResultElement> result) {
        this.results = result;
    }

    @Override
    public String toString() {
        return "SearchResult [source=" + this.source + ", query=" + this.query + ", url=" + this.url + ", description="
                + this.description + ", author=" + this.author + ", date=" + this.date + ", result count="
                + this.results.size() + "]";
    }

}

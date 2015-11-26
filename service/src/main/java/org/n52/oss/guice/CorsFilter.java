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
package org.n52.oss.guice;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class CorsFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(CorsFilter.class);

    public static String VALID_METHODS = "HEAD, GET, OPTIONS, POST";

    public CorsFilter() {
        log.info("NEW {}", this);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("INIT {}", this);

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // simple:
        resp.addHeader("Access-Control-Allow-Origin", "*");
        chain.doFilter(request, response);

        // complex:

        // No Origin header present means this is not a cross-domain request
        // String origin = req.getHeader("Origin");
        // if (origin == null) {
        // // Return standard response if OPTIONS request w/o Origin header
        // if ("OPTIONS".equalsIgnoreCase(httpReq.getMethod())) {
        // resp.setHeader("Allow", VALID_METHODS);
        // resp.setStatus(200);
        // return;
        // }
        // }
        // else {
        // // This is a cross-domain request, add headers allowing access
        // resp.setHeader("Access-Control-Allow-Origin", origin);
        // resp.setHeader("Access-Control-Allow-Methods", VALID_METHODS);
        //
        // String headers = req.getHeader("Access-Control-Request-Headers");
        // if (headers != null)
        // resp.setHeader("Access-Control-Allow-Headers", headers);
        //
        // // Allow caching cross-domain permission
        // resp.setHeader("Access-Control-Max-Age", "3600");
        // }
        // // Pass request down the chain, except for OPTIONS
        // if ( !"OPTIONS".equalsIgnoreCase(req.getMethod())) {
        // chain.doFilter(req, resp);
        // }
    }

    @Override
    public void destroy() {
        log.info("DESTROY {}", this);
    }

}

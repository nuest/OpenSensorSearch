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
/** @author Yakoub
 */

package org.n52.oss.ui.controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.n52.oss.ui.WebsiteConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    private static Logger log = LoggerFactory.getLogger(AdminController.class);

    private static WebsiteConfig config = new WebsiteConfig();

    public AdminController() {
        log.info("NEW {}", this);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/validate")
    public String validateUser(@ModelAttribute(value = "username")
    String username, ModelMap map) {

        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String token = userDetails.getPassword();
            HttpPost post = new HttpPost(config.getApiEndpoint() + "/user/validate");
            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("username", username));
            pairs.add(new BasicNameValuePair("auth_token", token));

            post.setEntity(new UrlEncodedFormEntity(pairs));

            HttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(post);
            map.put("ValidationCalled", true);
            if (resp.getStatusLine().getStatusCode() == 200) {
                map.put("ValidationMsg", username + " validated succesfully");
            }
            else {
                map.put("ValidationMsg", username + " couldn't be validated!");
            }
            return "admin/validate";
        }
        catch (Exception e) {
            map.put("ValidationCalled", true);
            map.put("ValidationMsg", "Validation couldn't be done at the moment!");
            return "admin/validate";

        }

    }

    @RequestMapping("/validate")
    public String validateUserUI() {
        return "admin/validate";
    }

}

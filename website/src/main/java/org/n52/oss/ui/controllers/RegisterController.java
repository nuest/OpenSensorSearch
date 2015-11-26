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

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;

@Controller
@RequestMapping(value = "/register")
public class RegisterController {

    public class Status {
        public boolean success;
        public String reason;
    }
    
    private static Logger log = LoggerFactory.getLogger(RegisterController.class);

    private static WebsiteConfig config = new WebsiteConfig();

    public RegisterController() {
        log.info("NEW {}", this);
    }

    @RequestMapping(value = "/")
    public String index(ModelMap map) {
        return "register/index";
    }

    @RequestMapping(value = "/user")
    public String registerUser(@ModelAttribute(value = "username")
    String username, @ModelAttribute(value = "password")
    String password, ModelMap map, RedirectAttributes rs) {
        try {
            HttpPost post = new HttpPost(config.getApiEndpoint() + "/user/register");
            List<NameValuePair> pairs = new ArrayList<>();
            pairs.add(new BasicNameValuePair("username", username));
            pairs.add(new BasicNameValuePair("password", password));
            post.setEntity(new UrlEncodedFormEntity(pairs));

            HttpClient client = new DefaultHttpClient();
            HttpResponse resp = client.execute(post);
            StringBuilder result = new StringBuilder();
            String s = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            while ( (s = reader.readLine()) != null)
                result.append(s);

            Status reg_result = new Gson().fromJson(result.toString(), Status.class);
            if (reg_result.success) {
                map.put("RegisterSucceded", true);
                return "redirect:/";
            }

            String errorMsg = reg_result.reason == null ? "Cannot register currently" : reg_result.reason;
            map.put("RegisterFailed", true);
            map.put("ErrorMsg", errorMsg);
            return "register/index";
        }
        catch (Exception e) {
            return null;
        }

    }
}

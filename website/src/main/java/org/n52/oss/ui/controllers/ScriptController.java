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
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.n52.oss.ui.UploadForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.Gson;

@Controller
@RequestMapping("/script")
public class ScriptController {

    private static Logger log = LoggerFactory.getLogger(ScriptController.class);

    public class ScriptContent {
        public String content;
    }

    public static LinkedHashMap<String, String> licenses = new LinkedHashMap<>();

    public ScriptController() {
        log.info("NEW {}", this);
    }

    @RequestMapping("/schedule")
    public String harvest(ModelMap map) {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        map.addAttribute("auth_token", details.getPassword());
        return "script/schedule";
    }

    @RequestMapping("/index")
    public String index(ModelMap map) {
        return "script/index";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public String processForm(@ModelAttribute(value = "uploadForm")
    UploadForm form, ModelMap map) {
        String s = form.getFile().getFileItem().getName();
        MultipartEntity multipartEntity = new MultipartEntity();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = userDetails.getPassword();

        // upload the file
        File dest = new File(s);
        try {
            System.out.println("Chosen license:" + form.getLicense());
            log.info("Chosen license:" + form.getLicense());
            form.getFile().transferTo(dest);
            UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            multipartEntity.addPart("file", new FileBody(dest));
            multipartEntity.addPart("user", new StringBody(details.getUsername()));
            multipartEntity.addPart("licenseCode", new StringBody(form.getLicense()));
            multipartEntity.addPart("auth_token", new StringBody(token));
            HttpPost post = new HttpPost("script/submit");
            post.setEntity(multipartEntity);
            org.apache.http.client.HttpClient client = new DefaultHttpClient();
            HttpResponse resp;
            resp = client.execute(post);
            int responseCode = resp.getStatusLine().getStatusCode();
            StringBuilder builder = new StringBuilder();
            String str = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            while ( (str = reader.readLine()) != null)
                builder.append(str);
            System.out.println("return  id:" + builder.toString());
            log.info("return id:" + builder.toString());

            if (responseCode == 200) {
                map.addAttribute("harvestSuccess", true);
                map.addAttribute("resultScript", builder.toString());
                map.addAttribute("license", form.getLicense());
                return "script/status";
            }

                map.addAttribute("harvestError", true);
                return "script/status";
        }
        catch (Exception e) {
            map.addAttribute("errorMSG", e);
            return "script/status?fail";
        }
    }

    @RequestMapping("/show/")
    public String selectScript(ModelMap map) {
        return "script/selectScript";
    }

    @RequestMapping("/show/{scriptId}")
    public String show(@PathVariable
    String scriptId, ModelMap map) {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet("script/" + scriptId);
        try {
            HttpResponse resp = client.execute(get);
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            String s = null;
            while ( (s = reader.readLine()) != null)
                builder.append(s);
            ScriptContent content = new Gson().fromJson(builder.toString(), ScriptContent.class);
            map.addAttribute("content", content.content);
            return "script/show";
        }
        catch (Exception e) {
            map.addAttribute("error", e);
            return "script/error";
        }

    }

    // private void addLicenseToHeader(File f,License l) throws IOException{
    // RandomAccessFile random = new RandomAccessFile(f, "rw");
    // random.seek(0); // to the beginning
    // random.write(prepareLicenseStr(l).getBytes());
    // random.close();
    // }
    // private String prepareLicenseStr(License l ){
    // StringBuilder builder=new StringBuilder();
    // builder.append("/*");
    // builder.append("This work is licensed under:");
    // builder.append(l.description);
    // builder.append(" For more details please visit:");
    // builder.append(l.link);
    // builder.append("*/");
    // return builder.toString();
    // }

    @RequestMapping("/upload")
    public String upload(ModelMap map) {
        return "script/upload";
    }
}

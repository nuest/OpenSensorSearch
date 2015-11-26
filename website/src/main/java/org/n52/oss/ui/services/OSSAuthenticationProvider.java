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
/** @author Yakoub
 */

package org.n52.oss.ui.services;

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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service("userAuthService")
public class OSSAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(OSSAuthenticationProvider.class);

    public class AuthToken {
        String auth_token;
        boolean isValid;
        boolean isAdmin;
    }

    private static WebsiteConfig config = new WebsiteConfig();

    public OSSAuthenticationProvider() {
        log.info("NEW {}", this);
    }

    @Override
    public Authentication authenticate(Authentication arg0) throws AuthenticationException {
        String username = arg0.getName();
        String password = arg0.getCredentials().toString();

        AuthToken token = authenticateOSS(username, password);

        if (token.auth_token != null) {
            if ( !token.isValid)
                throw new UsernameNotFoundException("Username is not validated please contact site administration!");

            final List<GrantedAuthority> grantedAuths = new ArrayList<>();
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_SCRIPT_AUTHOR"));

            if (token.isAdmin)
                grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            final UserDetails principal = new User(username, token.auth_token, grantedAuths);
            final Authentication auth = new UsernamePasswordAuthenticationToken(principal,
                                                                                token.auth_token,
                                                                                grantedAuths);
            return auth;

        }

        throw new UsernameNotFoundException("Wrong username/password combination");
    }

    private AuthToken authenticateOSS(String username, String password) {
        try {
            HttpPost post = new HttpPost(config.getApiEndpoint() + "/user/login");
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

            AuthToken token = new Gson().fromJson(result.toString(), AuthToken.class);
            return token;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean supports(Class< ? > arg0) {
        return arg0.equals(UsernamePasswordAuthenticationToken.class);
    }

}

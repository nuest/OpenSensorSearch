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
package org.n52.oss.api;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.n52.sir.SirConfigurator;
import org.n52.sir.ds.IUserAccountDAO;
import org.n52.sir.util.SHA1HashGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;

/**
 * @author Yakoub
 * 
 */
@Path(ApiPaths.USER_PATH)
@RequestScoped
public class UserAccessResource {
    protected static Logger log = LoggerFactory.getLogger(UserAccessResource.class);
    private SirConfigurator config;

    @Inject
    public UserAccessResource(SirConfigurator config) {
        this.config = config;
    }

    @POST
    @Path("/login")
    public Response authenticate(@FormParam("username")
    String user, @FormParam("password")
    String password) {
        log.debug("Authentication requested for user {}", user);

        try {
            IUserAccountDAO dao = this.config.getInstance().getFactory().userAccountDAO();
            String token = dao.authenticateUser(user, password);
            boolean isValid = dao.isAdmin(user);
            boolean isAdmin  = dao.isValid(user);
            log.debug("Token for user {} is {}", user, token);

            if (token == null)
                return Response.ok("{status:fail}").build();

            return Response.ok("{auth_token:'" + token + "',isValid:'"+isValid+"',isAdmin:'"+isAdmin+"'}").build();
        }
        catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }
    @POST
    @Path("/register")
    public Response register(@FormParam("username")
    String user, @FormParam("password")
    String password) {
        log.debug("Authentication requested for user {}", user);

        try {
            IUserAccountDAO dao = this.config.getInstance().getFactory().userAccountDAO();
            String passwordHash = new SHA1HashGenerator().generate(password);
            boolean userExists = dao.nameExists(user);
            if(userExists)return Response.ok("{success:'false',reason:'User exists'}").build();
            
            boolean success  = dao.register(user, passwordHash);
            return Response.ok("{success:'"+success+"'}").build();
        }
        catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }
    @POST
    @Path("/validate")
    public Response validate(@FormParam("username")
    String user, @FormParam("auth_token")
    String auth_token) {
        log.debug("Authentication requested for user {}", user);

        try {
            IUserAccountDAO dao = this.config.getInstance().getFactory().userAccountDAO();
            String admin_id = dao.getUserIDForToken(auth_token);
            String admin_user_name = dao.userNameForId(admin_id);
            String user_id = dao.getUserIDForUsername(user);
            boolean isAdmin = dao.isAdmin(admin_user_name);
            if(!isAdmin)return Response.status(403).entity("{status:Fail insufficent permission}").build();
            boolean result =  dao.validate(user_id);
            
            return Response.ok("{status:'"+result+"'}").build();
        }
        catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }
    
}

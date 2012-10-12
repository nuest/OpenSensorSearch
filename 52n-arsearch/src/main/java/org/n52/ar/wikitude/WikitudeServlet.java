/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.ar.wikitude;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.ar.SirCallbackServlet;
import org.n52.ar.SirPOI;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.json.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 * @author Arne de Wall
 */
public class WikitudeServlet extends SirCallbackServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 7433725421346384867L;
    
    private String layerName = "fumxorm";
    
    private static Logger log = LoggerFactory.getLogger(WikitudeServlet.class);

    public WikitudeServlet() {
        log.debug("NEW {}", this);
    }

    @Override
    protected SirPOI createPOI(SearchResultElement sre) {
        WikitudePOI p = new WikitudePOI();
		ArrayList<ServiceReference> references = (ArrayList<ServiceReference>) sre
				.getServiceReferences();
		p.title = references.get(0).getServiceSpecificSensorId(); 
        p.id = sre.getSensorIdInSir();
        
        // location paramters
        double[] latLon = sre.getSensorDescription().getBoundingBox().getCenter();
        p.lat = latLon[0];
        p.lon = latLon[1];
        p.alt = 0;
        
        String description = sre.getSensorDescription().getText();
        Scanner scanner = new Scanner(description);
        while(scanner.hasNext()){
        	
        }
        
        // Description 
        p.description = sre.getSensorDescription().getText();
        
        return p;
    }

    /**
     * http://www.wikitude.com/developer/publish-details
     * www.openarml.org/wikitude4.html
     * Just need a ARML transformer??
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userAgent = request.getHeader("User-agent");
        log.debug("User-agent={}", userAgent);
        
        // TODO implement callback URL
        double latitude = 51.965344, longitude = 7.600003;
        int maxPois = 20;
        float radius = 10000; 
        
        // get parameters
        // Wikitude does not work at the moment. Input parameter by wikitude
        // does not match with real parameter in any case.
        // lat-input 52.25, lon-input -8.25
        String latParameter = request.getParameter("latitude");
        String lonParameter = request.getParameter("longitude");
        if(latParameter != null || lonParameter != null){
        	latitude = Double.parseDouble(latParameter);
        	longitude = Double.parseDouble(lonParameter);
        }
        double[] center = new double[] {latitude, longitude};
        
        String maxParameter = request.getParameter("maxNumberOfPois");
        if(maxParameter != null){
        	maxPois = Integer.parseInt(maxParameter);
        }
        
        response.setContentType("application/xml; charset=utf-8");
        WikitudeResponse wikiRes = null;
        // query SIR
        try{
        	Collection<SirPOI> queryResult = querySIR(center, radius, CONTENT_TYPE);
        	wikiRes = new WikitudeResponse(queryResult);
        } catch (Exception e){
        	log.error("Error querying SIR.", e);
        }
        
        // write response
        StringBuilder sb = new StringBuilder();
        	wikiRes.toARML(sb);
        
        Writer out = response.getWriter();
        response.setContentType(CONTENT_TYPE_XML);
        out.write(sb.toString());
        
        // clean up
        response.flushBuffer();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        log.info("Initialized " + this);
    }
    
    public String getLayerName(){
    	return layerName;
    }
}
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

import java.io.IOException;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.TransformerException;

import net.opengis.ows.ExceptionReportDocument;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.json.Converter;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.sir.json.SearchResultElement;
import org.n52.sir.sml.SensorMLDecoder;
import org.n52.sir.xml.ITransformer;
import org.n52.sir.xml.ITransformer.TransformableFormat;
import org.n52.sir.xml.TransformerModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

/**
 * @author Yakoub, Daniel
 * 
 */
@Path(ApiPaths.TRANSFORMATION_PATH)
@Api(value = "/" + ApiPaths.TRANSFORMATION_PATH, description = "Conversion of SensorML document to different formats")
@RequestScoped
public class TransformationResource {

    private static Logger log = LoggerFactory.getLogger(TransformationResource.class);

    private SensorMLDecoder decoder;

    private Converter converter;

    private Set<ITransformer> transformers;

    @Inject
    public TransformationResource(Set<ITransformer> transformers) {
        this.transformers = transformers;

        this.decoder = new SensorMLDecoder();
        this.converter = new Converter();
    }

    // private String toJsonString(String sensorML) throws XmlException {
    // SensorMLDocument document = SensorMLDocument.Factory.parse(sensorML);
    // return this.gson.toJson(document);
    // }

    // public Response toJson(String sensor) {
    // try {
    // String response = toJsonString(sensor);
    // return Response.ok(response).build();
    // }
    // catch (XmlException e) {
    // return Response.ok("{ \"error\" : \"Cannot parse sensorML\" }").build();
    // }
    // }

    @GET
    @ApiOperation(value = "index of the available transformations")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIndex() {
        StringBuilder sb = new StringBuilder();

        sb.append("{ \"transformations\" : [");

        sb.append(" { \"id\" : \"sml100_to_ebrim101\", ");
        sb.append(" \"input\" : ");
        sb.append("\"text/xml;subtype='sensorML/1.0.0'\"");
        sb.append(" , \"output\" : ");
        sb.append("\"text/xml;subtype='EbRIM/1.0.1'\"");
        sb.append(" } ");
        sb.append("] }");

        return Response.ok(sb.toString()).build();
    }

    @POST
    @ApiOperation(value = "Convert sensor description to a specific form", notes = "The output can be json or ebrim.")
    @Produces(MediaType.APPLICATION_JSON)
    public Response convertSmlToJson(String data) {
        log.debug("Transforming to json: {}", data);

        try {
            SensorMLDocument sml = SensorMLDocument.Factory.parse(data);

            SirSensor decoded = this.decoder.decode(sml);

            SearchResultElement converted = this.converter.convert(decoded, true);

            return Response.ok(converted).build();
        }
        catch (XmlException e) {
            log.error("Could not *parse* SensorML for transformation.", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\" : \"" + e.getMessage() + "\" } ").build();
        }
    }

    @POST
    @ApiOperation(value = "Convert sensor description to a specific form", notes = "The output can be json or ebrim.")
    @Produces(MediaType.APPLICATION_XML)
    public Response convertSmlToEbrim(String data) {
        log.debug("Transforming to EbRIM: {}", data.substring(0, Math.min(data.length(), 1000)));
        SensorMLDocument sensorMLDocument;
        try {
            sensorMLDocument = SensorMLDocument.Factory.parse(data);
        }
        catch (XmlException e) {
            log.error("Could not parse SensorML: " + data, e);
            OwsExceptionReport oer = new OwsExceptionReport(ExceptionCode.InvalidRequest,
                                                            "input",
                                                            "Could not parse SensorML from '" + data + "'");

            ExceptionReportDocument document = oer.getDocument();
            Document doc = (Document) document.getDomNode();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(doc).build();
            // return Response.status(Response.Status.BAD_REQUEST).entity(oer).build();
        }

        log.debug("Transforming SML to EbRim... SML: {} [...]", sensorMLDocument.xmlText().substring(0, 300));

        ITransformer transformer = TransformerModule.getFirstMatchFor(this.transformers,
                                                                      TransformableFormat.SML,
                                                                      TransformableFormat.EBRIM);

        try {
            XmlObject transformed = transformer.transform(sensorMLDocument);
            Document doc = (Document) transformed.getDomNode();
            return Response.ok(doc).build();
        }
        catch (XmlException | TransformerException | IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\": \"Cannot parse sensorML\"; \"reason\":\" "
                    + e.getMessage() + "\" }").build();
        }
    }

}

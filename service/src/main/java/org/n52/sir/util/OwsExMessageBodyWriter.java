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
package org.n52.sir.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.opengis.ows.ExceptionReportDocument;

import org.n52.oss.sir.ows.OwsExceptionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.inject.Singleton;

/**
 * https://jersey.java.net/documentation/latest/message-body-workers.html
 * 
 * Writer must be binded:
 * http://stackoverflow.com/questions/11216321/guice-jersey-custom-serialization-of-entities
 * 
 * Based on DocumentProvider.class
 * 
 * @author Daniel
 * 
 */
@Singleton
@Produces({MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
@Provider
public class OwsExMessageBodyWriter implements MessageBodyWriter<OwsExceptionReport> {

    private static final Logger log = LoggerFactory.getLogger(OwsExMessageBodyWriter.class);

    private final TransformerFactory tf;

    public OwsExMessageBodyWriter() {
        this.tf = TransformerFactory.newInstance();
    }

    @Override
    public boolean isWriteable(Class< ? > type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == OwsExceptionReport.class;
    }

    @Override
    public long getSize(OwsExceptionReport report,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(OwsExceptionReport report,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        log.debug("Writing {}", report);

        // FIX ME: writeTo method not working for OwsExMessageBodyWriter:
        // if (mediaType.equals(MediaType.TEXT_HTML_TYPE)) {
        // PrintWriter writer = new PrintWriter(entityStream);
        // writer.println("<html><body>");
        // ExceptionReportDocument document = myBean.getDocument();
        // writer.println(document.xmlText());
        // writer.println("</body></html>");
        //
        // writer.flush();
        // return;
        // }
        //
        // ExceptionReportDocument document = myBean.getDocument();
        //
        // // serialize the entity myBean to the entity output stream
        // document.save(entityStream);
        // entityStream.flush();

        // next try, does not work:
        // try {
        // JAXBContext jaxbContext = JAXBContext.newInstance(OwsExceptionReport.class);
        //
        // // serialize the entity myBean to the entity output stream
        // jaxbContext.createMarshaller().marshal(report, entityStream);
        // }
        // catch (JAXBException e) {
        // throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        // }

        ExceptionReportDocument document = report.getDocument();
        Document doc = (Document) document.getDomNode();

        try {
            StreamResult sr = new StreamResult(entityStream);
            this.tf.newTransformer().transform(new DOMSource(doc), sr);
        }
        catch (TransformerException ex) {
            throw new WebApplicationException(ex, Status.INTERNAL_SERVER_ERROR);
        }

        entityStream.flush();
        // do not close stream, done by jersey!
    }

}

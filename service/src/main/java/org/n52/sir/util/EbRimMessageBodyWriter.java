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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;
import x0.oasisNamesTcEbxmlRegrepXsdRim3.impl.RegistryPackageDocumentImpl;

/**
 * https://jersey.java.net/documentation/latest/message-body-workers.html
 *
 * FIXME this does not work yet, instead I'm casting the response to a org.w3c.dom.Document before returning
 * it:
 *
 * <code>
 * XmlObject transformed = transformer.transform(sensorMLDocument);
 * Document doc = (Document) transformed.getDomNode();
 * return Response.ok(doc).build();G
 * </code>
 *
 * @author Daniel
 *
 */
public class EbRimMessageBodyWriter implements MessageBodyWriter<RegistryPackageDocumentImpl> {

    @Override
    public boolean isWriteable(Class< ? > type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == RegistryPackageDocument.class;
    }

    @Override
    public long getSize(RegistryPackageDocumentImpl t,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(RegistryPackageDocumentImpl t,
                        Class< ? > type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(RegistryPackageDocument.class);

            // serialize the entity myBean to the entity output stream
            jaxbContext.createMarshaller().marshal(t, entityStream);
        }
        catch (JAXBException e) {
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}

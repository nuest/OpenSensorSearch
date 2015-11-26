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
package org.n52.sir.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import net.opengis.ows.ExceptionReportDocument;
import net.opengis.ows.ExceptionReportDocument.ExceptionReport;
import net.opengis.ows.ExceptionType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.sir.ows.OWSConstants;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.oss.sir.ows.OwsExceptionReport.ExceptionCode;
import org.n52.oss.util.Tools;
import org.n52.oss.util.XmlTools;

/**
 * Implementation of the <code>ISirResponse</code> interface for OGC service exceptions.
 * 
 * @author Alexander C. Walkowski, Daniel Nüst
 * @version 0.1
 */
public class ExceptionResponse implements ISirResponse {

    /** the exception report Document */
    private ExceptionReportDocument erd;

    /**
     * @param erd
     *        the exception report document for which the exception response should be created
     */
    public ExceptionResponse(ExceptionReportDocument erd) {
        this.erd = erd;
        addNamespace();
    }

    public ExceptionResponse(OwsExceptionReport oer) {
        this(oer.getDocument());
    }

    /**
     * 
     * @param e
     *        a regular exception to be wrapped in a exception response
     */
    public ExceptionResponse(Exception e) {
        this.erd = ExceptionReportDocument.Factory.newInstance();
        ExceptionReport exceptionReport = this.erd.addNewExceptionReport();
        ExceptionType exception = exceptionReport.addNewException();
        StringBuilder sb = new StringBuilder();
        sb.append(e.getClass().toString());
        sb.append(": ");
        sb.append(e.getMessage());
        sb.append("\n\n");
        sb.append(Tools.getStackTrace(e));
        exception.addExceptionText(sb.toString());
        exception.setExceptionCode(ExceptionCode.NoApplicableCode.toString());

        addNamespace();
    }

    private void addNamespace() {
        XmlCursor cursor = this.erd.newCursor();
        if (cursor.toFirstChild()) {
            cursor.setAttributeText(XmlTools.SCHEMA_LOCATION_ATTRIBUTE_QNAME, OWSConstants.NAMESPACE + " "
                    + OWSConstants.SCHEMA_LOCATION);
        }
    }

    /**
     * @return Returns the response as byte[]
     * @throws IOException
     *         if getting the byte[] failed
     */
    @Override
    public byte[] getByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XmlOptions options = new XmlOptions();
        options.setSaveNamespacesFirst();
        options.setSaveAggressiveNamespaces();
        options.setSavePrettyPrint();
        options.setCharacterEncoding("UTF-8");

        HashMap<String, String> suggestedPrefixes = new HashMap<>();
        suggestedPrefixes.put(OWSConstants.NAMESPACE, OWSConstants.NAMESPACE_PREFIX);
        options.setSaveSuggestedPrefixes(suggestedPrefixes);

        this.erd.save(baos, options);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    /**
     * @return Returns the the length of the content in bytes
     * @throws IOException
     *         if getting the content length failed
     */
    @Override
    public int getContentLength() throws IOException {
        return getByteArray().length;
    }

    /**
     * @return Returns the content type of this response. The returned value is the constant
     *         ScsConstant.CONTENT_TYPE.
     */
    @Override
    public String getContentType() {
        return SirConstants.CONTENT_TYPE_XML;
    }

}
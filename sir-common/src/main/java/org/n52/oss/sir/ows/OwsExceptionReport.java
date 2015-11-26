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
package org.n52.oss.sir.ows;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import net.opengis.ows.ExceptionReportDocument;
import net.opengis.ows.ExceptionReportDocument.ExceptionReport;
import net.opengis.ows.ExceptionType;

import org.apache.xmlbeans.XmlCursor;
import org.n52.oss.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the ows service exception. The exception codes are defined according the ows common spec.
 * version 1.0
 *
 * @author Alexander C. Walkowski, Daniel Nüst (minor improvements)
 *
 */
@Deprecated // use iceland one soon
public class OwsExceptionReport extends Exception {

    /**
     * ExceptionCodes as defined in the OWS Common Implementation Specification 0.3.0
     */
    public enum ExceptionCode {

        InvalidParameterValue, InvalidRequest, InvalidUpdateSequence, MissingParameterValue, NoApplicableCode, NoDataAvailable, OperationNotSupported, VersionNegotiationFailed
    }

    /** Exception levels */
    public enum ExceptionLevel {

        DetailedExceptions, PlainExceptions
    }

    private static final Logger log = LoggerFactory.getLogger(OwsExceptionReport.class.getName());

    private static final long serialVersionUID = 9069373009339881302L;

    private ExceptionLevel excLevel = null;

    private ArrayList<ExceptionType> excs = new ArrayList<>();

    /**
     * standard constructor without parameters, sets the ExceptionLevel on PlainExceptions
     *
     */
    public OwsExceptionReport() {
        this.excLevel = ExceptionLevel.DetailedExceptions;
    }

    public OwsExceptionReport(ExceptionCode code, String locator, String message) {
        super();

        addCodedException(code, locator, message);
    }

    public OwsExceptionReport(ExceptionLevel excLevelIn) {
        this.excLevel = excLevelIn;
    }

    public OwsExceptionReport(String message, Throwable cause) {
        super(message, cause);
        this.excLevel = ExceptionLevel.DetailedExceptions;
        addCodedException(ExceptionCode.NoApplicableCode, message, cause);
    }

    public OwsExceptionReport(Throwable cause) {
        super(cause);
        this.excLevel = ExceptionLevel.DetailedExceptions;
    }

    /**
     * adds a coded Exception to this service exception with code, locator and the exception itself as
     * parameters
     *
     * @param code
     *        ExceptionCode of the added exception
     * @param locator
     *        String locator of the added exception
     * @param cause
     *        Exception which should be added
     */
    public void addCodedException(ExceptionCode code, String locator, Throwable cause) {

        ExceptionType et = ExceptionType.Factory.newInstance();
        et.setExceptionCode(code.toString());
        if (locator != null) {
            et.setLocator(locator);
        }

        String name = cause.getClass().getName();
        String message = cause.getMessage();
        StackTraceElement[] stackTraces = cause.getStackTrace();

        StringBuilder sb = new StringBuilder();
        sb.append("[EXC] internal service exception");
        if (this.excLevel.compareTo(ExceptionLevel.PlainExceptions) == 0) {
            sb.append(". Message: ").append(message);
        } else if (this.excLevel.compareTo(ExceptionLevel.DetailedExceptions) == 0) {
            sb.append(MessageFormat.format(": {0}\n", name));
            sb.append("[EXC] message: ").append(message).append("\n");
            for (int i = 0; i < stackTraces.length; i++) {
                StackTraceElement element = stackTraces[i];
                sb.append("[EXC]").append(element.toString()).append("\n");
            }
        } else {
            log.warn("addCodedException: unknown ExceptionLevel " + "(" + this.excLevel.toString() + ")occurred.");
        }

        et.addExceptionText(sb.toString());
        // i guess there is a better way to format an exception

        this.excs.add(et);
    }

    public void addCodedException(ExceptionCode code, String locator, String message) {
        addCodedException(code, locator, new String[]{message});
    }

    public void addCodedException(ExceptionCode code, String locator, String[] messages) {
        ExceptionType et = ExceptionType.Factory.newInstance();
        et.setExceptionCode(code.toString());
        if (locator != null) {
            et.setLocator(locator);
        }
        for (String string : messages) {
            et.addExceptionText(string);
        }
        this.excs.add(et);
    }

    public void addServiceException(OwsExceptionReport seIn) {
        this.excs.addAll(seIn.getExceptions());
    }

    /**
     * checks whether the ExceptionCode parameter is contain in this exception
     *
     * @param ec
     *        ExceptionCode which should be checked
     * @return Returns boolean true if ExceptionCode is contained, otherwise false
     */
    public boolean containsCode(ExceptionCode ec) {
        for (ExceptionType et : this.excs) {
            if (et.getExceptionCode().equalsIgnoreCase(ec.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * checks whether this service exception contains another exception
     *
     * @return Returns true if this service exception contains another exception
     */
    public boolean containsExceptions() {
        return this.excs.size() > 0;
    }

    /**
     *
     * @return Returns the ExceptionReportDocument XmlBean created from this service exception
     */
    public ExceptionReportDocument getDocument() {

        ExceptionReportDocument erd = ExceptionReportDocument.Factory.newInstance();
        ExceptionReport er = erd.addNewExceptionReport(); // ExceptionReport.Factory.newInstance();
        // er.setLanguage("en");
        er.setVersion(OWSConstants.OWS_VERSION);

        for (ExceptionType e : this.excs) {
            ExceptionType newException = er.addNewException();
            newException.setExceptionCode(e.getExceptionCode());
            newException.setLocator(e.getLocator());
            newException.setExceptionTextArray(e.getExceptionTextArray());
        }
        // er.setExceptionArray(this.excs.toArray(new ExceptionType[this.excs.size()]));
        // erd.setExceptionReport(er);

        XmlCursor c = erd.newCursor(); // Cursor on the documentc.toStartDoc();
        c.toFirstChild();
        c.setAttributeText(XmlTools.SCHEMA_LOCATION_ATTRIBUTE_QNAME, OWSConstants.NAMESPACE + " "
                + OWSConstants.SCHEMA_LOCATION);
        c.dispose();
        return erd;
    }

    /**
     *
     * @return Returns the ExceptionTypes of this exception
     */
    public ArrayList<ExceptionType> getExceptions() {
        return this.excs;
    }

    @Override
    public String getMessage() {
        String superMsg = super.getMessage();
        if (superMsg == null) {
            StringBuilder sb = new StringBuilder();
            for (ExceptionType e : this.excs) {
                sb.append("[");
                sb.append(Arrays.toString(e.getExceptionTextArray()));
                sb.append("]");
            }
            return sb.toString();
        }
        return superMsg;
    }
}

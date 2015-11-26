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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.n52.oss.util.XmlTools;

/**
 *
 * Some helper methods when dealing with SOAP classes (from the package <code>java.xml.soap</code>).
 *
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 *
 */
public class SoapTools {

    private static String inspect(Detail d) {
        StringBuilder sb = new StringBuilder();
        sb.append("Detail [");

        Iterator< ? > iter = d.getDetailEntries();
        while (iter.hasNext()) {
            DetailEntry entry = (DetailEntry) iter.next();
            sb.append(" [entry=");
            sb.append(XmlTools.xmlToString(entry));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String inspect(SOAPFault fault) {
        StringBuilder sb = new StringBuilder();
        sb.append("Fault [faultcode: ");
        sb.append(fault.getFaultCode());
        sb.append(", faultstring: ");
        sb.append(fault.getFaultString());
        sb.append(", faultactor: ");
        sb.append(fault.getFaultActor());
        sb.append(", detail: ");
        Detail d = fault.getDetail();
        sb.append(inspect(d));
        sb.append("]");
        return sb.toString();
    }

    public static String toString(SOAPMessage message) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            message.writeTo(out);
            out.close();
        }
        catch (SOAPException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = new String(out.toByteArray());
        return s;
    }
}

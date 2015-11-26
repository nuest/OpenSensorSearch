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
package org.n52.oss.opensearch.listeners;

import java.util.ArrayList;
import java.util.Collection;

import org.n52.oss.opensearch.OpenSearchConstants;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.api.SirSimpleSensorDescription;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public class OpenSearchTools {

    public static void concatenateWithLimit(StringBuilder sb, ArrayList<String> list, int maxElements) {
        ArrayList<String> myList = new ArrayList<>();
        while (myList.size() < maxElements) {
            // duplicate!
            myList.addAll(list);
        }

        int i = 0;
        while (i < maxElements) {
            String s = myList.get(i);
            sb.append(s);

            i++;
            if (i == maxElements)
                break;

            sb.append(",");
        }
    }

    public static String createGetCapabilitiesRequestURL(SirServiceReference reference) {
        return reference.getService().getUrl() + "?REQUEST=GetCapabilities&SERVICE=SOS";
    }

    /**
     * handle only & characters
     * 
     * @param url
     * @return
     */
    public static String decode(String url) {
        String s = url.replaceAll("&amp;", "\\&");
        return s;
    }

    /**
     * 
     * using the original URLEncoder does not work, it always appends the sir URL in front...
     * 
     * // getCapRequest = URLEncoder.encode(getCapRequest, //
     * SirConfigurator.getInstance().getCharacterEncoding());
     * 
     * @param getCapRequest
     * @return
     */
    public static String encode(String url) {
        String s = url.replaceAll("\\&", "&amp;");
        return s;
    }

    public static String extractDescriptionText(SirSimpleSensorDescription sensorDescription) {
        String ds = sensorDescription.getDescriptionText();

        // remove CDATA (if it exists)
        if (ds.contains(OpenSearchConstants.CDATA_START_TAG)) {
            ds = ds.replace(OpenSearchConstants.CDATA_START_TAG, "");

            if (ds.endsWith(OpenSearchConstants.CDATA_END_TAG))
                ds = ds.substring(0, ds.length() - 1);
        }

        // see if the string contains new line characters
        if (ds.contains("\n"))
            ds = ds.replaceAll("\\n", System.getProperty("line.separator"));

        // replace tabs
        if (ds.contains("\t"))
            ds = ds.replaceAll("\\t", " ");

        // encode possibly problematic characters
        if (ds.contains("&"))
            ds = encode(ds);

        ds = ds.trim();

        return ds;
    }

    public static String extractEntryTitle(SirSearchResultElement sirSearchResultElement) {
        StringBuilder sb = new StringBuilder();

        Collection<SirServiceReference> serviceReferences = sirSearchResultElement.getServiceReferences();
        for (SirServiceReference sirServiceReference : serviceReferences) {
            sb.append(sirServiceReference.getServiceSpecificSensorId());
            sb.append(" ");
        }

        return sb.toString();
    }

    /**
     * highlight all occurences of searchText using <b>-elements.
     * 
     * @param text
     * @param searchText
     * @param highlightSearchText
     * @param addLinksInSearchText
     * @return
     */
    public static String highlightHTML(String text,
                                       String searchText,
                                       boolean highlightSearchText,
                                       boolean addLinksInSearchText) {
        String s = text;

        StringBuffer regex = new StringBuffer();
        if (highlightSearchText) {
            String[] words = searchText.split(" ");
            for (String word : words) {
                // log.debug("Highlighting the word " + word);
                String head = "(?i)("; // case insensitive
                String tail = ")(?!([^<]+)?>>)";

                regex.delete(0, regex.length());
                regex.append(head);
                regex.append(word);
                regex.append(tail);

                s = s.replaceAll(regex.toString(), "<b>$1</b>");
            }
        }

        if (addLinksInSearchText) {
            // TODO remove all <b> tags that are within a URL

            // http://regexlib.com/Search.aspx?k=URL&AspxAutoDetectCookieSupport=1
            // String regex = "((mailto\\\\:|(news|(ht|f)tp(s?))\\\\://){1}\\\\S+)";

            // http://stackoverflow.com/questions/1909534/java-replacing-text-url-with-clickable-html-link
            s = s.replaceAll("(.*://[^<>[:space:]]+[[:alnum:]/])", "<a href=\"$1\">$1</a>");
        }

        return s;
    }

}

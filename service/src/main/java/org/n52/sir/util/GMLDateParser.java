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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.n52.sir.SirConfigurator;

/**
 * @author Jan Schulte
 * 
 */
public class GMLDateParser {

    private static final GMLDateParser instance = new GMLDateParser();

    /**
     * 
     * @return
     */
    public static GMLDateParser getInstance() {
        return instance;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SirConfigurator.getInstance().getGmlDateFormat());

    /**
     * private constructor for singleton pattern
     */
    private GMLDateParser() {
        //
    }

    /**
     * Parses a Calendar object to a string.
     * 
     * @param timestamp
     * @return
     */
    public String parseDate(Calendar timestamp) {
        return this.simpleDateFormat.format(timestamp.getTime());
    }

    /**
     * Parses a string into a Calendar object.
     * 
     * @param time
     *        String to be parsed
     * @return the Calendar Object
     * @throws ParseException
     */
    public Calendar parseString(String time) throws ParseException {
        Calendar cal = Calendar.getInstance();
        Date date = null;
        date = this.simpleDateFormat.parse(time);
        cal.setTime(date);
        return cal;
    }
}

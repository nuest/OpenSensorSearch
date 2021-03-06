/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

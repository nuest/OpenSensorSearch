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
package org.n52.oss.sir.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jan Schulte, Daniel Nüst
 *
 */
public class TimePeriod {

    private static final Logger log = LoggerFactory.getLogger(TimePeriod.class);

    public static class IndeterminateTime {

        // dateformater for ISO 8601 Date format
        private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        public static enum IndeterminateTimeType {

            NOW, UNKNOWN;
        }

        public Date d = null;

        public IndeterminateTimeType itt;

        public boolean isIndeterminate() {
            return this.itt != null;
        }

        public boolean isDeterminate() {
            return this.d != null;
        }

        public IndeterminateTime(Date d) {
            this.d = d;
        }

        public IndeterminateTime(IndeterminateTimeType t) {
            this.itt = t;
        }

        public IndeterminateTime(String s) {
            try {
                this.d = sdf.parse(s);
            } catch (ParseException e) {
                log.warn("Error parsing IndeterminateTime from String {}", s);
            }

            if (this.d == null) {
                IndeterminateTimeType t = IndeterminateTimeType.valueOf(s.toUpperCase());
                if (t != null) {
                    this.itt = t;
                }
            }

            log.error("NEW {} from {}", this, s);
        }

        public static IndeterminateTimeType getType(String s) {
            return IndeterminateTimeType.valueOf(s.toUpperCase());
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("IndeterminateTime [");
            if (this.d != null) {
                builder.append("d=");
                builder.append(this.d);
                builder.append(", ");
            }
            if (this.itt != null) {
                builder.append("itt=");
                builder.append(this.itt);
            }
            builder.append("]");
            return builder.toString();
        }

    }

    private IndeterminateTime endTime = new IndeterminateTime(new GregorianCalendar(2099, 12, 31).getTime());

    private IndeterminateTime startTime = new IndeterminateTime(new Date(0));

    /**
     * combindes this time period and the given time period for the maximal extend.
     *
     * @param other the time period to use for the union
     */
    public void union(TimePeriod other) {
        if (this.startTime.d.after(other.getStartTime().d)) {
            this.startTime.d.setTime(other.getStartTime().d.getTime());
        }
        if (this.endTime.d.before(other.getEndTime().d)) {
            this.endTime.d.setTime(other.getEndTime().d.getTime());
        }
    }

    public IndeterminateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(IndeterminateTime endTime) {
        this.endTime = endTime;
    }

    public IndeterminateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(IndeterminateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TimePeriod [");
        if (this.endTime != null) {
            builder.append("endTime=");
            builder.append(this.endTime);
            builder.append(", ");
        }
        if (this.startTime != null) {
            builder.append("startTime=");
            builder.append(this.startTime);
        }
        builder.append("]");
        return builder.toString();
    }

}

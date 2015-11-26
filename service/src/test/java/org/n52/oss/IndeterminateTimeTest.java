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
package org.n52.oss;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.n52.oss.sir.api.TimePeriod;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime.IndeterminateTimeType;
import org.n52.sir.ds.pgsql.SqlTools;

public class IndeterminateTimeTest {

    private TimePeriod foreverTimePeriod;
    private TimePeriod testTimePeriod;
    private Date testStart;
    private Date testEnd;

    @Before
    public void prepare() {
        this.foreverTimePeriod = new TimePeriod();
        this.foreverTimePeriod.setStartTime(new IndeterminateTime(IndeterminateTimeType.UNKNOWN));
        this.foreverTimePeriod.setEndTime(new IndeterminateTime(IndeterminateTimeType.UNKNOWN));

        this.testTimePeriod = new TimePeriod();
        this.testStart = new Date(0);
        this.testTimePeriod.setStartTime(new IndeterminateTime(this.testStart));
        this.testEnd = new Date(42);
        this.testTimePeriod.setEndTime(new IndeterminateTime(this.testEnd));
    }

    @Test
    public void normalTimePeriodContaintsCorrectDates() {
        assertThat("start date is correct", this.testTimePeriod.getStartTime().d, is(equalTo(this.testStart)));
        assertThat("start is determinate", this.testTimePeriod.getStartTime().isDeterminate(), is(equalTo(true)));

        assertThat("end date is correct", this.testTimePeriod.getEndTime().d, is(equalTo(this.testEnd)));
        assertThat("end is determinate", this.testTimePeriod.getEndTime().isDeterminate(), is(equalTo(true)));
    }

    @Test
    public void unknownStartTimeIsHandled() {
        assertThat("start date is correct", this.foreverTimePeriod.getStartTime().d, is(equalTo(isNull())));
        assertThat("start date is correct",
                   this.foreverTimePeriod.getStartTime().itt,
                   is(equalTo(IndeterminateTimeType.UNKNOWN)));
        assertThat("start is indeterminate", this.foreverTimePeriod.getStartTime().isIndeterminate(), is(true));
    }

    @Test
    public void unknownEndTimeIsHandled() {
        assertThat("end date is correct", this.foreverTimePeriod.getEndTime().d, is(equalTo(isNull())));
        assertThat("end date is correct",
                   this.foreverTimePeriod.getEndTime().itt,
                   is(equalTo(IndeterminateTimeType.UNKNOWN)));
        assertThat("end is indeterminate", this.foreverTimePeriod.getEndTime().isIndeterminate(), is(true));
    }

    @Test
    public void correctSqlStartTime() {
        String s = SqlTools.getStartDate(this.foreverTimePeriod);
        assertThat("unknown start date is '-infinity'", s, is(equalTo("-infinity")));
    }

    @Test
    public void correctSqlEndTime() {
        String s = SqlTools.getEndDate(this.foreverTimePeriod);
        assertThat("unknown end date is 'infinity'", s, is(equalTo("infinity")));
    }

}

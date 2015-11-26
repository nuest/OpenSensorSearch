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
package org.n52.oss.sir.harvest;

import java.util.Date;

import org.junit.Test;
import org.n52.sir.scheduler.HarvestJob;
import org.n52.sir.scheduler.QuartzConstants;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class ScheduleHarvest {

    @Test
    public void harvestAtTime() throws Exception {
        // TODO replace the value with a returned value
        SchedulerFactory factory = new StdSchedulerFactory();
        JobDetail detail = JobBuilder.newJob(HarvestJob.class).withIdentity("_J59").usingJobData(QuartzConstants.SENSOR_ID_HARVEST_JOB_DATA,
                                                                                                 "59").build();

        try {
            Trigger tr = TriggerBuilder.newTrigger().withIdentity("_T59").withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).startAt(new Date()).build();
            Scheduler sch = factory.getScheduler();
            sch.scheduleJob(detail, tr);
            sch.start();
            Thread.sleep(10000);
            // TODO check that the sensors are added here
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}

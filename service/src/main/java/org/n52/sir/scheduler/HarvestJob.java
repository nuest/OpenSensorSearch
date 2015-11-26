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
package org.n52.sir.scheduler;

import java.io.File;
import java.util.Date;

import org.n52.sir.SirConfigurator;
import org.n52.sir.harvest.exec.IJSExecute;
import org.n52.sir.harvest.exec.impl.RhinoJSExecute;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvestJob implements Job {
    
	private static Logger log = LoggerFactory.getLogger(HarvestJob.class);
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		JobDetail details = arg0.getJobDetail();
		//get sensor id from here
		String sensorId = details.getJobDataMap().getString(QuartzConstants.SENSOR_ID_HARVEST_JOB_DATA);
		System.out.println("Executed");
		log.info("Executed at : "+new Date().getTime());
		SirConfigurator config = SirConfigurator.getInstance();
		String path = config.getFactory().insertHarvestScriptDAO().getScriptPath(sensorId);
		if(path !=null){
			File f = new File(config.getScriptsPath()+path);
			IJSExecute executeEngine = new RhinoJSExecute();
			executeEngine.execute(f);
		}
		log.info("Harvesting sensor:"+path);
		try {
			arg0.getScheduler().unscheduleJob(arg0.getTrigger().getKey());
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			log.error("Cannot unscedule ",e);
		}
		
	}
	
}

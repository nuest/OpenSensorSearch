/**
 * ﻿Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.sir.util.jobs;

import org.n52.sir.util.jobs.impl.JobSchedulerFactoryImpl;

import com.google.inject.ImplementedBy;

/**
 * 
 * Factory class for encapsulation of {@link IJobScheduler} instances.
 * 
 * @author Daniel Nüst (daniel.neust@uni-muenster.de)
 * 
 */
@ImplementedBy(JobSchedulerFactoryImpl.class)
public interface IJobSchedulerFactory {

    /**
     * 
     * @return An instance of an {@link IJobScheduler} to schedule new or cancel existing tasks.
     */
    public abstract IJobScheduler getJobScheduler();

}

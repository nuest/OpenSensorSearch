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
package org.n52.sir.ds;

import java.util.Collection;

import org.n52.oss.sir.api.SirSensorIdentification;
import org.n52.oss.sir.api.SirStatus;
import org.n52.oss.sir.ows.OwsExceptionReport;

/**
 * interface for the specific DAOFactories, offers methods to create the matching DAOs for the
 * insertSensorStatus operation
 * 
 * @author Jan Schulte
 * 
 */
public interface IInsertSensorStatusDAO {

    /**
     * insert a new sensor status in the database
     * 
     * @param ident
     *        Identification of the sensor
     * @param status
     *        the status
     * @return Returns the sensorID in the SIR
     * @throws OwsExceptionReport
     */
    public String insertSensorStatus(SirSensorIdentification ident, Collection<SirStatus> status) throws OwsExceptionReport;

}

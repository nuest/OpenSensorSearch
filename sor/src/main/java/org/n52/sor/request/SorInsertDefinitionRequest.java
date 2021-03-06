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
package org.n52.sor.request;

import net.opengis.swe.x101.PhenomenonType;

import org.n52.sor.ISorRequest;


/**
 * @author Jan Schulte
 * 
 */
public class SorInsertDefinitionRequest implements ISorRequest {

    private String definitionURI;

    private PhenomenonType phenomenon;

    /**
     * @return the definitionURI
     */
    public String getDefinitionURI() {
        return this.definitionURI;
    }

    /**
     * @return the phenomenon
     */
    public PhenomenonType getPhenomenon() {
        return this.phenomenon;
    }

    /**
     * @param definitionURI
     *        the definitionURI to set
     */
    public void setDefinitionURI(String definitionURI) {
        this.definitionURI = definitionURI;
    }

    /**
     * @param phenomenon
     *        the phenomenon to set
     */
    public void setPhenomenon(PhenomenonType phenomenon) {
        this.phenomenon = phenomenon;
    }

    @Override
    public String toString() {
        return "InsertDefinition request: DefinitionURI: " + this.definitionURI + " - Phenomenon: "
                + this.phenomenon.xmlText();
    }

}
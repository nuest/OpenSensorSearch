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
package org.n52.sir.xml;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sensorML.x101.SystemType;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.api.SirSensorDescription;

import x0.oasisNamesTcEbxmlRegrepXsdRim3.RegistryPackageDocument;

/**
 * 
 * An object that implements this interface can be used for transformation of one XML document to another. The
 * transformation can include validation.
 * 
 * The transformation can be based on different kinds of inputs and outputs: Apart from the method relying on
 * java.xml.transform classes there is a convenience method for XMLBeans using {@link XmlObject}.
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
public interface ITransformer {

    public static enum TransformableFormat {
        SML, EBRIM;
    }

    public static final boolean IS_VALIDATING_DEFAULT = true;

    public static final RegistryPackageDocument PROCESSING_ERROR_OBJECT = RegistryPackageDocument.Factory.newInstance();

    public static final RegistryPackageDocument TRANSFORMATION_ERROR_OBJECT = RegistryPackageDocument.Factory.newInstance();

    public abstract boolean isValidatingInputAndOutput();

    public abstract void setValidatingInputAndOutput(boolean b);

    public abstract XmlObject transform(SensorMLDocument smlDoc) throws XmlException, TransformerException, IOException;

    public abstract XmlObject transform(SirSensorDescription sensor) throws XmlException,
            TransformerException,
            IOException;

    public abstract Result transform(Source input) throws TransformerException, FileNotFoundException;

    public abstract Result transform(String file) throws FileNotFoundException, TransformerException;

    public abstract XmlObject transform(SystemType copy) throws XmlException, TransformerException, IOException;

    public abstract boolean acceptsInput(TransformableFormat input);

    public abstract boolean producesOutput(TransformableFormat output);

}
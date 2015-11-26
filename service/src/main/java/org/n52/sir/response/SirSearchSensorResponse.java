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
package org.n52.sir.response;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import net.opengis.ows.x11.BoundingBoxType;
import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.n52.oss.sir.api.SirBoundingBox;
import org.n52.oss.sir.api.SirDetailedSensorDescription;
import org.n52.oss.sir.api.SirSearchResultElement;
import org.n52.oss.sir.api.SirSensorDescription;
import org.n52.oss.sir.api.SirServiceReference;
import org.n52.oss.sir.api.SirSimpleSensorDescription;
import org.n52.oss.sir.api.SirXmlSensorDescription;
import org.n52.oss.util.XmlTools;
import org.n52.sir.SirConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.SearchSensorResponseDocument;
import org.x52North.sir.x032.SearchSensorResponseDocument.SearchSensorResponse;
import org.x52North.sir.x032.SearchSensorResponseDocument.SearchSensorResponse.SearchResultElement;
import org.x52North.sir.x032.ServiceReferenceDocument.ServiceReference;
import org.x52North.sir.x032.SimpleSensorDescriptionDocument.SimpleSensorDescription;

/**
 * @author Jan Schulte
 *
 */
public class SirSearchSensorResponse extends AbstractXmlResponse {

	private static final Logger log = LoggerFactory
			.getLogger(SirSearchSensorResponse.class);

	/**
	 * the search result elements
	 */
	private Collection<SirSearchResultElement> searchResultElements;

	@Override
	public SearchSensorResponseDocument createXml() {
		SearchSensorResponseDocument document = SearchSensorResponseDocument.Factory
				.newInstance();
		SearchSensorResponse searchSensResp = document
				.addNewSearchSensorResponse();
		boolean isSimple = false;
		log.info("Results for XML conversion:" + searchResultElements.size());
		for (SirSearchResultElement searchResult : this.searchResultElements) {
			SearchResultElement elem = searchSensResp
					.addNewSearchResultElement();

			if (searchResult.getSensorId() != null
					|| searchResult.getSensorDescription() instanceof SirDetailedSensorDescription) {
				// sensorID in SIR
				if (searchResult.getSensorId() != null)
					elem.setSensorIDInSIR(searchResult.getSensorId());

				if (searchResult.getSensorDescription() instanceof SirDetailedSensorDescription) {
					log.info("A result from Solr");
					elem.setSensorIDInSIR(((SirDetailedSensorDescription) searchResult
							.getSensorDescription()).getId());
				}

				SirSensorDescription description = searchResult
						.getSensorDescription();

				// sensorDescription
				if (description != null) {
					if (description instanceof SirXmlSensorDescription) {
						SirXmlSensorDescription xmlDescr = (SirXmlSensorDescription) description;

						XmlObject sml = null;
						try {
							sml = XmlObject.Factory.parse(xmlDescr
									.getDescription().getDomNode());
						} catch (XmlException e) {
							log.error(
									"Could not parse XML sensor description stored in database for sensor "
											+ searchResult.getSensorId()
											+ ".", e);
						}

						// handle different cases of data in database... after
						// fixing the insertion, onle
						// SensorMLDocument should be saved from now on.
						if (sml instanceof SensorMLDocument) {
							SensorMLDocument smlDoc = (SensorMLDocument) sml;

							if (smlDoc.getSensorML().getMemberArray().length > 1)
								log.warn("More than one member in SensorML, but only using first one!");

							elem.setSensorDescription(smlDoc.getSensorML()
									.getMemberArray(0).getProcess());
						} else if (sml instanceof AbstractProcessType) {
							AbstractProcessType process = (AbstractProcessType) sml;
							elem.setSensorDescription(process);
						} else if (sml instanceof XmlAnyTypeImpl) {
							XmlAnyTypeImpl anyType = (XmlAnyTypeImpl) sml;
							log.warn("Could not detect type of XML, trying to parse to AbstractProcessType");
							try {
								AbstractProcessType process = AbstractProcessType.Factory
										.parse(anyType.getDomNode());
								elem.setSensorDescription(process);
							} catch (XmlException e) {
								log.error(
										"Could not parse XML sensor description stored in database for sensor "
												+ searchResult
														.getSensorId()
												+ ".", e);
							}
						}
					} else if (description instanceof SirSimpleSensorDescription) {
						isSimple = true;
						SirSimpleSensorDescription simpleDescr = (SirSimpleSensorDescription) description;
						SimpleSensorDescription newSSDescr = elem
								.addNewSimpleSensorDescription();
						newSSDescr.setDescriptionText(simpleDescr
								.getDescriptionText());
						newSSDescr.setSensorDescriptionURL(simpleDescr
								.getSensorDescriptionURL());

						SirBoundingBox boundingBox = simpleDescr
								.getBoundingBox();
						if (boundingBox != null) {
							BoundingBoxType bbox = newSSDescr
									.addNewObservedBoundingBox();
							bbox.setCrs(Integer.toString(boundingBox.getSrid()));
							bbox.setDimensions(BigInteger.valueOf(boundingBox
									.getDimension()));
							List<String> ll = boundingBox.getLowerCorner();
							bbox.setLowerCorner(ll);
							List<String> uu = boundingBox.getUpperCorner();
							bbox.setUpperCorner(uu);
						} else
							log.debug("No bounding box given, possibly not supported in service version.");
					} else if (description instanceof SirDetailedSensorDescription) {
						isSimple = true;
						SirDetailedSensorDescription solr_description = (SirDetailedSensorDescription) description;
						SimpleSensorDescription newSSDescr = elem
								.addNewSimpleSensorDescription();
						log.info("Solr_Description:"
								+ solr_description.getDescription());

						if (solr_description.getDescription() != null)
							newSSDescr.setDescriptionText(solr_description
									.getDescription());
						if (solr_description.getId() != null)
							newSSDescr.setSensorDescriptionURL(solr_description
									.getId());

						log.info(newSSDescr.toString());

					} else {
						log.error("Unsupported SirSensorDescription!\n"
								+ description);
					}
				}

				// ServiceReference
				if (searchResult.getServiceReferences() != null) {
					for (SirServiceReference servDesc : searchResult
							.getServiceReferences()) {
						ServiceReference temp = elem.addNewServiceReference();
						temp.setServiceSpecificSensorID(servDesc
								.getServiceSpecificSensorId());
						temp.setServiceType(servDesc.getService().getType());
						temp.setServiceURL(servDesc.getService().getUrl());
					}
				}

			}
		}

		if (isSimple)
			XmlTools.addSirSchemaLocation(searchSensResp);
		else
			XmlTools.addSirAndSensorMLSchemaLocation(searchSensResp);

		if (SirConfigurator.getInstance().isValidateResponses()) {
			if (!document.validate())
				log.warn("Service created invalid document!\n"
						+ XmlTools.validateAndIterateErrors(document));
		}
		log.info("ResultDoc" + document.toString());
		return document;
	}

	/**
	 * @return the searchResultElements
	 */
	public Collection<SirSearchResultElement> getSearchResultElements() {
		return this.searchResultElements;
	}

	/**
	 * @param searchResultElements
	 *            the searchResultElements to set
	 */
	public void setSearchResultElements(
			Collection<SirSearchResultElement> searchResultElements) {
		this.searchResultElements = searchResultElements;
	}

}

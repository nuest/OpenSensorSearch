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
package org.n52.oss.testdata.sml;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.opengis.ows.ExceptionReportDocument;
import net.opengis.sensorML.x101.AbstractProcessType;
import net.opengis.sensorML.x101.SensorMLDocument.SensorML.Member;
import net.opengis.sensorML.x101.SystemType;

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.SMLConstants;
import org.n52.oss.sir.SirConstants;
import org.n52.oss.util.XmlTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument.InsertSensorInfoRequest;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument.InsertSensorInfoRequest.InfoToBeInserted;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument.InsertSensorInfoResponse;
import org.x52North.sir.x032.ServiceReferenceDocument.ServiceReference;
import org.x52North.sir.x032.VersionAttribute;
import org.x52North.sir.x032.VersionAttribute.Version.Enum;

/**
 * 
 * @author Daniel Nüst (d.nuest@52north.org)
 * 
 */
@SuppressWarnings("unused")
public class GeneratorClient {

    private static final String SIR_INSERT_SENSOR_INFO_SCHEMA_LOCATION = "http://52north.org/sir/0.3.2 http://52north.org/schema/sir/0.3.2/sirInsertSensorInfo.xsd";

    private static final String STRING_REQUEST_CHARACTER_ENCODING = "UTF-8";

    private static final String STRING_REQUEST_ENCODING = "text/xml";

    private static final String SYSTEM_PROPERTY_PROXY_HOST = "http.proxyHost";

    private static final String SYSTEM_PROPERTY_PROXY_PORT = "http.proxyPort";

    private static final long SLEEP_BETWEEN_REQUESTS = 1000;

    private static final Enum SERVICE_VERSION = VersionAttribute.Version.X_0_3_1;

    private static Logger log = LoggerFactory.getLogger(GeneratorClient.class);

    private static DefaultHttpClient httpClient = new DefaultHttpClient();

    private static String genesisUrl = "http://giv-genesis.uni-muenster.de:8080/SIR/sir";

    private static String localhostUrl = "http://localhost:8080/SIR/sir";

    /* actual varying parameters: */
    private static String sirURL = localhostUrl;
    private static boolean sending = true;
    private static boolean printSensorMLDocuments = false;
    private static int nSensorsInOneInsertRequest = 3;
    private static int nSensorsToGenerate = 42;

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (sending)
            log.info("Starting insertion of test sensors into " + sirURL);
        else
            log.warn("Starting generation of test sensors, NOT sending!");

        TestSensorFactory factory = new TestSensorFactory();

        // create sensors
        List<TestSensor> sensors = new ArrayList<TestSensor>();

        for (int i = 0; i < nSensorsToGenerate; i++) {
            TestSensor s = factory.createRandomTestSensor();
            sensors.add(s);
            log.info("Added new random sensor: " + s);
        }

        ArrayList<String> insertedSirIds = new ArrayList<String>();

        // insert sensors to service
        int startOfSubList = 0;
        int endOfSubList = nSensorsInOneInsertRequest;
        while (endOfSubList <= sensors.size() + nSensorsInOneInsertRequest) {
            List<TestSensor> currentSensors = sensors.subList(startOfSubList, Math.min(endOfSubList, sensors.size()));

            if (currentSensors.isEmpty())
                break;

            try {
                String[] insertedSirIDs = insertSensorsInSIR(currentSensors);
                if (insertedSirIDs == null) {
                    log.error("Did not insert dummy sensors.");
                }
                else {
                    insertedSirIds.addAll(Arrays.asList(insertedSirIDs));
                }
            }
            catch (HttpException e) {
                log.error("Error inserting sensors.", e);
            }
            catch (IOException e) {
                log.error("Error inserting sensors.", e);
            }

            startOfSubList = Math.min(endOfSubList, sensors.size());
            endOfSubList = endOfSubList + nSensorsInOneInsertRequest;

            if (sending) {
                try {
                    log.debug("Sleeping for {} msecs.", SLEEP_BETWEEN_REQUESTS);
                    Thread.sleep(SLEEP_BETWEEN_REQUESTS);
                }
                catch (InterruptedException e) {
                    log.error("Interrupted!", e);
                }
            }
        }

        log.info("Added sensors (ids in sir): " + Arrays.toString(insertedSirIds.toArray()));
    }

    /**
     * 
     * @param testSensor
     * @throws HttpException
     * @throws IOException
     */
    private static String[] insertSensorsInSIR(Collection<TestSensor> sensors) throws HttpException, IOException {
        ArrayList<String> insertingIds = new ArrayList<String>();

        /* CREATE INSERT DOCUMENT */
        InsertSensorInfoRequestDocument requestDoc = InsertSensorInfoRequestDocument.Factory.newInstance();
        InsertSensorInfoRequest request = requestDoc.addNewInsertSensorInfoRequest();
        request.setService(SirConstants.SERVICE_NAME);
        request.setVersion(SERVICE_VERSION);

        XmlCursor requestCursor = request.newCursor();
        requestCursor.setAttributeText(XmlTools.SCHEMA_LOCATION_ATTRIBUTE_QNAME, SIR_INSERT_SENSOR_INFO_SCHEMA_LOCATION
                + " " + SMLConstants.SCHEMA_LOCATION);

        /* sensorInfo */
        for (TestSensor currentSensor : sensors) {
            log.info("Inserting test sensor: " + currentSensor);
            insertingIds.add(currentSensor.getUniqueId());

            InfoToBeInserted infoToBeInserted = request.addNewInfoToBeInserted();
            AbstractProcessType sensorDescription = infoToBeInserted.addNewSensorDescription();
            SystemType system = (SystemType) sensorDescription.changeType(SystemType.type);
            Member member = currentSensor.getSensorMLDocument().getSensorML().getMemberArray(0);
            system.set(member.getProcess());

            if (printSensorMLDocuments) {
                log.debug("\n {} \n",
                          currentSensor.getSensorMLDocument().xmlText(XmlTools.unconfiguredXmlOptionsForNamespaces()));
            }

            /* ServiceReferences */
            for (Map<String, String> currentInterface : currentSensor.getInterfaces()) {
                ServiceReference newReferenceInfos = infoToBeInserted.addNewServiceReference();
                newReferenceInfos.setServiceSpecificSensorID(currentInterface.get(SensorMLEncoder.MAP_KEY_SERVICE_SPECIFIC_SENSOR_ID));
                newReferenceInfos.setServiceType(currentInterface.get(SensorMLEncoder.MAP_KEY_SERVICE_TYPE));
                newReferenceInfos.setServiceURL(currentInterface.get(SensorMLEncoder.MAP_KEY_SERVICE_URL));
            }
        }

        /* SEND REQUEST */
        if (sending) {
            // log.debug("Sending:\n" + requestDoc.xmlText(XmlTools.unconfiguredXmlOptionsForNamespaces()));
            log.debug("Sending sensors {}", Arrays.toString(insertingIds.toArray()));

            if ( !requestDoc.validate()) {
                String errors = XmlTools.validateAndIterateErrors(requestDoc);
                log.debug(errors);
                return errors.split("\n");
            }

            XmlObject response = sendRequest(requestDoc);

            if (response instanceof ExceptionReportDocument) {
                ExceptionReportDocument er = (ExceptionReportDocument) response;
                throw new RuntimeException("Insertion returned error: "
                        + er.xmlText(XmlTools.unconfiguredXmlOptionsForNamespaces()));
            }

            if (response instanceof InsertSensorInfoResponseDocument) {
                InsertSensorInfoResponseDocument insertResponseDoc = (InsertSensorInfoResponseDocument) response;
                InsertSensorInfoResponse insertResponse = insertResponseDoc.getInsertSensorInfoResponse();

                log.info("InsertSensorInfoResponse: inserted sensors = " + insertResponse.getNumberOfInsertedSensors());

                if (insertResponse.getNumberOfInsertedSensors() > 0) {
                    return insertResponse.getInsertedSensors().getSensorIDInSIRArray();
                }
            }
            else
                log.debug("Response for insertion:\n {}", response.xmlText());

            return null;
        }

        log.debug("NOT sending: {}", requestDoc.xmlText(XmlTools.unconfiguredXmlOptionsForNamespaces()));
        log.debug(XmlTools.validateAndIterateErrors(requestDoc));
        return null;
    }

    /**
     * @param requestDoc
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws HttpException
     */
    private static XmlObject sendRequest(InsertSensorInfoRequestDocument requestDoc) throws UnsupportedEncodingException,
            IOException,
            HttpException {
        HttpPost request = new HttpPost(sirURL);
        request.setEntity(new StringEntity(requestDoc.xmlText(XmlTools.unconfiguredXmlOptionsForNamespaces()),
                                           STRING_REQUEST_ENCODING,
                                           STRING_REQUEST_CHARACTER_ENCODING));

        // String host = System.getProperty(SYSTEM_PROPERTY_PROXY_HOST);
        // String port = System.getProperty(SYSTEM_PROPERTY_PROXY_PORT);
        // if (host != null && host.length() > 0 && port != null && port.length() > 0) {
        // int portNumber = Integer.parseInt(port);
        // HostConfiguration hostConfig = new HostConfiguration();
        // hostConfig.setProxy(host, portNumber);
        // httpClient.setHostConfiguration(hostConfig);
        // }

        httpClient.execute(request);

        XmlObject response = null;
        try {
            response = XmlObject.Factory.parse(request.getEntity().getContent());
        }
        catch (XmlException e) {
            log.error("Error parsing response.", e);
            return null;
        }
        return response;
    }

}

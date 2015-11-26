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
package org.n52.sir.data;

/**
 * @author Yakoub
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.opengis.sensorML.x101.KeywordsDocument.Keywords.KeywordList;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.http.HttpException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.n52.oss.sir.Client;
import org.n52.oss.sir.api.SirSensor;
import org.n52.oss.sir.api.TimePeriod;
import org.n52.oss.sir.api.TimePeriod.IndeterminateTime;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.x52North.sir.x032.InsertSensorInfoRequestDocument;
import org.x52North.sir.x032.InsertSensorInfoResponseDocument;

import com.google.gson.Gson;
import com.google.inject.Inject;
import org.n52.sir.IT.Util;

public class DummySensorGenerator {

    @Inject
    private Client client;

    @BeforeClass
    public static void setUp() {
        Util.configureSirClient();
    }

    @Test
    public void parseJsonSensorsAndInsert() throws IOException, OwsExceptionReport, XmlException, HttpException {

        File sensor_file = new File(ClassLoader.getSystemResource("data/randomSensors.json").getFile());
        File sensor_temp = new File(ClassLoader.getSystemResource("AirBase-test.xml").getFile());
        SensorMLDocument DOC = SensorMLDocument.Factory.parse(sensor_temp);
        Gson gson = new Gson();
        StringBuilder builder = new StringBuilder();
        String s;
        try (BufferedReader reader = new BufferedReader( (new FileReader(sensor_file)));) {
            while ( (s = reader.readLine()) != null)
                builder.append(s);
        }

        JSONSensorsCollection collection = gson.fromJson(builder.toString(), JSONSensorsCollection.class);
        Iterator<JSONSensor> sensors = collection.sensors.iterator();
        while (sensors.hasNext()) {
            SirSensor sensor = new SirSensor();
            JSONSensor jsensor = sensors.next();
            sensor.setKeywords(jsensor.keywords);
            TimePeriod period = new TimePeriod();
            DateTime begin = DateTime.parse(jsensor.beginPosition);
            DateTime end = DateTime.parse(jsensor.endPosition);
            // fix because we need start < end , and the data is randomly
            // generated
            if (begin.getMillis() > end.getMillis()) {
                DateTime temp = begin;
                end = begin;
                begin = temp;
            }
            period.setStartTime(new IndeterminateTime(begin.toDate()));
            period.setEndTime(new IndeterminateTime(end.toDate()));
            sensor.setTimePeriod(period);
            sensor.setIdentificationsList(jsensor.Identifiers);
            List<String> contacts = new ArrayList<>();
            contacts.add(jsensor.contacts);
            sensor.setContacts(contacts);

            /*
             * Add to SIRPQSQL
             */
            KeywordList klist = KeywordList.Factory.newInstance();
            klist.setKeywordArray(jsensor.keywords.toArray(new String[] {}));
            DOC.getSensorML().getMemberArray(0).getProcess().getKeywordsArray(0).setKeywordList(klist);
            InsertSensorInfoRequestDocument req = InsertSensorInfoRequestDocument.Factory.newInstance();
            req.addNewInsertSensorInfoRequest().addNewInfoToBeInserted().setSensorDescription(DOC.getSensorML().getMemberArray(0).getProcess());

            XmlObject res = this.client.xSendPostRequest(req);

            InsertSensorInfoResponseDocument resp = InsertSensorInfoResponseDocument.Factory.parse(res.getDomNode());
            if (resp.getInsertSensorInfoResponse().getNumberOfInsertedSensors() < 1)
                System.out.println("Failed to insert sensor");

            /*
             * Insert into apache solr
             */
            // SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
            // dao.insertSensor(sensor);
        }

    }

    // public void parseJsonSensorsAndInsert() throws IOException, OwsExceptionReport {
    //
    // File sensor_file = new File(ClassLoader.getSystemResource("data/randomSensors.json").getFile());
    // Gson gson = new Gson();
    // StringBuilder builder = new StringBuilder();
    // String s;
    // BufferedReader reader = new BufferedReader( (new FileReader(sensor_file)));
    // while ( (s = reader.readLine()) != null)
    // builder.append(s);
    // JSONSensorsCollection collection = gson.fromJson(builder.toString(), JSONSensorsCollection.class);
    // Iterator<JSONSensor> sensors = collection.sensors.iterator();
    // while (sensors.hasNext()) {
    // SirSensor sensor = new SirSensor();
    // JSONSensor jsensor = sensors.next();
    // sensor.setKeywords(jsensor.keywords);
    // SirTimePeriod period = new SirTimePeriod();
    // DateTime begin = DateTime.parse(jsensor.beginPosition);
    // DateTime end = DateTime.parse(jsensor.endPosition);
    // // fix because we need start < end , and the data is randomly generated
    // if (begin.getMillis() > end.getMillis()) {
    // DateTime temp = begin;
    // end = begin;
    // begin = temp;
    // }
    // period.setStartTime(begin.toDate());
    // period.setEndTime(end.toDate());
    // sensor.setTimePeriod(period);
    // sensor.setIdentificationsList(jsensor.Identifiers);
    // List<String> contacts = new ArrayList<String>();
    // contacts.add(jsensor.contacts);
    // sensor.setContacts(contacts);
    // SOLRInsertSensorInfoDAO dao = new SOLRInsertSensorInfoDAO();
    // dao.insertSensor(sensor);
    // }
    //
    // }

}

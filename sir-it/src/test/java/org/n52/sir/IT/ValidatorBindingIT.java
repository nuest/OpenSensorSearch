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
/** @author Yakoub
 */
package org.n52.sir.IT;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;

import com.google.gson.Gson;


public class ValidatorBindingIT {
	
	@Test
	public void readValidSensorMLAndValidate() throws IOException{
		File f = new File(ClassLoader.getSystemResource("AirBase-test.xml").getFile());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String s = null;
		StringBuilder builder = new StringBuilder();
		while((s=reader.readLine())!=null)
			builder.append(s);
		String sensorML = builder.toString();
		HttpPost post = new HttpPost("http://localhost:8080/OpenSensorSearch/api/v1/check/sensorML");
        List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("sensor", sensorML));
		pairs.add(new BasicNameValuePair("format","json"));
		post.setEntity(new UrlEncodedFormEntity(pairs));
		
		HttpClient client = new DefaultHttpClient();
		HttpResponse resp = client.execute(post);
		StringBuilder result = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		while((s=reader.readLine())!=null)
			result.append(s);
		StatusResponse sr = new Gson().fromJson(result.toString(), StatusResponse.class);
		
		assertEquals("valid",sr.status);
	}
	@Test
	public void readInValidSensorMLAndValidate() throws IOException{
		File f = new File(ClassLoader.getSystemResource("Requests/testSensor.xml").getFile());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String s = null;
		StringBuilder builder = new StringBuilder();
		while((s=reader.readLine())!=null)
			builder.append(s);
		String sensorML = builder.toString();
		HttpPost post = new HttpPost("http://localhost:8080/OpenSensorSearch/api/v1/check/sensorML");
        List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("sensor", sensorML));
		pairs.add(new BasicNameValuePair("format","json"));
		post.setEntity(new UrlEncodedFormEntity(pairs));
		
		HttpClient client = new DefaultHttpClient();
		HttpResponse resp = client.execute(post);
		StringBuilder result = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		while((s=reader.readLine())!=null)
			result.append(s);
		System.out.println(result.toString());
		StatusResponse sr = new Gson().fromJson(result.toString(), StatusResponse.class);		
		assertEquals("invalid",sr.status);
	}
	
	public class StatusResponse{
		public String status;
		public String error;
	}

}

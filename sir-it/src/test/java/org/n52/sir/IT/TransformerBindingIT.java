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
/** @author Yakoub
 */
package org.n52.sir.IT;

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


public class TransformerBindingIT {
	
	@Test
	public void readValidSensorMLAndValidate() throws IOException{
		File f = new File(ClassLoader.getSystemResource("AirBase-test.xml").getFile());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String s = null;
		StringBuilder builder = new StringBuilder();
		while((s=reader.readLine())!=null)
			builder.append(s);
		String sensorML = builder.toString();
		HttpPost post = new HttpPost("http://localhost:8080/OpenSensorSearch/convert/");
        List<NameValuePair> pairs = new ArrayList<>();
		pairs.add(new BasicNameValuePair("sensor", sensorML));
		pairs.add(new BasicNameValuePair("output","ebrim"));
		post.setEntity(new UrlEncodedFormEntity(pairs));
		
		HttpClient client = new DefaultHttpClient();
		HttpResponse resp = client.execute(post);
		StringBuilder result = new StringBuilder();
		reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		while((s=reader.readLine())!=null)
			result.append(s);
		//TODO yakoub : complete teh checking here
		
	}
	

}

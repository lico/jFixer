/*
 * Copyright 2018 UpAndCoding.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.upandcoding.fixer.endpoint;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.upandcoding.fixer.FixerException;
import com.upandcoding.fixer.endpoint.field.EndpointFieldList;
import com.upandcoding.tests.config.TestConfig;
import com.upandcoding.tests.config.TestUtils;

import junit.framework.Assert;

public class TestEndpointConvert {

	private static final Logger log = LoggerFactory.getLogger(TestEndpointConvert.class); 

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(7079);

	@Test
	public void testGetDataConvert() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "{"
				+ "\"success\": true,"
				+ "\"query\": {"
				+ "\"from\": \"GBP\","
				+ "\"to\": \"JPY\","
				+ "\"amount\": 25"
				+ "},"
				+ "\"info\": {"
				+ "\"timestamp\": 1519328414,"
				+ "\"rate\": 148.972231"
				+ "},"
				+ "\"historical\": \"\","
				+ "\"date\": \"2018-02-22\","
				+ "\"result\": 3724.305775"
				+ "}";

		String from = "GBP";
		String to = "JPY";
		double amount = 25.0;

		// Setup Wire Mock HTTP server
		String endpointUrl = "/convert?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency + "&from=" + from + "&to=" + to + "&amount="+amount;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		// With optional parameters
		Endpoint convertEndpoint = new ConvertEndpoint(TestConfig.baseUrl);
		convertEndpoint.addParam("access_key", TestConfig.accessKey);
		convertEndpoint.addParam("base", TestConfig.baseCurrency);
		convertEndpoint.addParam("from", from);
		convertEndpoint.addParam("to", to);
		convertEndpoint.addParam("amount", ""+amount);
		
		try {
			//ResultHeader data = convertEndpoint.getData(jsonStr);
			//ResultHeader data = convertEndpoint.getData();
			EndpointFieldList data = convertEndpoint.getData();
			TestUtils.displayResult(data, log);
			
			double result = data.getField("result").getDouble();
			Assert.assertEquals(3724.305775, result);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

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
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.upandcoding.fixer.FixerException;
import com.upandcoding.fixer.endpoint.field.EndpointFieldList;
import com.upandcoding.fixer.model.Currency;
import com.upandcoding.tests.config.TestConfig;
import com.upandcoding.tests.config.TestUtils;

import junit.framework.Assert;

public class TestEndpointSupportedSymbols {

	private static final Logger log = LoggerFactory.getLogger(TestEndpointSupportedSymbols.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(7079);

	
	@Test
	public void testGetDataSupportedSymbols() throws FixerException, ClientProtocolException, IOException {
		
		// Mock the JSON response
		String jsonStr = "{"
			  + "\"success\": true,"
			  + "\"symbols\": { "
			  + "\"AED\": \"United Arab Emirates Dirham\","
			  + "\"AFN\": \"Afghan Afghani\","
			  + "\"ALL\": \"Albanian Lek\","
			  + "\"AMD\": \"Armenian Dram\""
			  + "}"
			  + "}";
		
		// Setup Wire Mock HTTP server
		String endpointUrl = "/symbols?access_key=" + TestConfig.accessKey;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr); 
		
		// Prepare endpoint
		SupportedSymbolsEndpoint symbolsEndPoint = new SupportedSymbolsEndpoint(TestConfig.baseUrl);
		symbolsEndPoint.addParam("access_key", TestConfig.accessKey);
		
		try {
			//ResultHeader data = symbolsEndPoint.getData(jsonStr);
			//ResultHeader data = symbolsEndPoint.getData();
			EndpointFieldList data = symbolsEndPoint.getData();
			TestUtils.displayResult(data, log);
			List<Currency> expected = new ArrayList<>();
			expected.add(new Currency("AED","United Arab Emirates Dirham"));
			expected.add(new Currency("AFN","Afghan Afghani"));
			expected.add(new Currency("ALL","Albanian Lek"));
			expected.add(new Currency("AMD","Armenian Dram"));
			Assert.assertEquals(expected, data.getCurrencies());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

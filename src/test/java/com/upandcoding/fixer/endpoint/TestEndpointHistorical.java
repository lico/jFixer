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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
import com.upandcoding.fixer.model.ExchangeRate;
import com.upandcoding.tests.config.TestConfig;
import com.upandcoding.tests.config.TestUtils;

import junit.framework.Assert;

public class TestEndpointHistorical {

	private static final Logger log = LoggerFactory.getLogger(TestEndpointHistorical.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(7079);

	@Test
	public void testGetDataHistorical() throws FixerException, ClientProtocolException, IOException {

		String baseCurrency = "GBP";
		
		// Mock the JSON response
		String jsonStr = "{"
				+ "\"success\": true,"
				+ "\"historical\": true,"
				+ "\"date\": \"2013-12-24\","
				+ "\"timestamp\": 1387929599,"
				+ "\"base\": \"GBP\","
				+ "\"rates\": {"
				+ "    \"USD\": 1.636492,"
				+ "    \"EUR\": 1.196476,"
				+ "    \"CAD\": 1.739516"
				+ "}"
				+ "}";

		// Setup Wire Mock HTTP server
		String symbols="USD,EUR,CAD";
		String endpointUrl = "/2013-12-24?access_key=" + TestConfig.accessKey + "&symbols=" + symbols + "&base=" + baseCurrency;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		// With optional parameters
		Endpoint historical = new HistoricalEndpoint(TestConfig.baseUrl);
		historical.addParam("access_key", TestConfig.accessKey);
		historical.addParam("symbols", symbols);
		historical.addPathVariable("date", "2013-12-24");
		historical.addParam("base", baseCurrency);
		
		try {
			//ResultHeader data = historical.getData(jsonStr);
			//ResultHeader data = historical.getData();
			EndpointFieldList data = historical.getData();
			TestUtils.displayResult(data, log);
			
			List<ExchangeRate> rates = data.getRates();
			List<ExchangeRate> expected = new ArrayList<>();
			LocalDateTime ld = LocalDateTime.ofInstant(Instant.ofEpochSecond(1387929599), ZoneId.systemDefault());
			expected.add(new ExchangeRate(baseCurrency, "USD", 1.636492, "2013-12-24", ld));
			expected.add(new ExchangeRate(baseCurrency, "EUR", 1.196476, "2013-12-24", ld));
			expected.add(new ExchangeRate(baseCurrency, "CAD", 1.739516, "2013-12-24", ld));
			Assert.assertEquals(expected, rates);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

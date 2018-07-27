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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

public class TestEndpointLatest {

	private static final Logger log = LoggerFactory.getLogger(TestEndpointLatest.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(7079);

	@Test
	public void testGetDataLatest() throws FixerException, ClientProtocolException, IOException {

		String baseCurrency = "USD";
		
		LocalDateTime now = LocalDateTime.now();
		ZonedDateTime zdt = now.atZone(ZoneId.systemDefault());
		long millis = zdt.toInstant().getEpochSecond();
		
		// Mock the JSON response
		String jsonStr = "{"
				+ "\"success\": true,"
				+ "\"timestamp\": " + millis + ","
				+ "\"base\": \"USD\","
				+ "\"date\": \"2018-07-10\","
				+ "\"rates\": {"
				+ "\"GBP\": 0.72007,"
				+ "\"JPY\": 107.346001,"
				+ "\"EUR\": 0.813399"
				+ "}"
				+ "}";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + baseCurrency;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		// Prepare endpoint
		LatestEndpoint latestEndpoint = new LatestEndpoint(TestConfig.baseUrl);
		latestEndpoint.addParam("access_key", TestConfig.accessKey);
		latestEndpoint.addParam("base", baseCurrency);
		
		//ResultHeader data = latestEndpoint.getData(jsonStr);
		EndpointFieldList data = latestEndpoint.getData();
		TestUtils.displayResult(data, log);
		
		List<ExchangeRate> rates = data.getRates();
		List<ExchangeRate> expected = new ArrayList<>();
		expected.add(new ExchangeRate(baseCurrency,"GBP", 0.72007, "2018-07-10", now));
		expected.add(new ExchangeRate(baseCurrency,"JPY", 107.346001, "2018-07-10",now));
		expected.add(new ExchangeRate(baseCurrency,"EUR", 0.813399, "2018-07-10",now));
		Assert.assertEquals(expected, rates);

		log.debug("millis: {}", millis);
		if (rates!=null && !rates.isEmpty()) {
			ExchangeRate rate = rates.get(0);
			LocalDateTime ld = rate.getTimestamp();
			long actualMillis = ld.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
			Assert.assertEquals(millis, actualMillis);  
		}
	}

}

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
package com.upandcoding.fixer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.upandcoding.fixer.model.ExchangeRate;
import com.upandcoding.tests.config.TestConfig;
import com.upandcoding.tests.config.TestUtils;

import junit.framework.Assert;

public class TestLoaderLatest {

	private static final Logger log = LoggerFactory.getLogger(TestLoaderLatest.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(7079);

	@Test
	public void testGetLatest() throws FixerException, ClientProtocolException, IOException {

		String baseCurrency = "USD";

		// Mock the JSON response
		String jsonStr = "{" + "\"success\": true," + "\"timestamp\": 1531958399," + "\"base\": \"USD\","
				+ "\"date\": \"2018-07-10\"," + "\"rates\": {" + "\"GBP\": 0.72007," + "\"JPY\": 107.346001,"
				+ "\"EUR\": 0.813399" + "}" + "}";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + baseCurrency
				+ "&symbols=GBP,JPY,EUR";
		log.debug("BaseURL: {} ::  url: {}", TestConfig.baseUrl, endpointUrl);
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		FixerApiLoader fixerApiLoader = new FixerApiLoader(TestConfig.baseUrl, TestConfig.accessKey, baseCurrency);
		List<ExchangeRate> rates = fixerApiLoader.getLatest("GBP,JPY,EUR");
		List<ExchangeRate> expected = new ArrayList<>();
		LocalDateTime ld = LocalDateTime.ofInstant(Instant.ofEpochSecond(1519296206), ZoneId.systemDefault());
		expected.add(new ExchangeRate(baseCurrency, "GBP", 0.72007, "2018-07-10", ld));
		expected.add(new ExchangeRate(baseCurrency, "JPY", 107.346001, "2018-07-10", ld));
		expected.add(new ExchangeRate(baseCurrency, "EUR", 0.813399, "2018-07-10", ld));
		Assert.assertEquals(expected, rates);

		if (rates != null && !rates.isEmpty()) {
			ExchangeRate rate = rates.get(0);
			LocalDateTime ld2 = rate.getTimestamp();
			log.debug("Timestamp: {}", ld2);
		}
	}

	@Test
	public void testGetLatest2() throws FixerException, ClientProtocolException, IOException {

		String baseCurrency = "USD";

		// Mock the JSON response
		String jsonStr = "{" + "\"success\": true," + "\"timestamp\": 1519296206," + "\"base\": \"USD\","
				+ "\"date\": \"2018-07-10\"," + "\"rates\": {" + "\"GBP\": 0.72007," + "\"JPY\": 107.346001,"
				+ "\"EUR\": 0.813399" + "}" + "}";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + baseCurrency
				+ "&symbols=JPY,EUR,GBP";
		log.debug("BaseURL: {} ::  url: {}", TestConfig.baseUrl, endpointUrl);
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		String[] currencies = { "GBP", "JPY", "EUR" };

		FixerApiLoader fixerApiLoader = new FixerApiLoader(TestConfig.baseUrl, TestConfig.accessKey, baseCurrency);
		// System.exit(0);
		List<ExchangeRate> rates = fixerApiLoader.getLatest(new HashSet<String>(Arrays.asList(currencies)));
		List<ExchangeRate> expected = new ArrayList<>();
		LocalDateTime ld = LocalDateTime.ofInstant(Instant.ofEpochSecond(1519296206), ZoneId.systemDefault());
		expected.add(new ExchangeRate(baseCurrency, "GBP", 0.72007, "2018-07-10", ld));
		expected.add(new ExchangeRate(baseCurrency, "JPY", 107.346001, "2018-07-10", ld));
		expected.add(new ExchangeRate(baseCurrency, "EUR", 0.813399, "2018-07-10", ld));
		Assert.assertEquals(expected, rates);
	}

	@Test
	public void testGetLatest3() throws FixerException, ClientProtocolException, IOException {

		String baseCurrency = "USD";

		// Mock the JSON response
		String jsonStr = "{" + "\"success\": true," + "\"timestamp\": 1519296206," + "\"base\": \"USD\","
				+ "\"date\": \"2018-07-10\"," + "\"rates\": {" + "\"GBP\": 0.72007," + "\"JPY\": 107.346001,"
				+ "\"EUR\": 0.813399" + "}" + "}";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + baseCurrency;
		log.debug("BaseURL: {} ::  url: {}", TestConfig.baseUrl, endpointUrl);
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		FixerApiLoader fixerApiLoader = new FixerApiLoader(TestConfig.baseUrl, TestConfig.accessKey, baseCurrency);
		List<ExchangeRate> rates = fixerApiLoader.getLatest();
		List<ExchangeRate> expected = new ArrayList<>();
		LocalDateTime ld = LocalDateTime.ofInstant(Instant.ofEpochSecond(1519296206), ZoneId.systemDefault());
		expected.add(new ExchangeRate(baseCurrency, "GBP", 0.72007, "2018-07-10", ld));
		expected.add(new ExchangeRate(baseCurrency, "JPY", 107.346001, "2018-07-10", ld));
		expected.add(new ExchangeRate(baseCurrency, "EUR", 0.813399, "2018-07-10", ld));
		Assert.assertEquals(expected, rates);

	}

}

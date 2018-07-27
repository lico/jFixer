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

public class TestLoaderTimeSeries {

	private static final Logger log = LoggerFactory.getLogger(TestLoaderTimeSeries.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(7079);

	@Test
	public void testGetTimeSeries1() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "{"
				+ "\"success\": true,"
				+ "\"timeseries\": true,"
				+ "\"start_date\": \"2012-05-01\","
				+ "\"end_date\": \"2012-05-03\","
				+ "\"base\": \"EUR\","
				+ "\"rates\": {"
				+ "\"2012-05-01\":{"
				+ "\"USD\": 1.322891,"
				+ "\"AUD\": 1.278047,"
				+ "\"CAD\": 1.302303"
				+ "},"
				+ "\"2012-05-02\": {"
				+ "\"USD\": 1.315066,"
				+ "\"AUD\": 1.274202,"
				+ "\"CAD\": 1.299083"
				+ "},"
				+ "\"2012-05-03\": {"
				+ "\"USD\": 1.314491,"
				+ "\"AUD\": 1.280135,"
				+ "\"CAD\": 1.296868"
				+ "}"
				+ "}"
				+ "}";

		// Setup Wire Mock HTTP server
		String startDate = "2012-05-01";
		String endDate = "2012-05-03";
		String endpointUrl = "/timeseries?access_key=" + TestConfig.accessKey + "&start_date=" + startDate + "&end_date=" + endDate + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		log.debug("URL: " + endpointUrl);
		FixerApiLoader fixerApiLoader = new FixerApiLoader(TestConfig.baseUrl, TestConfig.accessKey, TestConfig.baseCurrency);
		List<ExchangeRate> rates = fixerApiLoader.getTimeSeries(startDate, endDate, "USD,AUD,CAD");
		List<ExchangeRate> expected = new ArrayList<>();
		LocalDateTime ld = LocalDateTime.ofInstant(Instant.ofEpochSecond(1519296206), ZoneId.systemDefault());
		expected.add(new ExchangeRate(TestConfig.baseCurrency, "USD", 1.322891, "2012-05-01", ld));
		expected.add(new ExchangeRate(TestConfig.baseCurrency, "AUD", 1.278047, "2012-05-01", ld));
		expected.add(new ExchangeRate(TestConfig.baseCurrency, "CAD", 1.302303, "2012-05-01", ld));

		expected.add(new ExchangeRate(TestConfig.baseCurrency, "USD", 1.315066, "2012-05-02", ld));
		expected.add(new ExchangeRate(TestConfig.baseCurrency, "AUD", 1.274202, "2012-05-02", ld));
		expected.add(new ExchangeRate(TestConfig.baseCurrency, "CAD", 1.299083, "2012-05-02", ld));

		expected.add(new ExchangeRate(TestConfig.baseCurrency, "USD", 1.314491, "2012-05-03", ld));
		expected.add(new ExchangeRate(TestConfig.baseCurrency, "AUD", 1.280135, "2012-05-03", ld));
		expected.add(new ExchangeRate(TestConfig.baseCurrency, "CAD", 1.296868, "2012-05-03", ld));
		Assert.assertEquals(expected, rates);

	}
	
	
	@Test
	public void testGetTimeSeries2() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "{"
				+ "\"success\": true,"
				+ "\"timeseries\": true,"
				+ "\"start_date\": \"2012-05-01\","
				+ "\"end_date\": \"2012-05-03\","
				+ "\"base\": \"EUR\","
				+ "\"rates\": {"
				+ "\"2012-05-01\":{"
				+ "\"USD\": 1.322891,"
				+ "\"AUD\": 1.278047,"
				+ "\"CAD\": 1.302303"
				+ "},"
				+ "\"2012-05-02\": {"
				+ "\"USD\": 1.315066,"
				+ "\"AUD\": 1.274202,"
				+ "\"CAD\": 1.299083"
				+ "},"
				+ "\"2012-05-03\": {"
				+ "\"USD\": 1.314491,"
				+ "\"AUD\": 1.280135,"
				+ "\"CAD\": 1.296868"
				+ "}"
				+ "}"
				+ "}";

		// Setup Wire Mock HTTP server
		String startDate = "2012-05-01";
		String endDate = "2012-05-03";
		String endpointUrl = "/timeseries?access_key=" + TestConfig.accessKey + "&start_date=" + startDate + "&end_date=" + endDate + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		log.debug("URL: " + endpointUrl);
		FixerApiLoader fixerApiLoader = new FixerApiLoader(TestConfig.baseUrl, TestConfig.accessKey, TestConfig.baseCurrency);
		List<ExchangeRate> rates = fixerApiLoader.getTimeSeries(startDate, endDate, "USD");
		List<ExchangeRate> expected = new ArrayList<>();
		LocalDateTime ld = LocalDateTime.ofInstant(Instant.ofEpochSecond(1519296206), ZoneId.systemDefault());
		expected.add(new ExchangeRate(TestConfig.baseCurrency, "USD", 1.322891, "2012-05-01", ld));

		expected.add(new ExchangeRate(TestConfig.baseCurrency, "USD", 1.315066, "2012-05-02", ld));

		expected.add(new ExchangeRate(TestConfig.baseCurrency, "USD", 1.314491, "2012-05-03", ld));
		Assert.assertEquals(expected, rates);

	}

}

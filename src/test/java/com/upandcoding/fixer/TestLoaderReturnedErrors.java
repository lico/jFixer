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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.upandcoding.fixer.model.ExchangeRate;
import com.upandcoding.tests.config.TestConfig;
import com.upandcoding.tests.config.TestUtils;

import junit.framework.Assert;

public class TestLoaderReturnedErrors {

	private static final Logger log = LoggerFactory.getLogger(TestLoaderReturnedErrors.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(7079);

	@Test
	public void testError202() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "{"
				+ "\"success\":false,"
				+ "\"error\":"
				+ "{\"code\":202,"
				+ "\"type\":\"invalid_currency_codes\","
				+ "\"info\":\"You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]\""
				+ "}"
				+ "}";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		log.debug("BaseURL: {} ::  url: {}", TestConfig.baseUrl, endpointUrl);
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		FixerApiLoader fixerApiLoader = new FixerApiLoader(TestConfig.baseUrl, TestConfig.accessKey, TestConfig.baseCurrency);
		try {
			List<ExchangeRate> exchangeRates = fixerApiLoader.getLatest();
		} catch (FixerException fe) {
			Assert.assertEquals(202, fe.getFixerCode());
			Assert.assertEquals("invalid_currency_codes", fe.getFixerType());
			Assert.assertEquals("You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]", fe.getLocalizedMessage());
		}

	}

	@Test(expected = JsonParseException.class)
	public void testGetErrorNotJsonFormat() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "<html><body>Hello World</body>";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServerErrorHtml(TestConfig.baseUrl, endpointUrl, jsonStr);

		FixerApiLoader fixerApiLoader = new FixerApiLoader(TestConfig.baseUrl, TestConfig.accessKey, TestConfig.baseCurrency);
		List<ExchangeRate> exchangeRates = fixerApiLoader.getLatest();
	}

	@Test
	public void testGetHttpError404() throws FixerException, ClientProtocolException, IOException {

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServerError404(TestConfig.baseUrl, endpointUrl);

		FixerApiLoader fixerApiLoader = new FixerApiLoader(TestConfig.baseUrl, TestConfig.accessKey, TestConfig.baseCurrency);
		try {
			List<ExchangeRate> exchangeRates = fixerApiLoader.getLatest();
			Assert.assertTrue(false);
		} catch (FixerException fe) {
			log.debug(fe.getLocalizedMessage());
			Assert.assertEquals("ERROR: '404 Not Found' when loading URL: " + TestConfig.baseUrl + endpointUrl, fe.getLocalizedMessage());
		}
	}

	@Test
	public void testGetHttpError500() throws FixerException, ClientProtocolException, IOException {

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServerError500(TestConfig.baseUrl, endpointUrl);

		FixerApiLoader fixerApiLoader = new FixerApiLoader(TestConfig.baseUrl, TestConfig.accessKey, TestConfig.baseCurrency);
		try {
			List<ExchangeRate> exchangeRates = fixerApiLoader.getLatest();
			Assert.assertTrue(false);
		} catch (FixerException fe) {
			String expected = "ERROR: '500 Server Error' when loading URL: " + TestConfig.baseUrl + endpointUrl;
			Assert.assertEquals(expected, fe.getLocalizedMessage());
		}
	}
}

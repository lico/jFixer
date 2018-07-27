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

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.upandcoding.fixer.FixerException;
import com.upandcoding.fixer.endpoint.field.EndpointFieldList;
import com.upandcoding.tests.config.TestConfig;
import com.upandcoding.tests.config.TestUtils;

import junit.framework.Assert;

public class TestEndpointReturnedErrors {

	private static final Logger log = LoggerFactory.getLogger(TestEndpointReturnedErrors.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(7079);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	LatestEndpoint latestEndpoint;

	@Before
	public void init() {
		latestEndpoint = new LatestEndpoint(TestConfig.baseUrl);
		latestEndpoint.addParam("access_key", TestConfig.accessKey);
		latestEndpoint.addParam("base", TestConfig.baseCurrency);
	}

	@Test
	public void testGetErrorNoErrorCode() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = null;

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		// Prepare endpoint
		try {
			EndpointFieldList data = latestEndpoint.getData();
		} catch (FixerException fe) {
			Assert.assertEquals(0, fe.getFixerCode());
			Assert.assertEquals("unknown_error_type", fe.getFixerType());
			Assert.assertEquals("Failed to load data, unknown error", fe.getLocalizedMessage());
		}
	}

	// Invalid access kwy
	@Test
	public void testGetError101() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "{\"success\":false,\"error\":{\"code\":101,\"type\":\"invalid_access_key\"}}";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		// Prepare endpoint
		try {
			EndpointFieldList data = latestEndpoint.getData();
		} catch (FixerException fe) {
			log.debug("Json: {}", latestEndpoint.getJsonResponse());
			Assert.assertEquals(101, fe.getFixerCode());
			Assert.assertEquals("invalid_access_key", fe.getFixerType());
			Assert.assertEquals("invalid_access_key", fe.getLocalizedMessage());
		}
	}
	
	@Test
	public void testGetError105() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "{\"success\":false,\"error\":{\"code\":105,\"type\":\"base_currency_access_restricted\"}}";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		// Prepare endpoint
		try {
			EndpointFieldList data = latestEndpoint.getData();
		} catch (FixerException fe) {
			Assert.assertEquals(105, fe.getFixerCode());
			Assert.assertEquals("base_currency_access_restricted", fe.getFixerType());
			Assert.assertEquals("base_currency_access_restricted", fe.getLocalizedMessage());
		}
	}

	@Test
	public void testGetError202() throws FixerException, ClientProtocolException, IOException {

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
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		try {
			EndpointFieldList data = latestEndpoint.getData();
		} catch (FixerException fe) {
			Assert.assertEquals(202, fe.getFixerCode());
			Assert.assertEquals("invalid_currency_codes", fe.getFixerType());
			Assert.assertEquals("You have provided one or more invalid Currency Codes. [Required format: currencies=EUR,USD,GBP,...]", fe.getLocalizedMessage());
		}
	}

	@Test (expected = JsonParseException.class)
	public void testGetErrorNotJsonFormat() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "<html><body>Hello World</body>";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServerErrorHtml(TestConfig.baseUrl, endpointUrl, jsonStr);
		EndpointFieldList data = latestEndpoint.getData();
	}

	@Test
	public void testGetHttpError404() throws FixerException, ClientProtocolException, IOException {

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServerError404(TestConfig.baseUrl, endpointUrl);

		try {
			EndpointFieldList data = latestEndpoint.getData();
			Assert.assertTrue(false);
		} catch (FixerException fe) {
			Assert.assertEquals("ERROR: '404 Not Found' when loading URL: " + TestConfig.baseUrl + endpointUrl, fe.getLocalizedMessage());
		}
	}

	@Test
	public void testGetHttpError500() throws FixerException, ClientProtocolException, IOException {

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServerError500(TestConfig.baseUrl, endpointUrl);

		try {
			EndpointFieldList data = latestEndpoint.getData();
			Assert.assertTrue(false);
		} catch (FixerException fe) {
			String expected = "ERROR: '500 Server Error' when loading URL: " + TestConfig.baseUrl + endpointUrl;
			Assert.assertEquals(expected, fe.getLocalizedMessage());
		}
	}

	@Test(expected = JsonParseException.class)
	public void testErrorInconsistentData() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "{"
				+ "\"success\": true,"
				+ "\"timestamp\": 1519296206,"
				+ "\"base\": \"USD\","
				+ "\"date\": \"2018-07-10\","
				+ "\"rates\": {"
				+ "\"GBP\": abcdef,"
				+ "\"EUR\": 1.314E+2500"
				+ "}"
				+ "}";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		// Prepare endpoint
		LatestEndpoint latestEndpoint = new LatestEndpoint(TestConfig.baseUrl);
		latestEndpoint.addParam("access_key", TestConfig.accessKey);
		latestEndpoint.addParam("base", TestConfig.baseCurrency);

		EndpointFieldList data = latestEndpoint.getData();

	}

	@Test(expected = IllegalArgumentException.class)
	public void testErrorInconsistentData2() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "{"
				+ "\"success\": true,"
				+ "\"timestamp\": 1519296206,"
				+ "\"base\": \"USD\","
				+ "\"date\": \"2018-07-10\","
				+ "\"rates\": {"
				+ "\"GBP\": 125,"
				+ "\"EUR\": 1.314E+2500"
				+ "}"
				+ "}";

		// Setup Wire Mock HTTP server
		String endpointUrl = "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		// Prepare endpoint
		LatestEndpoint latestEndpoint = new LatestEndpoint(TestConfig.baseUrl);
		latestEndpoint.addParam("access_key", TestConfig.accessKey);
		latestEndpoint.addParam("base", TestConfig.baseCurrency);

		EndpointFieldList data = latestEndpoint.getData();

	}

}

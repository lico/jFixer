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
import com.upandcoding.fixer.model.Fluctuation;
import com.upandcoding.tests.config.TestConfig;
import com.upandcoding.tests.config.TestUtils;

import junit.framework.Assert;

public class TestLoaderFluctuations {

	private static final Logger log = LoggerFactory.getLogger(TestLoaderFluctuations.class);

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(7079);

	@Test
	public void testGetLatest() throws FixerException, ClientProtocolException, IOException {

		// Mock the JSON response
		String jsonStr = "{"
				+ "\"success\":true,"
				+ "\"fluctuation\":true,"
				+ "\"start_date\":\"2018-02-25\","
				+ "\"end_date\":\"2018-02-26\","
				+ "\"base\":\"EUR\","
				+ "\"rates\":{"
				+ "\"USD\":{"
				+ "\"start_rate\":1.228952,"
				+ "\"end_rate\":1.232735,"
				+ "\"change\":0.0038,"
				+ "\"change_pct\":0.3078"
				+ "},"
				+ "\"JPY\":{"
				+ "\"start_rate\":131.587611,"
				+ "\"end_rate\":131.651142,"
				+ "\"change\":0.0635,"
				+ "\"change_pct\":0.0483"
				+ "}"
				+ "}"
				+ "}";

		// Setup Wire Mock HTTP server
		String startDate = "2018-02-25";
		String endDate = "2018-02-26";
		String endpointUrl = "/fluctuation?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency + "&start_date=" + startDate + "&end_date=" + endDate + "&symbols=USD,JPY";
		TestUtils.setupMockHttpServer(TestConfig.baseUrl, endpointUrl, jsonStr);

		FixerApiLoader fixerApiLoader = new FixerApiLoader(TestConfig.baseUrl, TestConfig.accessKey, TestConfig.baseCurrency);
		List<Fluctuation> fluctuations = fixerApiLoader.getFluctuations("2018-02-25", "2018-02-26", "USD,JPY");
		List<Fluctuation> expected = new ArrayList<>();
		LocalDateTime ld = LocalDateTime.ofInstant(Instant.ofEpochSecond(1519296206), ZoneId.systemDefault());
		expected.add(new Fluctuation("2018-02-25", "2018-02-26", TestConfig.baseCurrency, "USD", 1.228952, 1.232735, 0.0038, 0.3078));
		expected.add(new Fluctuation("2018-02-25", "2018-02-26", TestConfig.baseCurrency, "JPY", 131.587611, 131.651142, 0.0635, 0.0483));
		Assert.assertEquals(expected, fluctuations);

	}

}

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
package com.upandcoding.tests.config;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.upandcoding.fixer.endpoint.field.EndpointField;
import com.upandcoding.fixer.endpoint.field.EndpointFieldList;
import com.upandcoding.fixer.model.Currency;
import com.upandcoding.fixer.model.ExchangeRate;
import com.upandcoding.fixer.model.Fluctuation;

public class TestUtils {

	private static final Logger log = LoggerFactory.getLogger(TestUtils.class);

	/* **********************************************************************************
	 * Mocking the HTTP server with Wire Mock
	 * **********************************************************************************
	 */

	public static void setupMockHttpServer(String baseUrl, String relativeUrl, String jsonStr) throws ClientProtocolException, IOException {
		String fullUrl = baseUrl + relativeUrl;

		/*
		log.debug("BaseUrl: {}", baseUrl);
		log.debug("relativeUrl: {}", relativeUrl);
		log.debug("fullUrl: {}", fullUrl);
		*/

		// Mock the HTTP server
		stubFor(get(urlEqualTo(relativeUrl))
				// .withHeader("Accept", equalTo("application/json"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(jsonStr)));

		// HTTP Client (just to make sure it works)
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(fullUrl);
		request.addHeader("Accept", "application/json");
		HttpResponse httpResponse = httpClient.execute(request);
		Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
	}

	public static void setupMockHttpServerErrorHtml(String baseUrl, String relativeUrl, String jsonStr) throws ClientProtocolException, IOException {
		String fullUrl = baseUrl + relativeUrl;

		// Mock the HTTP server
		stubFor(get(urlEqualTo(relativeUrl))
				// .withHeader("Accept", equalTo("application/json"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "text/html")
						.withBody(jsonStr)));

		// HTTP Client (just to make sure it works)
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(fullUrl);
		request.addHeader("Accept", "text/html");
		HttpResponse httpResponse = httpClient.execute(request);
		Assert.assertEquals(200, httpResponse.getStatusLine().getStatusCode());
	}

	public static void setupMockHttpServerError404(String baseUrl, String relativeUrl) throws ClientProtocolException, IOException {
		String fullUrl = baseUrl + relativeUrl;

		// Mock the HTTP server
		stubFor(get(urlEqualTo(relativeUrl))
				.willReturn(aResponse()
						.withStatus(404)
						.withHeader("Content-Type", "text/html")
						.withBody("<html><body>Page not found</body></html>")));

		// HTTP Client (just to make sure it works)
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(fullUrl);
		request.addHeader("Accept", "text/html");
		HttpResponse httpResponse = httpClient.execute(request);
		Assert.assertEquals(404, httpResponse.getStatusLine().getStatusCode());
	}

	public static void setupMockHttpServerError500(String baseUrl, String relativeUrl) throws ClientProtocolException, IOException {
		String fullUrl = baseUrl + relativeUrl;

		// Mock the HTTP server
		stubFor(get(urlEqualTo(relativeUrl))
				.willReturn(aResponse()
						.withStatus(500)));

		// HTTP Client (just to make sure it works)
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(fullUrl);
		request.addHeader("Accept", "text/html");
		HttpResponse httpResponse = httpClient.execute(request);
		Assert.assertEquals(500, httpResponse.getStatusLine().getStatusCode());
	}

	/* **********************************************************************************
	 * Display results consistently
	 * **********************************************************************************
	 */
	public static void displayRates(Map<String, Double> rates, String baseCurrency, Logger log) {

		log.debug("");
		log.debug("*** RATES FOR: ");
		if (rates != null && !rates.isEmpty()) {
			Set<String> keys = rates.keySet();
			for (String symbol : keys) {
				log.debug("{}: {}", symbol, rates.get(symbol));
			}
		} else {
			log.debug("=> No Rates");
		}
	}

	@Deprecated
	public static boolean listEquals(List<ExchangeRate> list1, List<ExchangeRate> list2) {
		boolean result = false;
		if ((list1 == null || list1.isEmpty()) && (list2 == null || list2.isEmpty())) {
			result = true;
		} else {
			if ((list1 == null || list1.isEmpty()) || (list2 == null || list2.isEmpty())) {
				result = false;
			} else {
				if (list1.size() != list2.size()) {
					result = false;
				} else {
					boolean compare = true;
					for (ExchangeRate rate : list1) {
						if (!list2.contains(rate)) {
							log.debug("List 2 does not containe {}", rate.toString());
							compare = false;
							break;
						}
					}
					if (compare) {
						for (ExchangeRate rate : list2) {
							if (!list1.contains(rate)) {
								log.debug("List 1 does not containe {}", rate.toString());
								compare = false;
								break;
							}
						}
						result = compare;
					} else {
						result = false;
					}
				}
			}
		}
		return result;
	}

	public static void displayResult(EndpointFieldList data, Logger log) {
		log.debug("");
		log.debug("*** FIELDS");
		List<EndpointField> fields = data.getFields();
		for (EndpointField field : fields) {
			log.debug("    Field: {} -> {}, of type: {}", field.getName(), field.getValue(), field.getType());
		}

		if (CollectionUtils.isNotEmpty(data.getCurrencies())) {
			log.debug("");
			log.debug("*** CURRENCIES");
			List<Currency> currencies = data.getCurrencies();
			for (Currency currency : currencies) {
				log.debug(currency.toString());
			}
		}

		if (CollectionUtils.isNotEmpty(data.getRates())) {
			log.debug("");
			log.debug("*** RATES");
			List<ExchangeRate> rates = data.getRates();
			for (ExchangeRate rate : rates) {
				log.debug(rate.toString());
			}
		}

		if (CollectionUtils.isNotEmpty(data.getFluctuations())) {
			log.debug("");
			log.debug("*** FLUCTUATIONS");
			List<Fluctuation> fluctuations = data.getFluctuations();
			for (Fluctuation fluctuation : fluctuations) {
				log.debug(fluctuation.toString());
			}
		}
	}

}

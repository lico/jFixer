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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.upandcoding.fixer.endpoint.ConvertEndpoint;
import com.upandcoding.fixer.endpoint.Endpoint;
import com.upandcoding.fixer.endpoint.FluctuationEndpoint;
import com.upandcoding.fixer.endpoint.HistoricalEndpoint;
import com.upandcoding.fixer.endpoint.LatestEndpoint;
import com.upandcoding.fixer.endpoint.SupportedSymbolsEndpoint;
import com.upandcoding.fixer.endpoint.TimeSeriesEndpoint;
import com.upandcoding.fixer.endpoint.field.EndpointField;
import com.upandcoding.fixer.endpoint.field.EndpointFieldList;
import com.upandcoding.fixer.model.Currency;
import com.upandcoding.fixer.model.ExchangeRate;
import com.upandcoding.fixer.model.Fluctuation;

/**
 * Class for sending request to the Fixer API
 * <ul>
 * <li>accessKey:</li>
 * <li>baseCurrency: must be a valid 3-digits ISO code</li>
 * <li>displayName: the english description as provided by the Fixer API</li>
 * </ul>
 * Also note that the class contains a static list of currencies available at Fixer API.
 * <p>
 * List<Currency> currencies = Currency.getSupportedcurrencies();
 * <p>
 * Not based on java.util.Currency because this latter comes with a pre-filled
 * list of available currencies. What we want here is to get the list of
 * currencies actually supported by the Fixer API.
 * 
 * @See <a href="https://fixer.io/documentation">Fixer API Documentation</a>
 * 
 * @author Lionel Conforto
 *
 */
public class FixerApiLoader {

	private final static Logger log = LoggerFactory.getLogger(FixerApiLoader.class);

	private String accessKey;
	private String baseCurrency;
	private String baseUrl;

	public FixerApiLoader() {

	}

	public FixerApiLoader(String baseUrl, String accessKey, String baseCurrency) {
		this.baseUrl = baseUrl;
		this.accessKey = accessKey;
		this.baseCurrency = baseCurrency;

		log.debug("Starting API Loader with the following settings: ");
		log.debug("    BaseURL: {}", this.baseUrl);
		log.debug("    AccessKey: {}", this.accessKey);
		log.debug("    BaseCurrency: {}", this.baseCurrency);
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * Fluctuations of a currency between two dates. Returns the rate at beginning and at end of the period
	 * plus the variation in value and in percentage
	 * 
	 * @param startDate the start date for which historical rates are requested in format yyyy-MM-dd, eg: 2018-04-26
	 * @param endDate the end date for which historical rates are requested in format yyyy-MM-dd, eg: 2018-04-26
	 * 
	 * @See <a href="https://fixer.io/documentation#fluctuation">Fluctuation Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public List<Fluctuation> getFluctuations(String startDate, String endDate, String symbols) throws FixerException, JsonParseException, IOException {

		// Check Dates
		try {
			LocalDate sDate = LocalDate.parse(startDate, EndpointField.dateFormatter);
			LocalDate eDate = LocalDate.parse(endDate, EndpointField.dateFormatter);
			if (sDate.isAfter(eDate)) {
				throw new FixerException("Start date cannot be after end date");
			}
		} catch (DateTimeParseException e) {
			throw new FixerException("Invalid or null date");
		}

		// Calculates
		Endpoint fluctuationEndpoint = new FluctuationEndpoint(baseUrl);
		fluctuationEndpoint.addParam("access_key", accessKey);
		fluctuationEndpoint.addParam("base", baseCurrency);
		fluctuationEndpoint.addParam("start_date", startDate);
		fluctuationEndpoint.addParam("end_date", endDate);
		fluctuationEndpoint.addParam("symbols", symbols);

		EndpointFieldList data = fluctuationEndpoint.getData();
		return data.getFluctuations();

	}

	/**
	 * Fluctuations of a currency between two dates. Returns the rate at beginning and at end of the period
	 * plus the variation in value and in percentage
	 * 
	 * @param startDate a valid local date
	 * @param endDate a valid local date
	 * 
	 * @See <a href="https://fixer.io/documentation#fluctuation">Fluctuation Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public List<Fluctuation> getFluctuations(LocalDate startDate, LocalDate endDate, String symbols) throws FixerException, JsonParseException, IOException {

		String strStartDate = null;
		if (startDate != null) {
			strStartDate = startDate.format(EndpointField.dateFormatter);
		} else {
			throw new FixerException("Invalid or null date");
		}

		String strEndDate = null;
		if (endDate != null) {
			strEndDate = endDate.format(EndpointField.dateFormatter);
		} else {
			throw new FixerException("Invalid or null date");
		}

		return getFluctuations(strStartDate, strEndDate, symbols);
	}

	/**
	 * Convert an amount in a given currency into the target currency
	 * 
	 * @param fromCurrency is the currency for amount
	 * @param targetCurrency is the target currency
	 * @param amount a double that represents the initial value in the fromCurrency
	 * 
	 * @return the value in the targetCurrency
	 * 
	 * @See <a href="https://fixer.io/documentation#convertcurrency">Convert Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public double getConversion(String fromCurrency, String targetCurrency, double amount) throws FixerException, JsonParseException, IOException {

		Endpoint convertEndpoint = new ConvertEndpoint(baseUrl);
		convertEndpoint.addParam("access_key", accessKey);
		convertEndpoint.addParam("base", baseCurrency);
		convertEndpoint.addParam("from", fromCurrency);
		convertEndpoint.addParam("to", targetCurrency);
		convertEndpoint.addParam("amount", "" + amount);

		EndpointFieldList data = convertEndpoint.getData();
		EndpointField fldResult = data.getField("result");
		if (fldResult != null) {
			return fldResult.getDouble();
		} else {
			throw new FixerException("Unable to determine conversion result due to unknown error");
		}
	}

	/**
	 * Returns a list of exchange rates between two dates
	 * 
	 * @param startDate the start date for which historical rates are requested in format yyyy-MM-dd, eg: 2018-04-26
	 * @param endDate the end date for which historical rates are requested in format yyyy-MM-dd, eg: 2018-04-26
	 * 
	 * @return list of exchange rate
	 * 
	 * @See ExchangeRate
	 * @See <a href="https://fixer.io/documentation#timeseries">Time-Series Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public List<ExchangeRate> getTimeSeries(String startDate, String endDate) throws FixerException, JsonParseException, IOException {

		// Check Dates
		try {
			LocalDate sDate = LocalDate.parse(startDate, EndpointField.dateFormatter);
			LocalDate eDate = LocalDate.parse(endDate, EndpointField.dateFormatter);
			if (sDate.isAfter(eDate)) {
				throw new FixerException("Start date cannot be after end date");
			}
		} catch (DateTimeParseException e) {
			throw new FixerException("Invalid or null date");
		}

		Endpoint timeSeries = new TimeSeriesEndpoint(baseUrl);
		timeSeries.addParam("access_key", accessKey);
		timeSeries.addParam("start_date", startDate);
		timeSeries.addParam("end_date", endDate);
		timeSeries.addParam("base", baseCurrency);

		EndpointFieldList data = timeSeries.getData();

		return data.getRates();

	}

	/**
	 * Returns a list of exchange rates between two dates
	 * 
	 * @param startDate a valid local date
	 * @param endDate a valid local date
	 * 
	 * @return list of exchange rate
	 * 
	 * @See ExchangeRate
	 * @See <a href="https://fixer.io/documentation#timeseries">Time-Series Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public List<ExchangeRate> getTimeSeries(LocalDate startDate, LocalDate endDate) throws FixerException, JsonParseException, IOException {

		String strStartDate = null;
		if (startDate != null) {
			strStartDate = startDate.format(EndpointField.dateFormatter);
		} else {
			throw new FixerException("Invalid or null date");
		}

		String strEndDate = null;
		if (endDate != null) {
			strEndDate = endDate.format(EndpointField.dateFormatter);
		} else {
			throw new FixerException("Invalid or null date");
		}

		return getTimeSeries(strStartDate, strEndDate);

	}

	/**
	 * Returns a list of historical exchange rates for the list of currencies. If this list is null, exchange rates are returned for all available currencies.
	 * <p>
	 * This method calls the Latest Rates Endpoint of the <a href="https://fixer.io/">Fixer API</a>.
	 * 
	 * @param date	the date for which historical rates are requested in format yyyy-MM-dd, eg: 2018-04-26
	 * @param symbols	a comma separated list of currency symbols, like: EUR,USD,CHF 
	 * @return	List of ExchangeRate objects
	 * 
	 * @See	ExchangeRate
	 * @See <a href="https://fixer.io/documentation#historicalrates">Historical Rates Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public List<ExchangeRate> getHistorical(String date, String symbols) throws FixerException, JsonParseException, IOException {

		// Check Date
		try {
			LocalDate.parse(date, EndpointField.dateFormatter);
		} catch (DateTimeParseException e) {
			throw new FixerException("Invalid or null date");
		}

		// Calculate
		Endpoint historical = new HistoricalEndpoint(baseUrl);
		historical.addParam("access_key", accessKey);
		if (StringUtils.isNotBlank(symbols)) {
			historical.addParam("symbols", symbols);
		}
		historical.addPathVariable("date", date);
		historical.addParam("base", baseCurrency);

		EndpointFieldList data = historical.getData();

		return data.getRates();
	}

	/**
	 * Returns a list of historical exchange rates for the list of currencies. If this list is null, exchange rates are returned for all available currencies.
	 * <p>
	 * This method calls the Latest Rates Endpoint of the <a href="https://fixer.io/">Fixer API</a>.
	 * 
	 * @param date	a valid local date
	 * @param symbols	a list of valid ISO currency symbols 
	 * @return	List of ExchangeRate objects
	 * 
	 * @See	ExchangeRate
	 * @See <a href="https://fixer.io/documentation#historicalrates">Historical Rates Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public List<ExchangeRate> getHistorical(LocalDate date, List<String> symbols) throws FixerException, JsonParseException, IOException {
		String strSymbols = null;
		if (CollectionUtils.isNotEmpty(symbols)) {
			strSymbols = String.join(",", symbols);
		}

		String strDate = null;
		if (date != null) {
			strDate = date.format(EndpointField.dateFormatter);
		} else {
			throw new FixerException("Invalid or null date");
		}

		return getHistorical(strDate, strSymbols);
	}

	/**
	 * Returns a list of real-time or latest exchange rates for all available currencies.
	 * 
	 * @return	List of ExchangeRate objects
	 * 
	 * @See	ExchangeRate
	 * @See <a href="https://fixer.io/documentation#latestrates">Latest Rates Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public List<ExchangeRate> getLatest() throws FixerException, JsonParseException, IOException {
		String symbols = null;
		return getLatest(symbols);
	}

	/**
	 * Returns a list of real-time or latest exchange rates for the list of currencies. If this list is null, exchange rates are returned for all available currencies.
	 * <p>
	 * This method calls the Latest Rates Endpoint of the <a href="https://fixer.io/">Fixer API</a>.
	 * 
	 * @param symbols	a comma separated list of currency symbols, like: EUR,USD,CHF 
	 * @return	List of ExchangeRate objects
	 * 
	 * @See	ExchangeRate
	 * @See <a href="https://fixer.io/documentation#latestrates">Latest Rates Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public List<ExchangeRate> getLatest(String symbols) throws FixerException, JsonParseException, IOException {

		LatestEndpoint latestEndpoint = new LatestEndpoint(baseUrl);
		latestEndpoint.addParam("access_key", accessKey);
		latestEndpoint.addParam("base", baseCurrency);
		if (StringUtils.isNotBlank(symbols)) {
			latestEndpoint.addParam("symbols", symbols);
		}

		EndpointFieldList data = latestEndpoint.getData();
		return data.getRates();
	}

	/**
	 * Returns a list of real-time or latest exchange rates for the list of currencies. If this list is null, exchange rates are returned for all available currencies.
	 * <p>
	 * This method calls the Latest Rates Endpoint of the <a href="https://fixer.io/">Fixer API</a>.
	 * 
	 * @param symbols	a list of currency symbols
	 * @return	List of ExchangeRate objects
	 * 
	 * @See	ExchangeRate
	 * @See <a href="https://fixer.io/documentation#latestrates">Latest Rates Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public List<ExchangeRate> getLatest(Set<String> symbols) throws FixerException, JsonParseException, IOException {
		String strSymbols = null;
		if (CollectionUtils.isNotEmpty(symbols)) {
			strSymbols = String.join(",", symbols);
		}
		return getLatest(strSymbols);
	}

	/**
	 * Returns a list of currencies available for the Fixer API.
	 * <p>
	 * The Currency returned is not the java.util.Currency because this class already comes with its own list of currencies all of which are not supported by the Fixer API.
	 * <p>
	 * This method calls the Latest Rates Endpoint of the <a href="https://fixer.io/">Fixer API</a>.
	 * 
	 * @return	List of Currency objects
	 * 
	 * @See	Currency
	 * @See <a href="https://fixer.io/documentation#supportedsymbols">Supported Symbols Endpoint documentation</a>
	 * 
	 * @throws FixerException
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public List<Currency> getSupportedSymbols() throws FixerException, JsonParseException, IOException {

		List<Currency> currencies = new ArrayList<>();

		SupportedSymbolsEndpoint symbolsEndPoint = new SupportedSymbolsEndpoint(baseUrl);
		symbolsEndPoint.addParam("access_key", accessKey);

		EndpointFieldList data = symbolsEndPoint.getData();
		currencies = data.getCurrencies();

		Currency.setSupportedCurrencies(currencies);

		return currencies;
	}
}

package com.upandcoding.fixer.endpoint;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.upandcoding.fixer.endpoint.field.EndpointField;

public class LatestEndpoint extends Endpoint {

	private static final Logger log = LoggerFactory.getLogger(LatestEndpoint.class);

	private void init() {
		endpointPath = "latest";
		if (CollectionUtils.isNotEmpty(authorizedEndpointParameters)) {
			authorizedEndpointParameters.clear();
		}
		authorizedEndpointParameters.add(new EndpointField("access_key", EndpointField.TYPE_STR, true));
		authorizedEndpointParameters.add(new EndpointField("base", EndpointField.TYPE_STR, false));
		authorizedEndpointParameters.add(new EndpointField("symbols", EndpointField.TYPE_LST, false));
	}

	public LatestEndpoint() {
		init();
	}

	public LatestEndpoint(String baseUrl) {
		this.baseUrl = baseUrl;
		init();
	}

	/*
	protected ResultHeader analyzeData(JsonParser parser) throws JsonParseException, IOException {
		// ResultsRates results = new ResultsRates();
		ResultHeader result = new ResultHeader();
		// String day = "";
		// String fromCurrency = "";
	
		while (!parser.isClosed()) {
			JsonToken jsonToken = parser.nextToken();
			// Value strings : headers of the request
			if (JsonToken.VALUE_STRING.equals(jsonToken)) {
				String fieldName = parser.getCurrentName();
				if (jsonToken.isNumeric()) {
					float fieldValue = parser.getFloatValue();
					log.debug(fieldName + " -> " + fieldValue);
				} else {
					String fieldValue = parser.getText();
					// log.debug(fieldName + " -> " + fieldValue);
					if ("date".equalsIgnoreCase(fieldName)) {
						result.setDate(fieldValue);
					}
					if ("base".equalsIgnoreCase(fieldName)) {
						result.setBaseCurrency(fieldValue);
					}
					if ("success".equalsIgnoreCase(fieldName)) {
						result.setSuccess(fieldValue);
					}
					if ("historical".equalsIgnoreCase(fieldName)) {
						result.setHistorical(fieldValue);
					}
					if ("query".equalsIgnoreCase(fieldName)) {
						result.setQuery(fieldValue);
					}
					if ("info".equalsIgnoreCase(fieldName)) {
						result.setInfo(fieldValue);
					}
					if ("fluctuation".equalsIgnoreCase(fieldName)) {
						result.setFluctuation(fieldValue);
					}
					if ("start-date".equalsIgnoreCase(fieldName)) {
						result.setStartDate(fieldValue);
					}
					if ("end-date".equalsIgnoreCase(fieldName)) {
						result.setEndDate(fieldValue);
					}
					if ("timestamp".equalsIgnoreCase(fieldName)) {
						result.setTimestamp(fieldValue);
					}
					if ("result".equalsIgnoreCase(fieldName)) {
						result.setResult(fieldValue);
					}
					if ("timeseries".equalsIgnoreCase(fieldName)) {
						result.setTimeseries(fieldValue);
					}
				}
			}
	
			// Number strings: exchange rates
			if (JsonToken.VALUE_NUMBER_FLOAT.equals(jsonToken)) {
				ExchangeRate exchangeRate = new ExchangeRate();
				exchangeRate.setDate(result.getDate());
				String fieldName = parser.getCurrentName();
				exchangeRate.setBaseCurrency(result.getBaseCurrency());
				exchangeRate.setTargetCurrency(fieldName);
				if (jsonToken.isNumeric()) {
					float fieldValue = parser.getFloatValue();
					exchangeRate.setRate(fieldValue);
					log.debug(fieldName + " -> " + fieldValue);
					Double rate = Double.valueOf(fieldValue);
					result.add(fieldName, rate);
				} else {
					String fieldValue = parser.getText();
					log.debug(fieldName + " -> " + fieldValue);
				}
	
				// Store exchange rate
				// log.debug(exchangeRate.toString());
			}
	
		}
		return result;
	}
	*/
}

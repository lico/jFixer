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
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.upandcoding.fixer.FixerException;
import com.upandcoding.fixer.endpoint.field.EndpointField;
import com.upandcoding.fixer.endpoint.field.EndpointFieldList;
import com.upandcoding.fixer.model.Currency;
import com.upandcoding.fixer.model.ExchangeRate;
import com.upandcoding.fixer.model.Fluctuation;

/**
 * Represents a endpoint of the Fixer API 
 * 
 * @See <a href="https://fixer.io/documentation">endpoints documentation</a>
 * 
 * @author Lionel Conforto
 *
 */
public class Endpoint {

	private static final Logger log = LoggerFactory.getLogger(Endpoint.class);

	private static final String ERR_NOT_NULL = "Parameter '%s' cannot be null";

	// Authorized parameters : only these parameters are allowed, some mandatory,
	// some optional
	protected static final Set<EndpointField> authorizedEndpointParameters = new LinkedHashSet<>();

	// Specific path of the URL. For example, "latest" for the Latest Rates Endpoint
	protected static String endpointPath;

	// Base URL of the Fixer API service, something like https://data.fixer.io/api/
	protected String baseUrl;

	// Json String returned by the URL. This value is kept for later reuse
	protected String jsonResponse;

	// Actual parameters : these are the parameters actually specified for a given
	// request
	// time
	protected Set<EndpointField> requestedEndpointParameters = new LinkedHashSet<>();

	/**
	 * Adds a string parameter, a not mandatory param 
	 * @param name
	 * @param value
	 */
	public void addParam(String name, String value) {
		EndpointField param = new EndpointField(name, value, EndpointField.TYPE_STR, false);
		this.requestedEndpointParameters.add(param);
	}

	/**
	 * A path variable is a parameter that does not appear in the parameters of the URL (after ?)
	 * but in the URL itself. For example, the parameter 'date':
	 * /2018-02-15
	 * @param name
	 * @param value
	 */
	public void addPathVariable(String name, String value) {
		EndpointField param = new EndpointField(name, value, EndpointField.TYPE_STR, false);
		param.setInUrlParameter(false);
		this.requestedEndpointParameters.add(param);
	}

	/**
	 * Builds the whole URL for the endpoint by concatenating the base URL with the 
	 * specific path and all parameters. Is specific to a request (that is specific
	 * to the actual parameters).
	 * 
	 * @return valid full URL
	 * 
	 * @throws FixerException
	 */
	public String getRequestUrl() throws FixerException {
		Validate.notNull(baseUrl, ERR_NOT_NULL, "baseUrl");
		Validate.notNull(endpointPath, ERR_NOT_NULL, "endpoint path");

		if (!StringUtils.endsWithIgnoreCase(baseUrl, "/")) {
			baseUrl = baseUrl + "/";
		}
		String endpointUrl = baseUrl + getUrlParameters();
		log.debug("EndpointUrl: {}", endpointUrl);
		return endpointUrl;
	}

	/**
	 * Concatenates the request parameters into a list of URL
	 * parameters
	 * 
	 * @return a valid URL parameters string
	 * 
	 * @throws FixerException
	 */
	private String getUrlParameters() throws FixerException {

		// Are mandatory parameters present?
		for (EndpointField eParam : authorizedEndpointParameters) {
			if (eParam.isMandatory()) {
				if (CollectionUtils.isEmpty(requestedEndpointParameters) || !requestedEndpointParameters.contains(eParam)) {
					// Validate.notNull(eParam.getName(), ERR_NOT_NULL, eParam.getName());
					throw new FixerException("Parameter '" + eParam.getName() + "' is mandatory");
				}
			}
		}

		// Build the URL parameters string
		String urlParameters = endpointPath;
		String paramSeparator = "?";
		boolean first = true;
		for (EndpointField actualParameter : requestedEndpointParameters) {
			if (!authorizedEndpointParameters.contains(actualParameter)) {
				// Validate.notNull(actualParameter.getName(), ERR_NOT_NULL,
				// actualParameter.getName());
				throw new FixerException("Parameter '" + actualParameter.getName() + "' is not part of the endpoint's parameters");
			}
			if (actualParameter.isInUrlParameter()) {
				urlParameters = urlParameters + paramSeparator + actualParameter.getName() + "=" + actualParameter.getValue();
			} else {
				urlParameters = actualParameter.getValue() + urlParameters;
			}
			if (first) {
				first = false;
				paramSeparator = "&";
			}
		}
		return urlParameters;
	}

	/**
	 * Get the response body from a URL. Uses Apache HttpClient
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws FixerException 
	 */
	private static String getResponse(String url) throws FixerException {
		String responseBody = "{}";
		try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
			HttpGet httpGet = new HttpGet(url);
			HttpResponse resp = client.execute(httpGet);
			ResponseHandler<String> handler = new BasicResponseHandler();
			try {
				responseBody = handler.handleResponse(resp);
			} catch (HttpResponseException e) {
				String msg = "ERROR: '" + e.getStatusCode() + " " + e.getLocalizedMessage() + "' when loading URL: " + url;
				throw new FixerException(e.getStatusCode(), "http_error", msg);
			}
		} catch (IOException e) {
			throw new FixerException(e.getLocalizedMessage());
		}
		return responseBody;
	}

	/**
	 * Calls the Fixer API web service and retrieves data
	 * 
	 * @return list of data
	 * 
	 * @throws JsonParseException
	 * @throws IOException
	 * @throws FixerException
	 * 
	 * @See EndpointFieldList
	 */
	public EndpointFieldList getData() throws JsonParseException, IOException, FixerException {
		String url = getRequestUrl();
		if (StringUtils.isNotBlank(url)) {
			JsonFactory factory = new JsonFactory();
			this.jsonResponse = getResponse(url);
			JsonParser parser = factory.createParser(this.jsonResponse);
			EndpointFieldList data = analyzeData(parser);
			if (data.isSuccess()) {
				return data;
			} else {
				EndpointField codeFld = data.getField("code");
				if (codeFld != null) {
					String code = data.getField("code").getValue();
					EndpointField typeFld = data.getField("type");
					EndpointField infoFld = data.getField("info");
					String errType = "Unknown error type";
					if (typeFld != null && StringUtils.isNotBlank(typeFld.getValue())) {
						errType = typeFld.getValue();
					}
					String errMsg = errType;
					if (infoFld != null && StringUtils.isNotBlank(infoFld.getValue())) {
						errMsg = infoFld.getValue();
					}
					throw new FixerException(code, errType, errMsg);
				} else {
					throw new FixerException(0, "unknown_error_type", "Failed to load data, unknown error");
				}
			}
		} else {
			throw new FixerException("No URL defined for this endpoint");
		}
	}

	/**
	 * Analyzes the result of a request. 
	 * 
	 * @param parser
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 * 
	 * @See EndpointFieldList
	 */
	protected EndpointFieldList analyzeData(JsonParser parser) throws JsonParseException, IOException {

		EndpointFieldList fields = new EndpointFieldList();

		Map<Integer, List<String>> fieldsBoolean = new HashMap<>();
		fieldsBoolean.put(EndpointField.TYPE_BOOL, Arrays.asList(new String[] { "success", "historical", "fluctuation", "timeseries" }));
		Map<Integer, List<String>> fieldsDate = new HashMap<>();
		fieldsDate.put(EndpointField.TYPE_DAT, Arrays.asList(new String[] { "timestamp" }));
		Map<Integer, List<String>> fieldsInt = new HashMap<>();
		fieldsInt.put(EndpointField.TYPE_INT, Arrays.asList(new String[] { "code" }));
		Map<Integer, List<String>> fieldsDouble = new HashMap<>();
		fieldsDouble.put(EndpointField.TYPE_DBL, Arrays.asList(new String[] { "amount", "result" }));
		Map<Integer, List<String>> fieldsString = new HashMap<>();
		fieldsString.put(EndpointField.TYPE_STR, Arrays.asList(new String[] { "date", "base", "historical", "from", "to", "info", "start_date", "end_date", "type" }));
		List<Map> allFields = Arrays.asList(new Map[] { fieldsBoolean, fieldsDate, fieldsInt, fieldsDouble, fieldsString });

		int level = 0;
		int levelInc = 4;
		boolean structStart = false;
		String parent = "";
		while (!parser.isClosed()) {

			JsonToken jsonToken = parser.nextToken();
			if (jsonToken != null) {
				String spc = StringUtils.repeat(" ", level);
				String fieldName = parser.getCurrentName();

				if (jsonToken.isBoolean() || jsonToken.isNumeric() || jsonToken.isScalarValue()) {
					String fieldValue = parser.getValueAsString();
					boolean fieldFound = false;
					for (Map<Integer, List<String>> mapFields : allFields) {
						Set<Integer> fieldTypes = mapFields.keySet();
						for (Integer fieldType : fieldTypes) {
							List<String> fieldLst = mapFields.get(fieldType);
							if (fieldLst.contains(fieldName) && !StringUtils.equalsIgnoreCase(fieldName, fieldValue)) {
								EndpointField field = new EndpointField(fieldName, fieldValue, fieldType, false);
								fields.addField(field);
								fieldFound = true;
								if (fieldType == EndpointField.TYPE_DAT) {
									/*
									try {
										log.debug("{}Champ date {}: {}", spc, fieldName, field.getDateTime());
									} catch (ParseException e) {
										log.debug("{}ERROR: {}", spc, e.getLocalizedMessage());
									}
									*/
								}
								if (fieldType == EndpointField.TYPE_STR) {
									/*
									try {
										log.debug("{}Champ date {}: {}", spc, fieldName, field.getDate());
									} catch (ParseException e) {
										log.debug("{}ERROR: {}", spc,e.getLocalizedMessage());
									}
									*/
								}
							}
						}
					}
					if (!fieldFound) {
						if ("symbols".equalsIgnoreCase(parent)) {
							Currency currency = new Currency(fieldName, fieldValue);
							fields.addCurrency(currency);
						}

						if (StringUtils.isNotBlank(parent) && parent.startsWith("rates")) {
							String base = (fields.getField("base")).getValue();

							boolean isListOfRates = false;
							EndpointField fldDate = fields.getField("date");
							String strDate = "";
							String targetCurrency = "";
							if (fldDate != null) {
								strDate = fldDate.getValue();
								isListOfRates = true;
							} else {
								if (parent.startsWith("rates/")) {
									strDate = StringUtils.substringAfterLast(parent, "rates/");
									try {
										LocalDate localDate = LocalDate.parse(strDate);
										isListOfRates = true;
									} catch (DateTimeParseException e) {
										targetCurrency = StringUtils.substringAfterLast(parent, "/");
										isListOfRates = false;
									}
								}
							}

							// Applies to: Lates, TimeSeries, Historical
							if (isListOfRates) {
								ExchangeRate exchangeRate = new ExchangeRate();
								exchangeRate.setDate(strDate);
								exchangeRate.setBaseCurrency(base);
								exchangeRate.setTargetCurrency(fieldName);
								EndpointField fldTimestamp = fields.getField("timestamp");
								if (fldTimestamp != null) {
									String timestamp = fldTimestamp.getValue();
									if (StringUtils.isNotBlank(timestamp)) {
										try {
											Long millis = Long.parseLong(timestamp);
											LocalDateTime date = Instant.ofEpochSecond(millis).atZone(ZoneId.systemDefault()).toLocalDateTime(); 
											exchangeRate.setTimestamp(date);
										} catch (NumberFormatException ne) {
											log.debug("Unable to convert timestamp '{}' to millisecondes", timestamp);
										}
									}
								}
								if (jsonToken.isNumeric()) {
									exchangeRate.setRate(JsonParseUtils.parseDouble(parser));
								}
								fields.addRate(exchangeRate);
							} else {
								// Applies to Fluctuations
								Fluctuation fluctuation = new Fluctuation();
								fluctuation.setBaseCurrency(fields.getField("base").getValue());
								fluctuation.setTargetCurrency(targetCurrency);
								fluctuation.setStartDate(fields.getField("start_date").getValue());
								fluctuation.setEndDate(fields.getField("end_date").getValue());
								fluctuation.setStartRate(JsonParseUtils.parseDouble(parser));

								int cnt = 0;
								for (int i = 0; i < 6; i++) {
									jsonToken = parser.nextToken();
									fieldName = parser.getCurrentName();
									if (StringUtils.isNotBlank(fieldName) && !fieldName.equalsIgnoreCase(parser.getText())) {
										if ("end_rate".equalsIgnoreCase(fieldName)) {
											fluctuation.setEndRate(JsonParseUtils.parseDouble(parser));
											cnt++;
										}
										if ("change".equalsIgnoreCase(fieldName)) {
											fluctuation.setChange(JsonParseUtils.parseDouble(parser));
											cnt++;
										}
										if ("change_pct".equalsIgnoreCase(fieldName)) {
											fluctuation.setChangePct(JsonParseUtils.parseDouble(parser));
											cnt++;
										}
									}
									if (cnt == 3) {
										break;
									}
								}
								fields.addFluctuation(fluctuation);
							}
						}
					}
				}

				if (jsonToken.isStructStart()) {
					structStart = true;
					level = level + levelInc;

					if (StringUtils.isNotBlank(fieldName)) {
						try {
							LocalDate lDate = LocalDate.parse(fieldName, EndpointField.dateFormatter);
						} catch (DateTimeParseException de) {

						}
					}

					if (StringUtils.isBlank(parent)) {
						parent = fieldName;
					} else {
						parent = parent + "/" + fieldName;
					}

				} else if (jsonToken.isStructEnd()) {
					structStart = false;
					level = level - levelInc;
					if (StringUtils.isNotBlank(parent)) {
						if (parent.contains("/")) {
							parent = StringUtils.substringBeforeLast(parent, "/");
						} else {
							parent = "";
						}
					}
				}
			}
		}
		return fields;
	}

	public Set<EndpointField> getRequestedEndpointParameters() {
		return requestedEndpointParameters;
	}

	public void setRequestedEndpointParameters(Set<EndpointField> actualEndpointParameters) {
		this.requestedEndpointParameters = actualEndpointParameters;
	}

	public Set<EndpointField> getAuthorizedendpointparameters() {
		return authorizedEndpointParameters;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getJsonResponse() {
		return jsonResponse;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

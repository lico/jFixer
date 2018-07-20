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
package com.upandcoding.fixer.endpoint.field;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.upandcoding.fixer.model.Currency;
import com.upandcoding.fixer.model.ExchangeRate;
import com.upandcoding.fixer.model.Fluctuation;

/**
 * Holds the response data after a call to a fixer endpoint
 * <p>
 * Contains the following properties:
 * <ul>
 * <li>fields: list of EndpointField that contain the metadata of the response</li>
 * <li>rates: list of exchange rates sent through the response (for Latest and Historical endpoint)</li>
 * <li>currencies: list of available currencies (for Supported Symbols enpoint only)</li>
 * <li>fluctuations: list of rates fluctuations (for Fluctuations endpoint only)</li>
 * </ul>
 * 
 * @See EndpointField
 * @See ExchangeRate
 * @See Currency
 * 
 * @author Lionel Conforto
 *
 */
public class EndpointFieldList {

	private static final Logger log = LoggerFactory.getLogger(EndpointFieldList.class);

	private List<EndpointField> fields = new ArrayList<>();

	private List<ExchangeRate> rates = new ArrayList<>();

	private List<Currency> currencies = new ArrayList<>();

	private List<Fluctuation> fluctuations = new ArrayList<>();

	public boolean isSuccess() {
		EndpointField successFld = getField("success");
		return (successFld != null && "true".equalsIgnoreCase(successFld.getValue()));
	}

	public List<EndpointField> getFields() {
		return fields;
	}

	public void setFields(List<EndpointField> fields) {
		this.fields = fields;
	}

	public List<ExchangeRate> getRates() {
		return rates;
	}

	public void setRates(List<ExchangeRate> rates) {
		this.rates = rates;
	}

	public List<Currency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(List<Currency> currencies) {
		this.currencies = currencies;
	}

	public List<Fluctuation> getFluctuations() {
		return fluctuations;
	}

	public void setFluctuations(List<Fluctuation> fluctuations) {
		this.fluctuations = fluctuations;
	}

	public void addField(EndpointField field) {
		this.fields.add(field);
	}

	public void addRate(ExchangeRate rate) {
		this.rates.add(rate);
	}

	public void addCurrency(Currency currency) {
		this.currencies.add(currency);
	}

	public void addFluctuation(Fluctuation fluctuation) {
		this.fluctuations.add(fluctuation);
	}

	public EndpointField getField(String fieldName) {
		EndpointField fieldFound = null;
		if (StringUtils.isNotBlank(fieldName) && CollectionUtils.isNotEmpty(fields)) {
			for (EndpointField field : fields) {
				if (fieldName.equalsIgnoreCase(field.getName())) {
					fieldFound = field;
					break;
				}
			}
		}
		return fieldFound;
	}

}

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
package com.upandcoding.fixer.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a currency with two properties
 * <ul>
 * <li>symbol: must be a valid 3-digits ISO code</li>
 * <li>displayName: the english description as provided by the Fixer API</li>
 * </ul>
 * Also note that the class contains a static list of currencies available at Fixer API.
 * <p>
 * List<Currency> currencies = Currency.getSupportedcurrencies();
 * <p>
 * Not based on java.util.Currency because this latter comes with a pre-filled
 * list of available currencies What we want here is to get the list of
 * currencies actually supported by the Fixer API
 * 
 * @author Lionel Conforto
 *
 */
public class Currency {

	private static final Logger log = LoggerFactory.getLogger(Currency.class);

	private static final List<Currency> supportedCurrencies = new ArrayList<>();

	private String symbol;
	private String displayName;

	public Currency() {

	}

	public Currency(String symbol, String displayName) {
		this.symbol = symbol;
		this.displayName = displayName;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getDisplayName(Locale locale) {
		String localName = this.displayName;
		if (StringUtils.isNotBlank(this.symbol)) {
			try {
				java.util.Currency javaCurrency = java.util.Currency.getInstance(this.symbol);
				localName = javaCurrency.getDisplayName(locale);
			} catch (IllegalArgumentException ie) {
				localName = this.displayName;
			}
		}
		return localName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public static void setSupportedCurrencies(List<Currency> currencies) {
		if ( CollectionUtils.isNotEmpty(currencies)) {
			supportedCurrencies.clear();
			for (Currency currency : currencies) {
				supportedCurrencies.add(currency);
			}
		}
	}

	public static List<Currency> getSupportedcurrencies() {
		return supportedCurrencies;
	}

	@Override
	public int hashCode() {
		return Objects.hash(symbol, displayName);
	}

	public boolean equals(Object obj) {
		if (obj == null || obj == this || obj.getClass() != getClass()) {
			return false;
		} else {
			Currency currency = (Currency) obj;
			return StringUtils.equalsIgnoreCase(this.symbol, currency.symbol) &&
					StringUtils.equalsIgnoreCase(this.displayName, currency.displayName);
		}
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

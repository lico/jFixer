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

import java.time.LocalDateTime;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
*
* Exchange rate data for a date and a couple of base and a target currencies.
* <p>
* <ul>
* <li>date format must be yyyy-MM-dd, eg 2018-05-26</li>
* <li>baseCurrency must be a valid 3-digits ISO code</li>
* <li>targetCurrency must be a valid 3-digits ISO code</li>
* <li>rate: the actual exchange rate that is how much of targetCurrency for one baseCurrency</li>
* </ul>
* 
* @author Lionel Conforto
*
*/
public class ExchangeRate {

	private static final Logger log = LoggerFactory.getLogger(ExchangeRate.class);

	private String date;
	private String baseCurrency;
	private String targetCurrency;
	private double rate;
	private LocalDateTime timestamp;

	public ExchangeRate() {
	}

	public ExchangeRate(String baseCurrency, String targetCurrency, double rate, String date, LocalDateTime timestamp) {
		this.baseCurrency = baseCurrency;
		this.targetCurrency = targetCurrency;
		this.rate = rate;
		this.date = date;
		this.timestamp = timestamp;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public String getTargetCurrency() {
		return targetCurrency;
	}

	public void setTargetCurrency(String currency) {
		this.targetCurrency = currency;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		return Objects.hash(baseCurrency, targetCurrency, date, rate);
	}

	public boolean equals(Object obj) {
		if (obj == null || obj == this || obj.getClass() != getClass()) {
			return false;
		} else {
			ExchangeRate exchangeRate = (ExchangeRate) obj;
			return StringUtils.equalsIgnoreCase(this.baseCurrency, exchangeRate.baseCurrency) &&
					StringUtils.equalsIgnoreCase(this.targetCurrency, exchangeRate.targetCurrency) &&
					StringUtils.equalsIgnoreCase(this.date, exchangeRate.date) &&
					this.rate == exchangeRate.rate;
		}
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

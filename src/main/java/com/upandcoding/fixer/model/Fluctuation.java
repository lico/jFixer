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
* Fluctuation of a currency between two dates
* 
* @author Lionel Conforto
*
*/
public class Fluctuation {

	private static final Logger log = LoggerFactory.getLogger(Fluctuation.class);

	private String startDate;
	private String endDate;
	private String baseCurrency;
	private String targetCurrency;
	private double startRate;
	private double endRate;
	private double change; // startRate-endRate
	private double changePct; // change in percentage

	public Fluctuation() {
	}

	public Fluctuation(String startDate, String endDate, String baseCurrency, String targetCurrency, double startRate, double endRate, double change, double changePct) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.baseCurrency = baseCurrency;
		this.targetCurrency = targetCurrency;
		this.startRate = startRate;
		this.endRate = endRate;
		this.change = change;
		this.changePct = changePct;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
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

	public void setTargetCurrency(String targetCurrency) {
		this.targetCurrency = targetCurrency;
	}

	public double getStartRate() {
		return startRate;
	}

	public void setStartRate(double startRate) {
		this.startRate = startRate;
	}

	public double getEndRate() {
		return endRate;
	}

	public void setEndRate(double endRate) {
		this.endRate = endRate;
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}

	public double getChangePct() {
		return changePct;
	}

	public void setChangePct(double changePct) {
		this.changePct = changePct;
	}

	@Override
	public int hashCode() {
		return Objects.hash(baseCurrency, targetCurrency, startDate, endDate);
	}

	public boolean equals(Object obj) {
		if (obj == null || obj == this || obj.getClass() != getClass()) {
			return false;
		} else {
			Fluctuation exchangeRate = (Fluctuation) obj;
			return StringUtils.equalsIgnoreCase(this.baseCurrency, exchangeRate.baseCurrency) &&
					StringUtils.equalsIgnoreCase(this.targetCurrency, exchangeRate.targetCurrency) &&
					StringUtils.equalsIgnoreCase(this.startDate, exchangeRate.startDate) &&
					StringUtils.equalsIgnoreCase(this.endDate, exchangeRate.endDate) &&
					this.startRate == exchangeRate.startRate &&
					this.endRate == exchangeRate.endRate;
		}
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

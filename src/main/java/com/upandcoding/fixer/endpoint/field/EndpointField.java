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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an input or output data of the Fixer API, such as for example the base currency.
 * <ul>
 * <li>name: must match a Fixer API parameter</li>
 * <li>value: a text value</li>
 * <li>type: can be string, numeric, date, list, integer, double or boolean</li>
 * <li>inUrlParameter: when calling the Fixer API, the parameter can be in the query string 
 *     (after ?) typically like &param=value, or it can be in the URL path like for
 *     historical endpoint : /2013-12-24</li>
 * <li>If the field is mandatory an exception is thrown at runtime in case it is missing</li>
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
public class EndpointField {

	private static final Logger log = LoggerFactory.getLogger(EndpointField.class);

	public static final int TYPE_STR = 0;
	public static final int TYPE_NUM = 1;
	public static final int TYPE_DAT = 2;
	public static final int TYPE_LST = 3;
	public static final int TYPE_INT = 4;
	public static final int TYPE_DBL = 5;
	public static final int TYPE_BOOL = 6;
	
	public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


	private String name;
	private String value;
	private int type = TYPE_STR;
	private boolean inUrlParameter = true; // if true, is appended to the url parameter like &name=value, if false, is
											// added to the url like https://data.fixer.io/api/value
	private boolean mandatory = false;

	public EndpointField() {

	}

	public EndpointField(String name, String value) {
		this.name = name;
		this.value = value;
		this.type = TYPE_STR;
		this.mandatory = false;
	}

	public EndpointField(String name, int type, boolean mandatory) {
		this.name = name;
		this.type = type;
		this.mandatory = mandatory;
	}

	public EndpointField(String name, String value, int type, boolean mandatory) {
		this.name = name;
		this.value = value;
		this.type = type;
		this.mandatory = mandatory;
	}

	public int getInt() throws NumberFormatException {
		return Integer.parseInt(this.value);
	}
	
	public double getDouble() throws NumberFormatException {
		return Double.parseDouble(this.value);
	}

	public Date getDate() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.parse(this.value);
	}
	
	public LocalDateTime getDateTime() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss[Z]");
		Long timestamp = Long.parseLong(this.value);
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
	}

	public List<String> getList() {
		return new ArrayList<String>(Arrays.asList((this.value).split(",")));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isInUrlParameter() {
		return inUrlParameter;
	}

	public void setInUrlParameter(boolean inUrlParameter) {
		this.inUrlParameter = inUrlParameter;
	}

	@Override
	public int hashCode() {
		if (this.name != null) {
			return this.name.hashCode();
		} else {
			return super.hashCode();
		}
	}

	public boolean equals(Object obj) {
		if (obj == null || obj == this || obj.getClass() != getClass()) {
			return false;
		} else {
			EndpointField eParameter = (EndpointField) obj;
			return StringUtils.equalsIgnoreCase(this.name, eParameter.name);
		}
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}

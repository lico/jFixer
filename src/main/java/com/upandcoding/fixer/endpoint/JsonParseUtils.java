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
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class JsonParseUtils {

	/**
	 * Parses an integer value from the current token of a Json parser
	 * <p>
	 * Does not only retrieve values identified as integer by the parser, but also integer in double values
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 */
	public static int parseInteger(JsonParser parser) throws IOException {
		JsonToken token = parser.currentToken();
		if (token == JsonToken.VALUE_NUMBER_INT) {
			return parser.getIntValue();
		}
		BigDecimal value = new BigDecimal(parser.getTextCharacters(), parser.getTextOffset(), parser.getTextLength());
		return value.intValueExact();
	}

	/**
	 * Parses a boolean value from the current token of a Json parser
	 * <p>
	 * Does not only parse token identified as isBoolean but also text values.
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 */
	public static boolean parseBool(JsonParser parser) throws IOException {
		JsonToken token = parser.currentToken();
		if (token.isBoolean()) {
			return parser.getBooleanValue();
		} else {
			String json = parser.getText();
			if (json.equals("true")) {
				return true;
			} else if (json.equals("false")) {
				return false;
			} else {
				return false;
			}
		}
	}

	/**
	 * Parses a double value from the current token of a Json parser
	 * <p>
	 * Takes also in consideration the cases where the value is infinite or NaN.
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 */
	public static double parseDouble(JsonParser parser) throws IOException {

		BigDecimal almostOne = new BigDecimal(String.valueOf(1.0 + 1e-6));
		BigDecimal maxValue = new BigDecimal(String.valueOf(Double.MAX_VALUE)).multiply(almostOne);
		BigDecimal minValue = new BigDecimal(String.valueOf(-Double.MAX_VALUE)).multiply(almostOne);

		JsonToken current = parser.currentToken();
		if (!current.isNumeric()) {
			String json = parser.getText();
			if (json.equals("NaN")) {
				return Double.NaN;
			} else if (json.equals("Infinity")) {
				return Double.POSITIVE_INFINITY;
			} else if (json.equals("-Infinity")) {
				return Double.NEGATIVE_INFINITY;
			}
		}
		BigDecimal value = new BigDecimal(parser.getTextCharacters(), parser.getTextOffset(), parser.getTextLength());
		if (value.compareTo(maxValue) > 0 || value.compareTo(minValue) < 0) {
			throw new IllegalArgumentException("Double value out of range: " + parser.getText());
		}
		return value.doubleValue();
	}

}

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

@SuppressWarnings("serial")
public class FixerException extends Exception {

	private int fixerCode = 0;
	private String fixerType;

	public FixerException() {
		super();
	}

	public FixerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FixerException(String message, Throwable cause) {
		super(message, cause);
	}

	public FixerException(int code, String type, String info) {
		super(info);
		this.fixerCode = code;
		this.fixerType = type;
	}

	public FixerException(String codeStr, String type, String info) {
		super(info);
		int code;
		try {
			code = Integer.parseInt(codeStr);
		} catch (NumberFormatException nfe) {
			code = 1000;
		}
		this.fixerCode = code;
		this.fixerType = type;
	}

	public FixerException(String message) {
		super(message);
	}

	public FixerException(Throwable cause) {
		super(cause);
	}

	public int getFixerCode() {
		return fixerCode;
	}

	public String getFixerType() {
		return fixerType;
	}

}

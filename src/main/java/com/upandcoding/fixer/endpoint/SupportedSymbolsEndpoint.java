package com.upandcoding.fixer.endpoint;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.upandcoding.fixer.endpoint.field.EndpointField;

public class SupportedSymbolsEndpoint extends Endpoint {

	private static final Logger log = LoggerFactory.getLogger(SupportedSymbolsEndpoint.class);

	private void init() {
		endpointPath = "symbols";
		if (CollectionUtils.isNotEmpty(authorizedEndpointParameters)) {
			authorizedEndpointParameters.clear();
		}
		authorizedEndpointParameters.add(new EndpointField("access_key", EndpointField.TYPE_STR, true));
	}

	public SupportedSymbolsEndpoint() {
		init();
	}

	public SupportedSymbolsEndpoint(String baseUrl) {
		this.baseUrl = baseUrl;
		init();
	}

}

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

}

package com.upandcoding.fixer.endpoint;


import org.apache.commons.collections4.CollectionUtils;

import com.upandcoding.fixer.endpoint.field.EndpointField; 

public class ConvertEndpoint extends Endpoint {

	private void init() {
		endpointPath = "convert";
		if (CollectionUtils.isNotEmpty(authorizedEndpointParameters)) {
			authorizedEndpointParameters.clear();
		}
		authorizedEndpointParameters.add(new EndpointField("access_key", EndpointField.TYPE_STR, true));
		authorizedEndpointParameters.add(new EndpointField("from", EndpointField.TYPE_STR, true));
		authorizedEndpointParameters.add(new EndpointField("to", EndpointField.TYPE_STR, true));
		authorizedEndpointParameters.add(new EndpointField("amount", EndpointField.TYPE_STR, true));
		authorizedEndpointParameters.add(new EndpointField("date", EndpointField.TYPE_DAT, false));
		authorizedEndpointParameters.add(new EndpointField("base", EndpointField.TYPE_DAT, false));
	}

	public ConvertEndpoint() {
		init();
	}

	public ConvertEndpoint(String baseUrl) {
		this.baseUrl = baseUrl;
		init();
	}
}

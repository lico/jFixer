package com.upandcoding.fixer.endpoint;

import org.apache.commons.collections4.CollectionUtils;

import com.upandcoding.fixer.endpoint.field.EndpointField;

public class FluctuationEndpoint extends Endpoint {

	private void init() {
		endpointPath = "fluctuation";
		if (CollectionUtils.isNotEmpty(authorizedEndpointParameters)) {
			authorizedEndpointParameters.clear();
		}
		authorizedEndpointParameters.add(new EndpointField("access_key", EndpointField.TYPE_STR, true));
		authorizedEndpointParameters.add(new EndpointField("start_date", EndpointField.TYPE_DAT, true));
		authorizedEndpointParameters.add(new EndpointField("end_date", EndpointField.TYPE_DAT, true));
		authorizedEndpointParameters.add(new EndpointField("base", EndpointField.TYPE_STR, false));
		authorizedEndpointParameters.add(new EndpointField("symbols", EndpointField.TYPE_LST, false));
	} 

	public FluctuationEndpoint() {
		init();
	}

	public FluctuationEndpoint(String baseUrl) {
		this.baseUrl = baseUrl;
		init();
	}
}

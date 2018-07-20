package com.upandcoding.fixer.endpoint;

import org.apache.commons.collections4.CollectionUtils;

import com.upandcoding.fixer.endpoint.field.EndpointField;

public class HistoricalEndpoint extends Endpoint {

	private void init() {
		endpointPath = "";
		if (CollectionUtils.isNotEmpty(authorizedEndpointParameters)) {
			authorizedEndpointParameters.clear();
		}
		authorizedEndpointParameters.add(new EndpointField("access_key", EndpointField.TYPE_STR, true));
		EndpointField dateParam = new EndpointField("date", EndpointField.TYPE_DAT, true);
		dateParam.setInUrlParameter(false);
		authorizedEndpointParameters.add(dateParam);
		authorizedEndpointParameters.add(new EndpointField("base", EndpointField.TYPE_STR, false));
		authorizedEndpointParameters.add(new EndpointField("symbols", EndpointField.TYPE_LST, false));
	}
	
	public HistoricalEndpoint() {
		init();
	}

	public HistoricalEndpoint(String baseUrl) {
		this.baseUrl = baseUrl;
		init();
	}
}

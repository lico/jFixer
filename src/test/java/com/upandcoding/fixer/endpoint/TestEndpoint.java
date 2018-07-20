package com.upandcoding.fixer.endpoint;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.upandcoding.fixer.FixerException;
import com.upandcoding.tests.config.TestConfig;

import junit.framework.Assert;

public class TestEndpoint {

	private static final Logger log = LoggerFactory.getLogger(TestEndpoint.class);

	@Test
	public void testGetUrl() throws FixerException {
		// With mandatory parameters
		Endpoint latest2 = new LatestEndpoint(TestConfig.baseUrl);
		latest2.addParam("access_key", TestConfig.accessKey);
		String url2 = latest2.getRequestUrl();
		String expected2 = TestConfig.baseUrl + "/latest?access_key=" + TestConfig.accessKey;
		log.debug("url2: {}", url2);
		log.debug("expected2: {}", expected2);
		Assert.assertEquals(expected2, url2); 

		// With optional parameters
		Endpoint latest1 = new LatestEndpoint(TestConfig.baseUrl);
		latest1.addParam("access_key", TestConfig.accessKey);
		latest1.addParam("base", TestConfig.baseCurrency);
		String url1 = latest1.getRequestUrl();
		String expected1 = TestConfig.baseUrl + "/latest?access_key=" + TestConfig.accessKey + "&base=" + TestConfig.baseCurrency;
		Assert.assertEquals(expected1, url1);
	}

	@Test(expected = FixerException.class)
	public void testGetUrlError() throws FixerException {
		Endpoint latest3 = new LatestEndpoint(TestConfig.baseUrl);
		latest3.addParam("access_key", TestConfig.accessKey);
		latest3.addParam("toto", "toto");
		log.debug(latest3.toString());
		String url3 = latest3.getRequestUrl();
		log.debug("url3: {}", url3);
	}

	@Test(expected = FixerException.class)
	public void testGetUrlError2() throws FixerException {
		Endpoint latest3 = new LatestEndpoint(TestConfig.baseUrl);
		log.debug(latest3.toString());
		String url3 = latest3.getRequestUrl();
	}

	@Test(expected = FixerException.class)
	public void testGetUrlError3() throws FixerException {
		Endpoint latest4 = new LatestEndpoint(TestConfig.baseUrl);
		latest4.addParam("base", TestConfig.baseCurrency);
		String url4 = latest4.getRequestUrl();
	}

}

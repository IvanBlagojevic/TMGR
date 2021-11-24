package org.gs4tr.termmanager.tests.controllers.rest.main;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class RestUtils {

    private static RestTemplate restTemplate = new RestTemplate();

    public static <T> ResponseEntity<T> performExcange(String requestAddress, HttpMethod method,
	    HttpEntity<T> requestEntity, Class<T> resultClass) {
	ResponseEntity<T> result = null;
	try {
	    result = restTemplate.exchange(new URI(requestAddress), method, requestEntity, resultClass);
	} catch (HttpServerErrorException e) {
	    throw new RuntimeException(e.getResponseBodyAsString(), e);
	} catch (RestClientException e) {
	    throw new RuntimeException(e.getMessage());
	} catch (URISyntaxException e) {
	    throw new RuntimeException("Request URL could not be parsed as a URI reference.", e);
	}
	return result;
    }

    public static <T> T performGetForObject(String requestAddress, Class<T> resultClass) {

	T result = null;

	try {
	    result = restTemplate.getForObject(new URI(requestAddress), resultClass);
	} catch (HttpServerErrorException e) {
	    throw new RuntimeException(e.getResponseBodyAsString(), e);
	} catch (RestClientException e) {
	    throw new RuntimeException(e.getMessage());
	} catch (URISyntaxException e) {
	    throw new RuntimeException("Request URL could not be parsed as a URI reference.", e);
	}

	return result;
    }

    public static String performGetForPlainText(String requestAddress) {

	return performGetForObject(requestAddress, String.class);
    }

    public static <T> T performPostForObject(String requestAddress, HttpEntity<?> httpEntity,
	    Map<String, Object> attributes, Class<T> resultClass) {

	T result = null;

	try {
	    if (attributes == null) {
		result = restTemplate.postForObject(new URI(requestAddress), httpEntity, resultClass);
	    } else {
		result = restTemplate.postForObject(requestAddress, httpEntity, resultClass, attributes);
	    }
	    if (result == null) {
		System.err.println("Cannot retrieve Representation from GET response.");
	    }
	} catch (RestClientException e) {
	    throw new RuntimeException(e.getMessage());
	} catch (URISyntaxException e) {
	    throw new RuntimeException("Request URL could not be parsed as a URI reference.", e);
	}

	return result;
    }

    public static String performPostForPlainText(String requestAddress) {

	return performPostForObject(requestAddress, null, null, String.class);
    }

    public static String performPostForPlainText(String requestAddress, HttpEntity<?> httpEntity) {

	return performPostForObject(requestAddress, httpEntity, null, String.class);
    }

    public static String performPostForPlainText(String requestAddress, HttpEntity<?> httpEntity,
	    Map<String, Object> attributes) {

	return performPostForObject(requestAddress, httpEntity, attributes, String.class);
    }
}
/**
 * Tiny Rest Client for Facebook
 * @author Carmen Delessio
 * carmendelessio AT gmail DOT com
 * http://www.socialjava.com
 * created March 30, 2009
 *
 **/


package com.socialjava;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.uri.UriComponent;

public class TinyFBClient {
	private static final String DEFAULT_API_VERSION = "1.0";
	private static final String DEFAULT_RESPONSE_FORMAT = "JSON";
	private static final String FACEBOOK_REST_SERVER_URL = "http://api.facebook.com/restserver.php";

	private TreeMap<String, String> queryParameters = new TreeMap<String, String>();
	private Client restClient;

	public TinyFBClient() {
		this.queryParameters.put("v", DEFAULT_API_VERSION);
		this.queryParameters.put("format", DEFAULT_RESPONSE_FORMAT);
		this.restClient = Client.create();
	}

	public TinyFBClient(String facebookApiKey, String facebookSecretKey) {
		this();
		this.queryParameters.put("secret_key", facebookSecretKey);
		this.queryParameters.put("api_key", facebookApiKey);
	}

	public TinyFBClient(String facebookApiKey, String facebookSecretKey,
			String sessionKey) {
		this(facebookApiKey, facebookSecretKey);
		this.queryParameters.put("session_key", sessionKey);
	}

	public TinyFBClient(String facebookApiKey, String facebookSecretKey,
			String sessionKey, String apiVersion, String responseFormat) {
		this(facebookApiKey, facebookSecretKey, sessionKey);
		this.queryParameters.put("v", apiVersion);
		this.queryParameters.put("format", responseFormat);

	}

	public void setRequestParms(TreeMap<String, String> parms) {
		this.queryParameters.putAll(parms);
	}
	
		public ClientResponse getResponse(TreeMap<String, String> params) {
		String signatureValue = ""; // String used for creating signature
		String encodedParm;
		
		this.queryParameters.putAll(params);
		this.queryParameters.put("call_id", String.valueOf(System.currentTimeMillis()));

		UriBuilder uriBuilder = UriBuilder.fromUri(FACEBOOK_REST_SERVER_URL);
		for (String key : this.queryParameters.keySet()) { 
			String value = this.queryParameters.get(key);
			signatureValue += key + "=" + value;
			// TODO should be a &&
			if ((value.indexOf("{") >= 0)
					|| (value.indexOf("}") >= 0)) { // if passing JSON
															// Array, encode the
															// {}
				encodedParm = UriComponent.contextualEncode(value, UriComponent.Type.QUERY_PARAM, false);
				uriBuilder.queryParam(key, encodedParm);
			} else {
				uriBuilder.queryParam(key, value);
			}
			
		}

		String signature = generateSignature(signatureValue, this.queryParameters.get("secret_key"));

		uriBuilder.queryParam("sig", signature);
		
		URI uri = uriBuilder.build();
		
		WebResource resource = this.restClient.resource(uri);
		
		ClientResponse restResponse = resource.get(ClientResponse.class);
		return restResponse;

	}


	public ClientResponse getResponse(String method,
			TreeMap<String, String> params) {
		params.put("method", method);
		return getResponse(params);
	}

	public String call(TreeMap<String, String> params) {
		return getResponse(params).getEntity(String.class);
	}

	public String call(String method, TreeMap<String, String> params) {
		params.put("method", method);
		return call(params);
	}

	public String generateSignature(String requestString, String secretKey) {
		requestString = requestString + secretKey;
		StringBuilder result = new StringBuilder();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			for (byte b : md.digest(requestString.toString().getBytes())) {
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0x0f));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			System.console.println("No MD5 algorithm!");
			return null;
		}
	}

}
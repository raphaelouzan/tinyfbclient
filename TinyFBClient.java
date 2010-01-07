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

import com.allen_sauer.gwt.log.client.Log;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.uri.UriComponent;

/**
 * @note Not well designed
 * @author raphaelouzan
 * 
 */
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
		StringBuilder queryValueToSign = new StringBuilder();

		this.queryParameters.putAll(params);
		this.queryParameters.put("call_id", String.valueOf(System
				.currentTimeMillis()));

		UriBuilder uriBuilder = UriBuilder.fromUri(FACEBOOK_REST_SERVER_URL);
		
		for (String key : this.queryParameters.keySet()) {
			String value = this.queryParameters.get(key);
			queryValueToSign.append(key);
			queryValueToSign.append("=");
			queryValueToSign.append(value);
			uriBuilder.queryParam(key, UriComponent.contextualEncode(value,
					UriComponent.Type.QUERY_PARAM, false));
		}

		String signature = generateSignature(queryValueToSign.toString(),
				this.queryParameters.get("secret_key"));

		// Adding the signature
		uriBuilder.queryParam("sig", signature);

		// Making the call
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
			System.out.println("No MD5 algorithm!", e);
			return null;
		}
	}

}

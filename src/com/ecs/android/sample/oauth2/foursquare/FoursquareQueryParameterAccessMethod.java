package com.ecs.android.sample.oauth2.foursquare;

import java.io.IOException;

import com.google.api.client.auth.oauth2.Credential.AccessMethod;
import com.google.api.client.http.HttpRequest;

  /**
   * Immutable and thread-safe OAuth 2.0 method for accessing protected resources using the <a
   * href="http://tools.ietf.org/html/rfc6750#section-2.3">URI Query Parameter</a>.
   * 
   * Note that foursqaure uses a parameter called oauth_token instead of access_token, hence we need
   * a specific AccessMethod for Foursquare.
   * 
   * For info on https://developer.foursquare.com/overview/auth
   * 
   */
  public final class FoursquareQueryParameterAccessMethod implements AccessMethod {

	private static final String PARAM_NAME = "oauth_token";

	private static final FoursquareQueryParameterAccessMethod instance = new FoursquareQueryParameterAccessMethod();
	
	private FoursquareQueryParameterAccessMethod() {
    }
	
	public static FoursquareQueryParameterAccessMethod getInstance() {
		return instance;
	}

    public void intercept(HttpRequest request, String accessToken) throws IOException {
      request.getUrl().set(PARAM_NAME, accessToken);
    }

    public String getAccessTokenFromRequest(HttpRequest request) {
      Object param = request.getUrl().get(PARAM_NAME);
      return param == null ? null : param.toString();
    }
  }
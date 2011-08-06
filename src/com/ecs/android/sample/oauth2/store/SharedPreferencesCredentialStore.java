package com.ecs.android.sample.oauth2.store;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;

public class SharedPreferencesCredentialStore implements CredentialStore {

	private static final String ACCESS_TOKEN = "access_token";
	private static final String EXPIRES_IN = "expires_in";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String SCOPE = "scope";

	private SharedPreferences prefs;
	
	public SharedPreferencesCredentialStore(SharedPreferences prefs) {
		this.prefs = prefs;
	}
	
	@Override
	public AccessTokenResponse read() {
		AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
			accessTokenResponse.accessToken = prefs.getString(ACCESS_TOKEN, "");
			accessTokenResponse.expiresIn = prefs.getLong(EXPIRES_IN, 0);
			accessTokenResponse.refreshToken = prefs.getString(REFRESH_TOKEN, "");
			accessTokenResponse.scope = prefs.getString(SCOPE, "");
		return accessTokenResponse;
	}

	@Override
	public void write(AccessTokenResponse accessTokenResponse) {
		Editor editor = prefs.edit();
		editor.putString(ACCESS_TOKEN,accessTokenResponse.accessToken);
		editor.putLong(EXPIRES_IN,accessTokenResponse.expiresIn);
		editor.putString(REFRESH_TOKEN,accessTokenResponse.refreshToken);
		editor.putString(SCOPE,accessTokenResponse.scope);
		editor.commit();
	}
	
	@Override
	public void clearCredentials() {
		Editor editor = prefs.edit();
		editor.remove(ACCESS_TOKEN);
		editor.remove(EXPIRES_IN);
		editor.remove(REFRESH_TOKEN);
		editor.remove(SCOPE);
		editor.commit();
	}
}

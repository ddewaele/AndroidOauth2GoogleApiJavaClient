package com.ecs.android.sample.oauth2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ecs.android.sample.oauth2.store.CredentialStore;
import com.ecs.android.sample.oauth2.store.SharedPreferencesCredentialStore;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.latitude.Latitude;
import com.google.api.services.latitude.model.LatitudeCurrentlocationResourceJson;

public class LatitudeApiSample extends Activity {

	private SharedPreferences prefs;
	private TextView textView; 
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Button launchOauth = (Button) findViewById(R.id.btn_launch_oauth);
		Button clearCredentials = (Button) findViewById(R.id.btn_clear_credentials);

		this.textView = (TextView) findViewById(R.id.response_code);
		
		// Launch the OAuth flow to get an access token required to do authorized API calls.
		// When the OAuth flow finishes, we redirect to this Activity to perform the API call.
		launchOauth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent().setClass(v.getContext(),OAuthAccessTokenActivity.class));
			}
		});

		// Clearing the credentials and performing an API call to see the unauthorized message.
		clearCredentials.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				clearCredentials();
				performApiCall();
			}

		});
		
		// Performs an authorized API call.
		performApiCall();

	}
	
	/**
	 * Clears our credentials (token and token secret) from the shared preferences.
	 * We also setup the authorizer (without the token).
	 * After this, no more authorized API calls will be possible.
	 */
    private void clearCredentials() {
    	new SharedPreferencesCredentialStore(prefs).clearCredentials();
    }
	
    /**
     * Performs an authorized API call.
     */
	private void performApiCall() {
		try {
			JsonFactory jsonFactory = new JacksonFactory();
			HttpTransport transport = new NetHttpTransport();
			
			CredentialStore credentialStore = new SharedPreferencesCredentialStore(prefs);
			AccessTokenResponse accessTokenResponse = credentialStore.read();
			
			GoogleAccessProtectedResource accessProtectedResource = new GoogleAccessProtectedResource(accessTokenResponse.accessToken,
			        transport,
			        jsonFactory,
			        OAuth2ClientCredentials.CLIENT_ID,
			        OAuth2ClientCredentials.CLIENT_SECRET,
			        accessTokenResponse.refreshToken);
			
		    final Latitude latitude = new Latitude(transport, accessProtectedResource, jsonFactory);
		    latitude.setKey(OAuth2ClientCredentials.API_KEY);
		    
			LatitudeCurrentlocationResourceJson currentLocation = latitude.currentLocation.get().execute();
			String locationAsString = convertLocationToString(currentLocation);
			textView.setText(locationAsString);
		} catch (Exception ex) {
			ex.printStackTrace();
			textView.setText("Error occured : " + ex.getMessage());
		}
	}

	private String convertLocationToString(
			LatitudeCurrentlocationResourceJson currentLocation) {
		String timestampMs = (String) currentLocation.get("timestampMs");
		DateFormat df= new  SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		Date d = new Date(Long.valueOf(timestampMs));
		String locationAsString = "Current location : " + currentLocation.get("latitude") + " - " + currentLocation.get("longitude") + " at " + df.format(d);
		return locationAsString;
	}
	
}
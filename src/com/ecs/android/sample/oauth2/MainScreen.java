package com.ecs.android.sample.oauth2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

public class MainScreen extends Activity {

	private SharedPreferences prefs;
	private TextView txtApiResponse;
	private OAuth2Helper oAuth2Helper;
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		oAuth2Helper = new OAuth2Helper(this.prefs);		

		this.txtApiResponse = (TextView) findViewById(R.id.response_code);
		this.txtApiResponse.setText(R.string.waiting_for_data);
		
		// Performs an authorized API call.
		performApiCall();

	}
    
    /**
     * Performs an authorized API call.
     */
	private void performApiCall() {
		new ApiCallExecutor().execute();
	}
	
	private class ApiCallExecutor extends AsyncTask<Uri, Void, Void> {

		String apiResponse = null;
		
		@Override
		protected Void doInBackground(Uri...params) {
			
			try {
				apiResponse = oAuth2Helper.executeApiCall();
				Log.i(Constants.TAG, "Received response from API : " + apiResponse);
			} catch (Exception ex) {
				ex.printStackTrace();
				apiResponse=ex.getMessage();
			}
            return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			txtApiResponse.setText(apiResponse);
		}

	}
	
}
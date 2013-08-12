package com.ecs.android.sample.oauth2;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.client.auth.oauth2.Credential;

public class IntroScreen extends Activity {

	
	private Timer timer = new Timer();
	private Button btnOAuthGooglePlus;
	private Button btnOAuthGoogleTasks;
	private Button btnOAuthFoursquare;
	private SharedPreferences prefs;
	protected int elapsedTime;
	private Button btnClearGooglePlus;
	private Button btnApiGooglePlus;
	private Button btnClearGoogleTasks;
	private Button btnApiGoogleTasks;
	private Button btnApiFoursquare;
	private Button btnClearFoursquare;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_intro);
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		btnOAuthGooglePlus = (Button)findViewById(R.id.btn_oauth_googleplus);
		btnClearGooglePlus = (Button)findViewById(R.id.btn_clear_googleplus);
		btnApiGooglePlus = (Button)findViewById(R.id.btn_api_googleplus);
		
		btnOAuthGoogleTasks = (Button)findViewById(R.id.btn_oauth_googletasks);
		btnClearGoogleTasks = (Button)findViewById(R.id.btn_clear_googletasks);
		btnApiGoogleTasks = (Button)findViewById(R.id.btn_api_googletasks);
		
		
		btnOAuthFoursquare = (Button)findViewById(R.id.btn_oauth_foursquare);
		btnClearFoursquare = (Button)findViewById(R.id.btn_clear_foursquare);
		btnApiFoursquare = (Button)findViewById(R.id.btn_api_foursquare);
		
		btnOAuthGooglePlus.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Constants.OAUTH2PARAMS = Oauth2Params.GOOGLE_PLUS;
				startActivity(new Intent().setClass(v.getContext(),OAuthAccessTokenActivity.class));
			}
		});
		
		btnClearGooglePlus.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				clearCredentials(Oauth2Params.GOOGLE_PLUS);
			}

		});
		
		btnApiGooglePlus.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Constants.OAUTH2PARAMS = Oauth2Params.GOOGLE_PLUS;
				startActivity(new Intent().setClass(v.getContext(),MainScreen.class));
			}

		});		
		
		btnOAuthGoogleTasks.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Constants.OAUTH2PARAMS = Oauth2Params.GOOGLE_TASKS_OAUTH2;
				startActivity(new Intent().setClass(v.getContext(),OAuthAccessTokenActivity.class));
			}
		});
		
		
		btnClearGoogleTasks.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				clearCredentials(Oauth2Params.GOOGLE_TASKS_OAUTH2);
			}

		});
		
		btnApiGoogleTasks.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Constants.OAUTH2PARAMS = Oauth2Params.GOOGLE_TASKS_OAUTH2;
				startActivity(new Intent().setClass(v.getContext(),MainScreen.class));
			}

		});			
		
		btnOAuthFoursquare.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Constants.OAUTH2PARAMS = Oauth2Params.FOURSQUARE_OAUTH2;
				startActivity(new Intent().setClass(v.getContext(),OAuthAccessTokenActivity.class));
			}
		});
		
		btnClearFoursquare.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				clearCredentials(Oauth2Params.FOURSQUARE_OAUTH2);
			}

		});
		
		btnApiFoursquare.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Constants.OAUTH2PARAMS = Oauth2Params.FOURSQUARE_OAUTH2;
				startActivity(new Intent().setClass(v.getContext(),MainScreen.class));
			}

		});			
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		startTimer();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		stopTimer();
	}

	private void stopTimer() {
		timer.cancel();
	}

	private String getTokenStatusText(Oauth2Params oauth2Params) throws IOException {
		Credential credential = new OAuth2Helper(this.prefs,oauth2Params).loadCredential();
		String output = null;
		if (credential==null || credential.getAccessToken()==null) {
			output = "No access token found.";
		} else if (credential.getExpirationTimeMilliseconds()!=null){
			output = credential.getAccessToken() + "[ " + credential.getExpiresInSeconds() + " seconds remaining]";
		} else {
			output = credential.getAccessToken() + "[does not expire]";
		}
		return output;
	}
	
	protected  void startTimer() {
		System.out.println(" +++++ Started timer");
		timer = new Timer();
	    timer.scheduleAtFixedRate(new TimerTask() {
	        public void run() {
	        	System.out.println(" +++++ running timer");
	        	try {
		            Message msg = new Message();
		            Bundle bundle = new Bundle();
		            bundle.putString("plus", getTokenStatusText(Oauth2Params.GOOGLE_PLUS));
		            bundle.putString("tasks", getTokenStatusText(Oauth2Params.GOOGLE_TASKS_OAUTH2));
		            bundle.putString("foursquare", getTokenStatusText(Oauth2Params.FOURSQUARE_OAUTH2));
		            msg.setData(bundle);
					//mHandler.obtainMessage(1).sendToTarget();
		            mHandler.sendMessage(msg);
		            
	        	} catch (Exception ex) {
	        		ex.printStackTrace();
	        		timer.cancel();
		            Message msg = new Message();
		            Bundle bundle = new Bundle();
		            bundle.putString("plus", ex.getMessage());
		            bundle.putString("tasks", ex.getMessage());
		            bundle.putString("foursquare", ex.getMessage());
		            msg.setData(bundle);
	        		mHandler.sendMessage(msg);
	        	}

	        }
	    }, 0, 1000);
	}

	private static class WeakRefHandler extends Handler {
		    private WeakReference<Activity> ref;
		    public WeakRefHandler(Activity ref) {
		        this.ref = new WeakReference<Activity>(ref);
		    }
		    @Override
		    public void handleMessage(Message msg) {
		    	Activity f = ref.get();
		    	
		    	((TextView)f.findViewById(R.id.txt_oauth_googleplus)).setText(msg.getData().getString("plus"));
		    	((TextView)f.findViewById(R.id.txt_oauth_googletasks)).setText(msg.getData().getString("tasks"));
		    	((TextView)f.findViewById(R.id.txt_oauth_foursquare)).setText(msg.getData().getString("foursquare"));
		    	
		    }
	}
	
	private WeakRefHandler mHandler = new WeakRefHandler(this);

		
	
	/**
	 * Clears our credentials (token and token secret) from the shared preferences.
	 * We also setup the authorizer (without the token).
	 * After this, no more authorized API calls will be possible.
	 * @throws IOException 
	 */
    private void clearCredentials(Oauth2Params oauth2Params)  {
		try {
			new OAuth2Helper(prefs,oauth2Params).clearCredentials();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

}

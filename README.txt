In this article, I'm going to show you how you can implement an OAuth 2.0 flow in Android.
We'll be using the Google Latitude API as a sample Google API (one that has recently received Oauth 2.0 support). We'll be using the Google API client for Java to help us out with the OAuth 2.0 flow, and the Latitude client library to interact with the Latitude API.

In the sample application, we're going to execute 1 authorized API call to the Latitude API. The call will return the current location of the user.

Google API client for Java makes it very easy to interact with various Google APIs. The library has a set of generated client libraries that hide a lot of the complexities when interacting with Google APIs.
As an example, to get the current location of the user, all it takes is the following 3 lines of code.


		    Latitude latitude = new Latitude(transport, accessProtectedResource, jsonFactory);
		    latitude.apiKey=OAuth2ClientCredentials.API_KEY;
		    
			LatitudeCurrentlocationResourceJson currentLocation = latitude.currentLocation.get().execute();

What the code above does is 

1. Initialize a Latitude service definition object, the main gateway to the Latitude API. The object is initiazed with an HTTP Transport, an HTTP request initializer and a JSON factory.
	We specify an HTTP transport, as the Latitude API like all Google APIs is a REST based API, where all communication is done over HTTP.
	We speficy an HTTP request initializer, as we need to ensure that the API calls are properly authorized. (meaning that the proper authorization headers get filled in with our OAutn 2.0 token).
	We specify a JSON Factory object, so that the responses coming back from the API can be serialized into a clean java based model.

2. We specify an API key on  the Latitude service defintion object, so we can track the API usage in the Google APIs console.

3. We execute a GET request on the currentLocation endpoint.

So in short, as a developer, there's no need to write plumbing code to create HTTP request objects, parse responses, making sure everything is properly signed.... all of that plumbing is handled by the Google API client for Java library, and the Latitude generate client library.

However, before we can actually start making these calls, we need to make sure we have an OAuth 2.0 access token. If we attempt to call the API in a non-secured way, we'll get the following exception.

com.google.api.client.http.HttpResponseException: 401 Unauthorized
     at com.google.api.client.http.HttpRequest.execute(HttpRequest.java:380)
     at com.google.api.services.latitude.Latitude$RemoteRequest.execute(Latitude.java:550)
     at com.google.api.services.latitude.Latitude$CurrentLocation$Get.executeUnparsed(Latitude.java:222)
     at com.google.api.services.latitude.Latitude$CurrentLocation$Get.execute(Latitude.java:207)
     at com.ecs.android.sample.oauth2.AndroidOauthGoogleApiJavaClient.getCurrentLocation(AndroidOauthGoogleApiJavaClient.java:107)
     at com.ecs.android.sample.oauth2.AndroidOauthGoogleApiJavaClient.performApiCall(AndroidOauthGoogleApiJavaClient.java:80)


The first thing we need to before we can make a call to the Latitude API is to obtain an OAuth 2.0 access token. This token is obtained through a series of HTTP interactions between the application and the service provider (Google in this case).
The first step is to present the user with an authorization screen, allowing him to authorize our application to access his Latitude API.

This authorizationUrl can be generated using the GoogleAuthorizationRequestUrl object, provided by the Google API client for Java.

	 String authorizationUrl = new GoogleAuthorizationRequestUrl(OAuth2ClientCredentials.CLIENT_ID, OAuth2ClientCredentials.OAUTH_CALLBACK_URL, OAuth2ClientCredentials.SCOPE).build();

What's important to note here is that we need to provide our OAuth 2.0 client ID (that we can found in the APIs console), a callback URL (after the user has authorized the request, Google will issue a redirect to this URL), and the 
autorization scope.

The Latitude API provides the following scopes. (see http://code.google.com/apis/latitude/v1/using_rest.html#auth for more info).

https://www.googleapis.com/auth/latitude.current.city	Access to current location at city granularity.
https://www.googleapis.com/auth/latitude.current.best	Access to best-available current location.
https://www.googleapis.com/auth/latitude.all.city	Access to current location and location history at city granularity.
https://www.googleapis.com/auth/latitude.all.best	Access to best-available current and past locations.

The generated URL that looks like this:

https://accounts.google.com/o/oauth2/auth?client_id=1021231231376.apps.googleusercontent.com&redirect_uri=http://localhost&response_type=code&scope=https://www.googleapis.com/auth/latitude.all.best

page looks like this : .... blabla


In our Android sample application, we'll load up this URL in a WebView. The reason why I've opted to use a WebView is because we need a hook somewhere to intercept the page when the user authorizes the request. Google OAuth 2.0
has some limitations regarding the redirect URIs. In a previous article, where I showed you how to implement the OAuth 1.0 flow in Android, we used a custom scheme in our Oauth redirect URL (xoauth://callback). With OAuth 2.0 this
is no longer possible. In our case, we're using http://localhost as a callback, and we'll use Webview to intercept this page, so that we can retrieve the code. There are other advantages of using a Webview. For example, the Webview doesn't have
an address bar, so the user cannot navigate away from the page. Also, the Webview doesn't interfere with your browser app, something you will have when you pop the browser app from your Android application.

We start by creating a WebView component, and putting it as the main content of the activity. 


		WebView webview = new WebView(this);
        webview.getSettings().setJavaScriptEnabled(true);  
        webview.setVisibility(View.VISIBLE);
        setContentView(webview);

We create a WebViewClient, needed to have a hook when the user has authorized the request, and we need to intercept the code.
In this code, we check if the URL loaded into the webview is our redirect URL (simple startswith check). If this is the case, there can be 2 options :

The user authorized the request, meaning a code request parameter will be present in the URL. If this is the case, we retrieve the code from the URL, and create a GoogleAuthorizationCodeGrant object.
We pass on our transport, a JSON factory, our OAuth 2.0 client ID and client secret, our code and our callback URL. When we execute the GoogleAuthorizationCodeGrant, we get a AccessTokenResponse that contains our OAuth 2.0 access token and refresh token.

We store the token response in our shared preferences, hide the webview (hack to ensure the user doesn't see the redirect URL being loaded in the webview, as we only need it to fetch the code, and not to display the URL), and start our main activity again.
In the main activity, we perform the Latitude API call (this time with a valid access token).
        
        /* WebViewClient must be set BEFORE calling loadUrl! */  
        webview.setWebViewClient(new WebViewClient() {  

        	@Override  
            public void onPageFinished(WebView view, String url)  {  
            	
            	if (url.startsWith(OAuth2ClientCredentials.OAUTH_CALLBACK_URL)) {
            		try {
						
            			if (url.indexOf("code=")!=-1) {
            			
	            			String code = url.substring(OAuth2ClientCredentials.OAUTH_CALLBACK_URL.length()+7,url.length());
							
				  		      AccessTokenResponse accessTokenResponse = new GoogleAuthorizationCodeGrant(new NetHttpTransport(),
										      new JacksonFactory(),
										      OAuth2ClientCredentials.CLIENT_ID,
										      OAuth2ClientCredentials.CLIENT_SECRET,
										      code,
										      OAuth2ClientCredentials.OAUTH_CALLBACK_URL).execute();
				
				  		      CredentialStore credentialStore = new SharedPreferencesCredentialStore(prefs);
				  		      credentialStore.write(accessTokenResponse);
				  		      view.setVisibility(View.INVISIBLE);
				  		      startActivity(new Intent(PrepareRequestTokenActivity.this,AndroidOauthGoogleApiJavaClient.class));
            			} else if (url.indexOf("error=")!=-1) {
            				new SharedPreferencesCredentialStore(prefs).clearCredentials();
            				startActivity(new Intent(PrepareRequestTokenActivity.this,AndroidOauthGoogleApiJavaClient.class));
            			}
            			
					} catch (IOException e) {
						e.printStackTrace();
					}

            	}
                System.out.println("onPageFinished : " + url);
  		      
            }  
        });  
        
        
The following code simply loads up the authorizationUrl into the webview.
        
        String authorizationUrl = new GoogleAuthorizationRequestUrl(OAuth2ClientCredentials.CLIENT_ID, OAuth2ClientCredentials.OAUTH_CALLBACK_URL, OAuth2ClientCredentials.SCOPE).build();
        webview.loadUrl(authorizationUrl);
        
        
Access / Refresh Tokens

The sample application contains a simple SharedPreferencesCredentialStore class that we'll use to store the Oauth 2.0 tokens, and retrieve them when we want to make an API call. Once we have the OAuth 2.0 tokens in our shared preferences, there's no
need to pop the Webview again, as the user has already authorized the request.



Executing the API call

The actual code to perform the secured API call can be found here.

We start by creating a JSON Factory and an HTTP transport.
We retrieve our accessTokenResponse from the credentialStore (were put there when the user authorized access through the WebView).
We create our Latitude service definition object
We execute a GET request on the currentLocation, to retrieve a LatitudeCurrentlocationResourceJson object.
We output the current location on the screen.

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
		    latitude.apiKey=OAuth2ClientCredentials.API_KEY;
		    
			LatitudeCurrentlocationResourceJson currentLocation = latitude.currentLocation.get().execute();
			String timestampMs = (String) currentLocation.get("timestampMs");
			DateFormat df= new  SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date d = new Date(Long.valueOf(timestampMs));
			textView.setText("Current location : " + currentLocation.get("latitude") + " - " + currentLocation.get("longitude") + " at " + df.format(d));
		} catch (Exception ex) {
			ex.printStackTrace();
			textView.setText("Error occured : " + ex.getMessage());
		}
	}
	
	
Conclusion :

I was surprised to see that there was little documentation available on how to properly implement OAuth 2.0 on an Android platform. I have the feeling that the method being outlined here is currently the only way to do it properly. 
During the last Google I/O, there was a session that talked about integrating OAuth 2.0 tokens with the Android AccountManager, meaning that you can authorize a user through the AccountManager. That should be more tightly coupled with the Android system,
and you're not forced to go through the Oauth 2.0 web based flow like we did here. Unfortunately, this didn't seem to work for the LAtitude API. IT is possible to get it up and running with the Google Buzz and Google Tasks API, but it also doesn't really 
make a great user experience. It's also not documented at all, and unclear if Google sees this as the preferred way of using OAuth 2.0 on an Android platform. The talk also mentioned that they are not quite ready with it.
So, to conclude, we've implemented the Oauth 2.0 webflow on an Android platform. Thanks to the Google API client for Java, very little code was required to setup the OAuth 2.0 dance, and using the WebView, we've ensured that we had a decent user experience, despite
the fact that we needed to load up the Google authorization page. This approach can be used for any Google API, and although the Google API client for Java is very Google API centric, it can be used with other service providers as well. 
	
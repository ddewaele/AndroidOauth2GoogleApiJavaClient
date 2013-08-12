##Introduction

The goal of this sample project is to show you how you can do OAuth2 from an Android application using the [Google OAuth Client Library for Java](https://code.google.com/p/google-oauth-java-client/).

The sample application allows you to access data from the following OAuth2 providers

- Foursquare (using the Google OAuth Client Library for Java to access a non-Google API).
- Google Tasks  (using the Google OAuth Client Library for Java to access this Google API).
- Google Plus (using Google APIs Client Library for Java, offering a higher level abstraction for interacting with Google APIs) 

##Legacy github repo / blog post

This repository has recently been updated to use a new version of the Google OAuth libraries, and now also includes the Foursquare flow.
The original blog post for the original github repository can be found here : http://blog.doityourselfandroid.com/2011/08/06/oauth-2-0-flow-android/
Note that some of the code samples in the article are now out-of-date due to the new version of the Google OAuth library.
I'll write up a new blog post as soon as I can find the time.

##Google projects for doing OAuth2

The sample application uses 3 projects from  the Google OAuth2 client library family. 

- [Google OAuth Client Library for Java](https://code.google.com/p/google-oauth-java-client/)
- [Google HTTP Client Library for Java](https://code.google.com/p/google-http-java-client/)
- [Google APIs Client Library for Java](http://code.google.com/p/google-api-java-client/)

The last one is optional and is only really needed when interacting with Google APIs. 
It offers a higher level abstraction for the Google OAuth2 endpoints.
There's for example no need to configure authorization and token URLs yourself as all of that is handled by the API.
It also comes with a huge set of [client libraries](https://code.google.com/p/google-api-java-client/wiki/APIs) that make it even easier to interact with Google APIs.

These client libraries not only encapsulates the OAuth2 part, but also provide

- Java wrappers for the data-structures used by the API (ex: the Google Plus client library will have an Activity / Person / Comment objects)
- Java wrappers for the actual API itself (ex: the Google Plus client library has a Plus class, allowing easy access to activities, persons and comments).

The Google Tasks sample included in the application for example doesn't use the Google APIs Client library, but shows you how to configure the Google OAuth2 endpoints manually, and execute the API calls using raw HTTP (without using a client library).

Although the Google APIs Client Library for Java primarily target Google APIs, they can also be used to interact with non-Google Oauth2 providers like Foursquare. 

##Project setup

This project is built using the [m2e-android plugin](http://rgladwell.github.io/m2e-android/index.html) to handle its external dependencies.

When using Eclipse ADT, it assumes that the following components are installed :

- Eclipse Market Client
- m2e-android plugin

If you don't have the Eclipse Marker Client installed, you can install it by clicking on 

```Help → Install new Software → Switch to the Juno Repository → General Purpose Tools → Marketplace Client```

Once you have the Eclipse Market Client installed, you can proceed to install the m2e-android plugin

```Help -> Eclipse Marketplace... and search for "android m2e".```

More instructions can be found on the [m2e-android plugin](http://rgladwell.github.io/m2e-android/index.html) site.

## Project configuration

The OAuth2 connection params are defined in the ```com.ecs.android.sample.oauth2.OAuth2Params``` class.

	GOOGLE_PLUS("","","https://accounts.google.com/o/oauth2/token","https://accounts.google.com/o/oauth2/auth",BearerToken.authorizationHeaderAccessMethod(),PlusScopes.PLUS_ME,"http://localhost","plus","https://www.googleapis.com/plus/v1/people/me/activities/public"),
	GOOGLE_TASKS_OAUTH2("","","https://accounts.google.com/o/oauth2/token","https://accounts.google.com/o/oauth2/auth",BearerToken.authorizationHeaderAccessMethod(),"https://www.googleapis.com/auth/tasks","http://localhost","tasks","https://www.googleapis.com/tasks/v1/users/@me/lists"),
	FOURSQUARE_OAUTH2("","","https://foursquare.com/oauth2/access_token", "https://foursquare.com/oauth2/authenticate",FoursquareQueryParameterAccessMethod.getInstance(),"","http://localhost","foursquare","https://api.foursquare.com/v2/users/self/checkins"); 

I haven't shared my own clientId and clientSecrets here so you'll need to provide them yourself (first 2 arguments of the constructor.

If you don't provide the clientId and clientSecret you'll see the following message on your screen.

![No clientID and secret provided](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/OAuth2Demo/10_noclientidandsecret.png)

If you have provided a clientId and clientSecret then you should see this

![clientID and secret provided](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/OAuth2Demo/1_intro_screen.png)

Notice how for each OAuth2 provider we have 3 buttons

- OAuth2 (starts the oauth2 flow and gets an access token (+ optional refresh token)
- API (executes an API call)
- Clear (clears the credentials)

When you start an OAuth2 flow (ex: the Google flows), you'll be invited to login with your Google account

![clientID and secret provided](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/OAuth2Demo/2_google_login.png)

Google will then prompt you to provide access to your application to access the users data.

![clientID and secret provided](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/OAuth2Demo/3_google_authorization.png)
 
When returning to the application, an API call will be executed on a protected resource from that OAuth2 service provided. The RAW JSON is simply dumped on the screen.

![clientID and secret provided](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/OAuth2Demo/4_api_call.png)

When returning to the intro screen, the application shows the ```access_token``` and the time in seconds before it expires. Note that the Google OAuth library handles expired tokens transparently.
So when clicking on the API button with an expired token, the API will automatically refresh the token in the background before executing the API call.  

![clientID and secret provided](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/OAuth2Demo/5_accesstoken.png)
    
The same is applicable for non-Google OAuth2 service providers like Foursquare. You also need to login and you also need to authorize the 
application to access your data.
    
![clientID and secret provided](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/OAuth2Demo/6_foursquare.png)
![clientID and secret provided](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/OAuth2Demo/7_foursquare_login.png)

Note how Foursquare doesn't issue a refresh token, but rather a non-expiring access token.

![clientID and secret provided](https://dl.dropboxusercontent.com/u/13246619/Blog%20Articles/OAuth2Demo/9_accesstoken2.png)


## Project dependences

This project depends on the following libraries. (automatically pulled in when using the m2e-android plugin).

- commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar
- org/apache/httpcomponents/httpclient/4.0.1/httpclient-4.0.1.jar
- org/apache/httpcomponents/httpcore/4.0.1/httpcore-4.0.1.jar
- commons-codec/commons-codec/1.3/commons-codec-1.3.jar
- com/google/apis/google-api-services-plus/v1-rev72-1.15.0-rc/google-api-services-plus-v1-rev72-1.15.0-rc.jar
- com/google/api-client/google-api-client/1.15.0-rc/google-api-client-1.15.0-rc.jar
- com/google/oauth-client/google-oauth-client/1.15.0-rc/google-oauth-client-1.15.0-rc.jar
- com/google/http-client/google-http-client-jackson2/1.15.0-rc/google-http-client-jackson2-1.15.0-rc.jar
- com/google/http-client/google-http-client/1.15.0-rc/google-http-client-1.15.0-rc.jar
- com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar
- com/fasterxml/jackson/core/jackson-core/2.1.3/jackson-core-2.1.3.jar

## References

- [m2e-android plugin](http://rgladwell.github.io/m2e-android/index.html)
- [Google APIs Client Library for Java](http://code.google.com/p/google-api-java-client/)
- [Google OAuth Client Library for Java](https://code.google.com/p/google-oauth-java-client/)
- [Google HTTP Client Library for Java](https://code.google.com/p/google-http-java-client/)
- [OAuth2 Playground](https://developers.google.com/oauthplayground)




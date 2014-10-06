package com.maryamq.basictwitter.client;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change
																				// this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change
																			// this,
																			// base
																			// API
																			// URL
	public static final String REST_CONSUMER_KEY = "k08bfmd5C7kIYn8KKYI0KuZPG"; // Change
																				// this
	public static final String REST_CONSUMER_SECRET = "budWoqJmYmnunHyxe1d84TR8vim82ssUI6wTrvOJ7zzNFPXXM4"; // Change
																											// this
	public static final String REST_CALLBACK_URL = "oauth://cpbasictweets"; // Change
																			// this
																			// (here
																			// and
																			// in
																			// manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimelineFrom(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("max_id", maxId + "");
		Utils.log("Get Timeline from " + maxId );
		client.get(apiUrl, params, handler);
		
	}
	
	public void getHomeTimelineSince(long since, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("since_id", since + "");
		Utils.log("Get Timeline Since " + since);
		client.get(apiUrl, params, handler);
	}
	
	public void getMentionsTimelineFrom(long maxId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("max_id", maxId + "");
		client.get(apiUrl, params, handler);
		
	}
	
	public void getMentionsTimelineSince(long since, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("since_id", since + "");
		client.get(apiUrl, params, handler);
	}
	
	public void getUserTimelineFrom(long maxId, long userId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("max_id", maxId + "");
		params.put("user_id", userId + "");
		client.get(apiUrl, params, handler);
	}
	
	public void getUserTimelineSince(long since, long userId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("since_id", since + "");
		params.put("user_id", userId + "");
		client.get(apiUrl, params, handler);
	}
	
	public void postNewTweet(String message, AsyncHttpResponseHandler handler) {
		postNewTweet(message, -1, handler);
	}
	

	public void postNewTweet(String message, long prevTweetId, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", message);
		if (prevTweetId != -1) {
			params.put("in_reply_to_status_id", prevTweetId + "");
		}
		client.post(apiUrl, params, handler);
		
	}
	
	/*
	 * // CHANGE THIS // DEFINE METHODS for different API endpoints here public
	 * void getInterestingnessList(AsyncHttpResponseHandler handler) { String
	 * apiUrl =
	 * getApiUrl("?nojsoncallback=1&method=flickr.interestingness.getList"); //
	 * Can specify query string params directly or through RequestParams.
	 * RequestParams params = new RequestParams(); params.put("format", "json");
	 * client.get(apiUrl, params, handler); }
	 */

	/*
	 * 1. Define the endpoint URL with getApiUrl and pass a relative path to the
	 * endpoint i.e getApiUrl("statuses/home_timeline.json"); 2. Define the
	 * parameters to pass to the request (query or body) i.e RequestParams
	 * params = new RequestParams("foo", "bar"); 3. Define the request method
	 * and make a call to the client i.e client.get(apiUrl, params, handler);
	 * i.e client.post(apiUrl, params, handler);
	 */
}
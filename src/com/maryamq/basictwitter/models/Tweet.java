package com.maryamq.basictwitter.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {

private String body;
private long uid;
private String createdAt;
private User user;

public String getBody() {
	return body;
}

public long getUid() {
	return uid;
}

public String getCreatedAt() {
	return createdAt;
}

public User getUser() {
	return user;
}

public static Tweet fromJSON(JSONObject json) {
	Tweet tweet = new Tweet();
	// Extract values form JSON
	try {
		tweet.body = json.getString("text");
		tweet.uid = json.getLong("id");
		tweet.createdAt = json.getString("created_at");
		tweet.user = User.fromJSON(json.getJSONObject("user"));
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
	return tweet;
}

public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
	// TODO Auto-generated method stub
	ArrayList<Tweet> tweets = new ArrayList<Tweet>();
	for (int i = 0; i < jsonArray.length(); i++){
		JSONObject json = null;
		try {
			json = jsonArray.getJSONObject(i);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			continue;
		}
		
		Tweet tweet = Tweet.fromJSON(json);
		if(tweet != null) {
			tweets.add(tweet);
		}
	}
	
	return tweets;
	
}
}

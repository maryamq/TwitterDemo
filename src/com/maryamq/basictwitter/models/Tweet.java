package com.maryamq.basictwitter.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.provider.BaseColumns;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.maryamq.basictwitter.client.Utils;

@Table(name = "Tweet", id = BaseColumns._ID)
public class Tweet extends Model implements Serializable {

	@Column(name = "body")
	private String body;

	@Column(name = "tweetId", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;

	@Column(name = "createdAt")
	private String createdAt;

	@Column(name = "user", onUpdate = ForeignKeyAction.CASCADE, onDelete = ForeignKeyAction.CASCADE, notNull = true)
	private User user;
	
	@Column(name = "media_url")
	private String media_url;  // TODO: Create its own model

	public String getBody() {
		return body;
	}

	public String getMediaUrl() {
		return media_url;
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

	
	public void persist() {
		this.user = this.user.updateOrCreateUser();
		this.save();
	}

	private static String getMediaUrl(JSONObject json) throws JSONException {
		if (!json.getJSONObject("entities").isNull("media")) {
            JSONArray mediaArray = json.getJSONObject("entities").getJSONArray("media");
            if (mediaArray.length() > 0) {
            	JSONObject media = mediaArray.getJSONObject(0);
            	if (!media.isNull("media_url_https")){
            		return media.get("media_url_https").toString();
            	} 
            }
        }
		return "";
	}
	public static Tweet fromJSON(JSONObject json) {
		Tweet tweet = new Tweet();
		// Extract values form JSON
		try {
			tweet.body = json.getString("text");
			tweet.uid = json.getLong("id");
			tweet.createdAt = json.getString("created_at");
			tweet.user = User.fromJSON(json.getJSONObject("user"));
			// Get media url
			tweet.media_url = getMediaUrl(json);
			tweet.persist();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return tweet;
	}

	public static List<Tweet> loadAll() {
		return new Select().from(Tweet.class).orderBy("tweetId DESC")
				.execute();
	}

	public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
		// TODO Auto-generated method stub
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		ActiveAndroid.beginTransaction();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject json = null;
			try {
				json = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}

			Tweet tweet = Tweet.fromJSON(json);
			if (tweet != null) {
				tweets.add(tweet);
			}
		}
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
		return tweets;

	}
}

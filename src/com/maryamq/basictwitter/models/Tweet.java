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

	public void persist() {
		this.user = this.user.updateOrCreateUser();
		this.save();
	}

	public static Tweet fromJSON(JSONObject json) {
		Tweet tweet = new Tweet();
		// Extract values form JSON
		try {
			tweet.body = json.getString("text");
			tweet.uid = json.getLong("id");
			tweet.createdAt = json.getString("created_at");
			tweet.user = User.fromJSON(json.getJSONObject("user"));
			tweet.persist();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return tweet;
	}

	public static List<Tweet> loadAll() {
		return new Select().from(Tweet.class).execute();
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

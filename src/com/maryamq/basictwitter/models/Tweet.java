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
import com.maryamq.basictwitter.TwitterApplication;
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
	
	@Column(name = "favorited")
	private boolean favorited = false;
	
	@Column(name = "user_mentioned")
	private boolean isUserMentioned;
	
	@Column(name = "is_home_timeline")
	private boolean isInHomeTimeline;

	@Column(name = "in_reply_to_user")
	private String inReplyToUserId;

	@Column(name = "retweet_count")
	private int retweetCount;
	
	@Column(name = "retweeted")
	private boolean retweeted;
	
	public boolean isRetweeted() {
		return retweeted;
	}

	public void setRetweeted(boolean retweeted) {
		this.retweeted = retweeted;
	}

	public int getRetweetCount() {
		return retweetCount;
	}
	
	public void setRetweetCount(int count) {
		retweetCount = count;
	}
	
	public String getInReplyToUserId() {
		return inReplyToUserId;
	}
	
	public boolean isFavorited() {
		return favorited;
	}
	
	public boolean isUserMentioned() {
		return isUserMentioned;
	}
	
	public boolean isInHomeTimeline() {
		return isInHomeTimeline;
	}
	
	public void setIsFavorited(boolean value) {
		favorited = value;
	}
	
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
	
	public static Tweet getTweet(long id) {
		List<Tweet> savedTweets = new Select().from(Tweet.class).where("tweetId=?", id).execute();
		if (savedTweets.size() > 0) {
			return savedTweets.get(0);
		} else {
			return null;
		}
	}
	
	public static List<Tweet> getMentionedTweets() {
		return new Select().from(Tweet.class).where("user_mentioned=?", true).execute();
	}
	
	public static List<Tweet> getHomeTimelineTweets() {
		return new Select().from(Tweet.class).where("is_home_timeline=?", true).execute();
	}


	public static Tweet fromJSON(JSONObject json) {
		return fromJSON(json, true);
	}
	
	public static Tweet fromJSON(JSONObject json, boolean persist) {
		Tweet tweet = null;
		
		// Extract values form JSON
		try {
			long id = json.getLong("id");
			tweet = getTweet(id);
			if (tweet == null) {
				tweet = new Tweet();
				tweet.uid = id;
			}
			
			tweet.body = json.getString("text");
			tweet.uid = json.getLong("id");
			tweet.createdAt = json.getString("created_at");
			tweet.user = User.fromJSON(json.getJSONObject("user"));
			tweet.favorited = json.getBoolean("favorited");
			tweet.retweeted = json.getBoolean("retweeted");
			tweet.inReplyToUserId = json.isNull("in_reply_to_screen_name") ? "" :
				json.getString("in_reply_to_screen_name");
			tweet.retweetCount = json.getInt("retweet_count");
			//Get media url
			tweet.media_url = getMediaUrl(json);
			
			if (!json.isNull("user_mentions")) {
				JSONArray usersMentioned = json.getJSONArray("user_mentions");
				tweet.isUserMentioned = false;
				if (TwitterApplication.getRestClient().getAccount() != null) {
					long accountId = TwitterApplication.getRestClient().getAccount().getUid();
					for (int j=0; j<usersMentioned.length(); j++) {
						Utils.log("User: " + usersMentioned.getJSONObject(j).toString());
						if (accountId == usersMentioned.getJSONObject(j).getLong("id")) {
							tweet.isUserMentioned = true;
							break;
						}
						
					}
				}
			}
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
		ArrayList<Tweet> tweets = new ArrayList<Tweet>();
		ActiveAndroid.beginTransaction();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject json = null;
			try {
				json = jsonArray.getJSONObject(i);
			} catch (JSONException e) {
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

	public void setIsMentioned(boolean b) {
		this.isUserMentioned = b;
		
	}

	public void setIsHomeTimeline(boolean b) {
		this.isInHomeTimeline = true;
		
	}
}

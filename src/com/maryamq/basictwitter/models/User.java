package com.maryamq.basictwitter.models;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.provider.BaseColumns;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.maryamq.basictwitter.client.Utils;

@Table(name="User", id = BaseColumns._ID)
public class User extends Model implements Serializable {

	@Column(name="Name")
	private String name;
	
	@Column(name="UserId", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long uid;
	
	@Column(name="ScreenName")
	private String screenName;
	
	@Column(name="ProfileImageUrl")
	private String profileImageUrl;
	
	@Column(name="CoverUrl")
	private String coverUrl;
	


	@Column(name="FavouriteCount")
	private int favCount;
	
	@Column(name="FriendsCount")
	private int friendsCount;
	
	@Column(name="FollowersCount")
	private int followersCount;
	
	@Column(name="TweetsCount")
	private int tweetsCount;

	@Column(name="Tagline")
	private String tagline;
	
	public String getTagline() {
		return tagline;
	}
	
	public User updateOrCreateUser() {
		List<User> userIds = new Select().from(User.class).where("UserId=?", uid).execute();
		if (userIds == null || userIds.size() == 0) {
			Utils.log("New User id" + uid);
			save();
			
		}
		List<User> savedUser = new Select().from(User.class).where("ScreenName=?", screenName).execute();
		
		return savedUser.get(0);
	}
	
	public static User getUser(long id) {
		List<User> savedUser = new Select().from(User.class).where("UserId=?", id).execute();
		if (savedUser.size() > 0) {
			return savedUser.get(0);
		} else {
			return null;
		}
		
	}

	public static User fromJSON(JSONObject json) {
		// TODO Auto-generated method stub
				
		
		User user = null;
		try {
			long uid = json.getLong("id");
			user = getUser(uid);
			if (user == null) {
			  user = new User();
			}
			
			user.name = json.getString("name");
			user.uid = json.getLong("id");
			user.screenName = json.getString("screen_name");
			user.profileImageUrl =  json.getString("profile_image_url");
			user.coverUrl = !json.isNull("profile_background_image_url") ?
					json.getString("profile_background_image_url") : "";
			user.tagline = !json.isNull("description") ? 
					json.getString("description"): "";
					
			// Parse additional params if available (by get credials call)
			if (!json.isNull("friends_count")) {
				user.friendsCount = json.getInt("friends_count");
			}
			if (!json.isNull("followers_count")) {
				user.followersCount = json.getInt("followers_count");
			}
			
			if (!json.isNull("favourites_count")) {
				user.favCount = json.getInt("favourites_count");
			}
			
			if (!json.isNull("statuses_count")) {
				user.tweetsCount = json.getInt("statuses_count");
			}
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return user;
	}

	public String getName() {
		return name;
	}

	public long getUid() {
		return uid;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
	public String getCoverUrl() {
		return coverUrl;
	}
	
	public int getFavCount() {
		return favCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public int getFollowersCount() {
		return followersCount;
	}
	
	public int getTweetsCount() {
		return tweetsCount;
	}

}

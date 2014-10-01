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
	
	public User updateOrCreateUser() {
		List<User> userIds = new Select().from(User.class).where("UserId=?", uid).execute();
		if (userIds == null || userIds.size() == 0) {
			Utils.log("New User id" + uid);
			save();
			
		}
		List<User> savedUser = new Select().from(User.class).where("ScreenName=?", screenName).execute();
		
		return savedUser.get(0);
	}

	public static User fromJSON(JSONObject json) {
		// TODO Auto-generated method stub
		User user = new User();
		try {
			user.name = json.getString("name");
			user.uid = json.getLong("id");
			user.screenName = json.getString("screen_name");
			user.profileImageUrl =  json.getString("profile_image_url");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			user = null;
		}
		if (user == null) {
			Log.d("Debug", "User is null");
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
	

}

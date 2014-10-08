package com.maryamq.basictwitter.activities;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.TwitterApplication;
import com.maryamq.basictwitter.client.TwitterClient;
import com.maryamq.basictwitter.dialog.ComposeDialog.Mode;
import com.maryamq.basictwitter.fragments.CoverFragment;
import com.maryamq.basictwitter.fragments.TwitterListFragment;
import com.maryamq.basictwitter.fragments.TwitterListFragment.IDataFetcher;
import com.maryamq.basictwitter.models.Tweet;
import com.maryamq.basictwitter.models.User;

public class ProfileActivity extends FragmentActivity implements IDataFetcher {
	public static final String USER_DATA_KEY = "user";
	
	User user;
	TwitterClient client = TwitterApplication.getRestClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xFF55ACEE));
		user = (User) this.getIntent().getSerializableExtra(
				USER_DATA_KEY);
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.flTopCover, new CoverFragment(user));
		ft.replace(R.id.flTweets, new TwitterListFragment());
		ft.commit();
		
	}

	@Override
	public void getMoreTweets(Fragment f, long fromId,
			JsonHttpResponseHandler responseHandler) {
		client.getUserTimelineFrom(fromId, user.getUid(), responseHandler);

	}

	@Override
	public void getLastestTweets(Fragment f, long sinceId,
			JsonHttpResponseHandler responseHandler) {
		client.getUserTimelineSince(sinceId, user.getUid(), responseHandler);
	}

	@Override
	public List<Tweet> getInitialTweets() {
		// TODO Auto-generated method stub
		return new ArrayList<Tweet>();
	}

	@Override
	public void persist(List<Tweet> tweets) {
		// do nothing
		
	}

	@Override
	public boolean addToTimeline(Tweet t, Mode m) {
		// TODO Auto-generated method stub
		return false;
	}
}

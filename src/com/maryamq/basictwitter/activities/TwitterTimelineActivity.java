package com.maryamq.basictwitter.activities;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.TwitterApplication;
import com.maryamq.basictwitter.activities.TwitterListFragment.IDataFetcher;
import com.maryamq.basictwitter.client.TwitterClient;
import com.maryamq.basictwitter.client.Utils;
import com.maryamq.basictwitter.dialog.ComposeDialog;
import com.maryamq.basictwitter.listeners.FragmentTabListener;
import com.maryamq.basictwitter.models.User;

public class TwitterTimelineActivity extends FragmentActivity implements IDataFetcher {
	TwitterClient client = TwitterApplication.getRestClient();
	Tab homeTab;
	Tab mentionsTab;
	private User account;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xFF55ACEE));
		setupTabs();

	}

	private void setupTabs() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		homeTab = actionBar
				.newTab()
				.setText("Home")
				.setTag("HomeTimelineFragment")
				.setTabListener(
						new FragmentTabListener<TwitterListFragment>(R.id.flTweets, this,
								"first", TwitterListFragment.class));

		actionBar.addTab(homeTab);

		mentionsTab = actionBar
				.newTab()
				.setText("Mentions")
				.setTag("MentionsTimelineFragment")
				.setTabListener(
						new FragmentTabListener<TwitterListFragment>(R.id.flTweets, this,
								"first", TwitterListFragment.class));

		actionBar.addTab(mentionsTab);
		actionBar.selectTab(homeTab);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.miCompose) {
			ComposeDialog dialog = new ComposeDialog(client);
			dialog.show(this.getSupportFragmentManager(), "fragment_compose");
		} else if (item.getItemId() == R.id.miProfile) {
			if (account == null) {
				// load account
				client.getMyInfo(new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, JSONObject arg1) {
						// TODO Auto-generated method stub
						account = User.fromJSON(arg1);
						account.save();
						Intent intent = new Intent(TwitterTimelineActivity.this, ProfileActivity.class);
						intent.putExtra(ProfileActivity.USER_DATA_KEY, account);
						startActivity(intent);
					}
					
					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						Utils.log(arg1.toString());
						super.onFailure(arg0, arg1);
					}
					
				});
				
			} else {
				Intent intent = new Intent(TwitterTimelineActivity.this, ProfileActivity.class);
				intent.putExtra(ProfileActivity.USER_DATA_KEY, account);
				startActivity(intent);
			}
			
		}

		return true;
	}

	@Override
	public void getMoreTweets(Fragment f, long fromId,
			JsonHttpResponseHandler responseHandler) {
		if (getActionBar().getSelectedTab() == this.homeTab) {
			client.getHomeTimelineFrom(fromId, responseHandler);
		} else {
			client.getMentionsTimelineFrom(fromId, responseHandler);
		}

	}

	@Override
	public void getLastestTweets(Fragment f, long sinceId,
			JsonHttpResponseHandler responseHandler) {

		if (getActionBar().getSelectedTab() == this.homeTab) {
			client.getHomeTimelineSince(sinceId, responseHandler);
		} else {
			client.getMentionsTimelineSince(sinceId, responseHandler);

		}
	}

}

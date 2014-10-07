package com.maryamq.basictwitter.activities;

import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.TwitterApplication;
import com.maryamq.basictwitter.activities.TwitterListFragment.IDataFetcher;
import com.maryamq.basictwitter.client.TwitterClient;
import com.maryamq.basictwitter.client.Utils;
import com.maryamq.basictwitter.dialog.ComposeDialog;
import com.maryamq.basictwitter.dialog.ComposeDialog.ComposeDialogListener;
import com.maryamq.basictwitter.dialog.ComposeDialog.Mode;
import com.maryamq.basictwitter.listeners.FragmentTabListener;
import com.maryamq.basictwitter.models.Tweet;

public class TwitterTimelineActivity extends FragmentActivity implements IDataFetcher,
		ComposeDialogListener {

	TwitterClient client = TwitterApplication.getRestClient();
	Tab homeTab;
	Tab mentionsTab;
	FragmentTabListener<TwitterListFragment> homeTabListener;
	SearchView searchView;

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

		this.homeTabListener = new FragmentTabListener<TwitterListFragment>(
				R.id.flTweets, this, "Home", TwitterListFragment.class);
		homeTab = actionBar.newTab().setText("Home").setTag("HomeTimelineFragment")
				.setTabListener(homeTabListener);

		actionBar.addTab(homeTab);

		FragmentTabListener<TwitterListFragment> mentionsTabListener = new FragmentTabListener<TwitterListFragment>(
				R.id.flTweets, this, "Mentions", TwitterListFragment.class);
		mentionsTab = actionBar.newTab().setText("Mentions")
				.setTag("MentionsTimelineFragment").setTabListener(mentionsTabListener);
		actionBar.addTab(mentionsTab);
		actionBar.selectTab(homeTab);

		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.timeline, menu);
		searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				// perform query here
				Intent i = new Intent(
						TwitterTimelineActivity.this, SearchActivity.class);
				i.putExtra(SearchActivity.SEARCH_QUERY, query);
				TwitterTimelineActivity.this.startActivity(i);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.miCompose) {
			ComposeDialog dialog = new ComposeDialog(client);
			dialog.setResponseHandler(this);
			dialog.show(this.getSupportFragmentManager(), "fragment_compose");
		} else if (item.getItemId() == R.id.miProfile) {
			if (TwitterApplication.getRestClient().getAccount() == null) {
				// load account
				Utils.showToast(this, "Unable to load user info");

			} else {
				Intent intent = new Intent(TwitterTimelineActivity.this,
						ProfileActivity.class);
				intent.putExtra(ProfileActivity.USER_DATA_KEY, TwitterApplication
						.getRestClient().getAccount());
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

	@Override
	public void onPostNewTweet(Tweet newTweet, Tweet sourceTweet, Mode mode) {
		TwitterListFragment fragment = (TwitterListFragment) this.homeTabListener
				.getFragment();
		fragment.addTweet(newTweet);
	}

	@Override
	public List<Tweet> getInitialTweets() {
		// TODO Auto-generated method stub
		if (getActionBar().getSelectedTab() == this.homeTab) {
			return Tweet.getHomeTimelineTweets();
		} else {
			return Tweet.getMentionedTweets();
		}

	}

	@Override
	public void persist(List<Tweet> tweets) {
		if (getActionBar().getSelectedTab() == this.homeTab) {
			for (Tweet t: tweets) {
				t.setIsHomeTimeline(true);
				t.persist();
			}	
		} else {
			for (Tweet t: tweets) {
				t.setIsMentioned(true);
				t.persist();
			}	
		}
		
	}
}

package com.maryamq.basictwitter.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.activeandroid.content.ContentProvider;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.TwitterApplication;
import com.maryamq.basictwitter.R.id;
import com.maryamq.basictwitter.R.layout;
import com.maryamq.basictwitter.R.menu;
import com.maryamq.basictwitter.adapters.TweetArrayAdapter;
import com.maryamq.basictwitter.client.TwitterClient;
import com.maryamq.basictwitter.client.Utils;
import com.maryamq.basictwitter.dialog.ComposeDialog;
import com.maryamq.basictwitter.dialog.ComposeDialog.ComposeDialogListener;
import com.maryamq.basictwitter.dialog.ComposeDialog.Mode;
import com.maryamq.basictwitter.listeners.EndlessScrollListener;
import com.maryamq.basictwitter.models.Tweet;

public class TwitterTimelineActivity extends FragmentActivity implements
		ComposeDialogListener {

	private TwitterClient client;
	private List<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private ListView lvTweets;
	private long maxIdValue = 0;
	private long since_id = 1;
	private int lastLoadCount = 0;
	private SwipeRefreshLayout swipeContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		client = TwitterApplication.getRestClient();

		lvTweets = (ListView) this.findViewById(R.id.lvTweets);
		lvTweets.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// TODO Auto-generated method stub
				if (lastLoadCount == 0) {
					// aTweets.clear();
				}
				fetchMore();
				lastLoadCount = totalItemsCount;

			}

		});
		tweets = Tweet.loadAll();
		aTweets = new TweetArrayAdapter(this, tweets, client,
				this.getSupportFragmentManager());
		lvTweets.setAdapter(aTweets);
		this.updateIds();

		swipeContainer = (SwipeRefreshLayout) this
				.findViewById(R.id.swipeContainer);
		swipeContainer.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				swipeContainer.setRefreshing(true);
				fetchLatest();
			}
		});
		// Configure the refreshing colors
		swipeContainer.setColorSchemeResources(
				android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		// this.fetchLatest();
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xFF55ACEE));
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
			return true;
		}
		return false;
	}

	private void fetchMore() {
		if (!hasInternet()) {
			return;
		}
		client.getHomeTimelineFrom(maxIdValue, new JsonHttpResponseHandler() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(JSONArray jsonArray) {
				ArrayList<Tweet> tweets = Tweet.fromJSONArray(jsonArray);
				aTweets.addAll(tweets);
				updateIds();
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				Utils.showToast(getApplicationContext(), "Failure: " + arg1);
				Utils.log("Failure: " + arg1);
				// Handle known errors more elegantly
				super.onFailure(arg0, arg1);
			}
		});
	}

	private boolean hasInternet() {
		if (!Utils.isNetworkAvailable(getApplicationContext())) {
			Utils.showToast(getApplicationContext(), "Internet unavailale");
			return false;
		}
		return true;
	}

	private void fetchLatest() {
		if (!hasInternet()) {
			return;
		}
		client.getHomeTimelineSince(since_id, new JsonHttpResponseHandler() {
			@SuppressLint("NewApi")
			@Override
			public void onSuccess(JSONArray jsonArray) {
				swipeContainer.setRefreshing(false);
				ArrayList<Tweet> newTweets = Tweet.fromJSONArray(jsonArray);
				for (int i = newTweets.size() - 1; i >= 0; i--) {
					tweets.add(0, newTweets.get(i));
				}
				aTweets.notifyDataSetChanged();
				updateIds();
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				Utils.showToast(getBaseContext(), "Error: " + arg1);
				Utils.log("Failed to fetch new tweets: " + arg1);
				swipeContainer.setRefreshing(false);
			}

			@Override
			public void handleFailureMessage(Throwable e, String responseBody) {
				Utils.showToast(getBaseContext(), "Unexpected failure: " + responseBody);
				Utils.log("Unexpected failure: " + responseBody);
				swipeContainer.setRefreshing(false);
			}
		});

	}
	
	private void setupSql() {
		this.getSupportLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {
	        @Override
	        public Loader<Cursor> onCreateLoader(int arg0, Bundle cursor) {
	        	 return new CursorLoader(TwitterTimelineActivity.this,
	                     ContentProvider.createUri(Tweet.class, null), null, null, null, null);
	        }
	        // ...

			@Override
			public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onLoaderReset(Loader<Cursor> arg0) {
				// TODO Auto-generated method stub
				
			}
	});
	}

	@Override
	public void onPostNewTweet(Tweet newTweet, Tweet sourceTweet, Mode mode) {
		this.tweets.add(0, newTweet);
		newTweet.persist();
		aTweets.notifyDataSetChanged();
	}

	private void updateIds() {
		for (int i = 0; i < aTweets.getCount(); i++) {
			long uid = aTweets.getItem(i).getUid();
			// Add +1 to avoid duplicates. @See docs.
			maxIdValue = i == 0 ? uid : Math.min(uid, maxIdValue) + 1;
			since_id = i == 0 ? uid : Math.max(uid, since_id);
		}
		Utils.log("Since id is " + since_id);
	}

}

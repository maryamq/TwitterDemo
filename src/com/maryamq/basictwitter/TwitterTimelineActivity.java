package com.maryamq.basictwitter;


import java.util.ArrayList;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codepath.apps.restclienttemplate.ComposeDialog;
import com.codepath.apps.restclienttemplate.ComposeDialog.ComposeDialogListener;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import com.maryamq.basictwitter.models.Tweet;

public class TwitterTimelineActivity extends FragmentActivity implements ComposeDialogListener {

	private TwitterClient client;
    private ArrayList<Tweet> tweets;
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
		populateTimeline();
		
		lvTweets = (ListView)this.findViewById(R.id.lvTweets);
		lvTweets.setOnScrollListener(new EndlessScrollListener(){

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// TODO Auto-generated method stub
				if (lastLoadCount == 0) {
					//aTweets.clear();
				}
				populateTimeline();
				lastLoadCount = totalItemsCount;
				
			}
			
		});
		tweets = new ArrayList<Tweet>();
		aTweets = new TweetArrayAdapter(this, tweets);
	    lvTweets.setAdapter(aTweets);
	    
	    swipeContainer = (SwipeRefreshLayout)this.findViewById(R.id.swipeContainer);
	    swipeContainer.setOnRefreshListener(new OnRefreshListener(){

			@Override
			public void onRefresh() {
				swipeContainer.setRefreshing(true);
				fetchLatest();		
			}
	    });
	    // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, 
                android.R.color.holo_green_light, 
                android.R.color.holo_orange_light, 
                android.R.color.holo_red_light);
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
	
	private void log(String message) {
		Log.d("DEBUG", message);
	}
	
	private void populateTimeline() {
	  log("Since id is " + maxIdValue);
	  client.getHomeTimeline(maxIdValue, new JsonHttpResponseHandler(){
		@SuppressLint("NewApi") @Override
		public void onSuccess(JSONArray jsonArray) {
			ArrayList<Tweet> tweets = Tweet.fromJSONArray(jsonArray);
			aTweets.addAll(tweets);
			updateIds();
		}
		
		  @Override
		public void onFailure(Throwable arg0, String arg1) {
			// TODO Auto-generated method stub
			super.onFailure(arg0, arg1);
		}
	  });
	}

	private void fetchLatest() {
		  client.getHomeTimelineSince(since_id, new JsonHttpResponseHandler(){
			@SuppressLint("NewApi") @Override
			public void onSuccess(JSONArray jsonArray) {
				swipeContainer.setRefreshing(false);
				ArrayList<Tweet> newTweets = Tweet.fromJSONArray(jsonArray);
				for (int i= newTweets.size() -1; i>=0; i--) {
					tweets.add(0, newTweets.get(i));
				}
				aTweets.notifyDataSetChanged();
				updateIds();
			}
			
			  @Override
			public void onFailure(Throwable arg0, String arg1) {
				// TODO Auto-generated method stub
				Utils.showToast(getBaseContext(), "Failed to fetch new tweets");
				swipeContainer.setRefreshing(false);
			}
			  @Override
			public void handleFailureMessage(Throwable e, String responseBody) {
				  Utils.showToast(getBaseContext(), "Unexpected failure");
			}
		  });
			
		}
	
	@Override
	public void onPostNewTweet(Tweet newTweet) {
	   //this.tweets.add(0, newTweet);
	   //aTweets.notifyDataSetChanged();
		this.fetchLatest();
		
	}

	private void updateIds() {
		for (int i=0; i<aTweets.getCount(); i++) {
			long uid = aTweets.getItem(i).getUid();
			maxIdValue = i== 0 ? uid : Math.min(uid, maxIdValue) + 1; // Add +1 to avoid duplicates. see docs.
			since_id = i== 0 ? uid : Math.max(uid, since_id);
		}
		log("Since id is " + since_id);
	}

}

package com.maryamq.basictwitter;


import java.util.ArrayList;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.codepath.apps.restclienttemplate.ComposeDialog;
import com.codepath.apps.restclienttemplate.ComposeDialog.ComposeDialogListener;
import com.codepath.apps.restclienttemplate.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.models.Tweet;

public class TwitterTimelineActivity extends FragmentActivity implements ComposeDialogListener {

	private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private ArrayAdapter<Tweet> aTweets;
    private ListView lvTweets;
    private long lowestId = 0;
    private long maxId = 1;
    private int lastLoadCount = 0;
    
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
	  log("Since id is " + lowestId);
	  client.getHomeTimeline(lowestId+1, new JsonHttpResponseHandler(){
		@SuppressLint("NewApi") @Override
		public void onSuccess(JSONArray jsonArray) {
			aTweets.addAll(Tweet.fromJSONArray(jsonArray));
			
			for (int i=0; i<aTweets.getCount(); i++) {
				long uid = aTweets.getItem(i).getUid();
				lowestId = i== 0 ? uid : Math.min(uid, lowestId);
				maxId = i== 0 ? uid :Math.max(uid, maxId);
			}
		}
		  @Override
		public void onFailure(Throwable arg0, String arg1) {
			// TODO Auto-generated method stub
			super.onFailure(arg0, arg1);
		}
	  });
		
	}

	@Override
	public void onPostNewTweet(Tweet newTweet) {
	   this.tweets.add(0, newTweet);
	   aTweets.notifyDataSetChanged();
		
	}


}

package com.maryamq.basictwitter.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.TwitterApplication;
import com.maryamq.basictwitter.activities.TwitterListFragment.IDataFetcher;
import com.maryamq.basictwitter.models.Tweet;

public class SearchActivity extends FragmentActivity implements IDataFetcher {
	String query = "";
	public static final String SEARCH_QUERY = "query";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getActionBar().setBackgroundDrawable(new ColorDrawable(0xFF55ACEE));
		query = getIntent().getStringExtra(SEARCH_QUERY);
		getActionBar().setTitle("Search: " + query);
		
		FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.flContainer, new TwitterListFragment());
		ft.commit();

	}

	@Override
	public List<Tweet> getInitialTweets() {
		// TODO Auto-generated method stub
		return new ArrayList<Tweet>(); // empty
	}

	@Override
	public void getMoreTweets(Fragment fragment, long from_id,
			JsonHttpResponseHandler responseHandler) {
		// TODO Auto-generated method stub
		TwitterApplication.getRestClient().searchTweetsFrom(query, from_id,
				responseHandler);
	}

	@Override
	public void getLastestTweets(Fragment fragment, long since_id,
			JsonHttpResponseHandler jsonResponseHandler) {
		final JsonHttpResponseHandler responseHandler = jsonResponseHandler;
		TwitterApplication.getRestClient().searchTweetsSince(query, since_id,
				new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, JSONObject json) {
				try {
					responseHandler.onSuccess(arg0, json.getJSONArray("statuses"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// TODO Auto-generated method stub	
			}
			@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
				      responseHandler.onFailure(arg0, arg1);
					}
			
		});
	}

	@Override
	public void persist(List<Tweet> tweets) {
	  // do not persist	
	}
}

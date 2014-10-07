package com.maryamq.basictwitter.activities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.TwitterApplication;
import com.maryamq.basictwitter.adapters.TweetArrayAdapter;
import com.maryamq.basictwitter.client.TwitterClient;
import com.maryamq.basictwitter.client.Utils;
import com.maryamq.basictwitter.dialog.ComposeDialog;
import com.maryamq.basictwitter.dialog.ComposeDialog.ComposeDialogListener;
import com.maryamq.basictwitter.dialog.ComposeDialog.Mode;
import com.maryamq.basictwitter.listeners.EndlessScrollListener;
import com.maryamq.basictwitter.models.Tweet;

public class TwitterListFragment extends Fragment implements ComposeDialogListener {
	private List<Tweet> tweets;
	private ArrayAdapter<Tweet> aTweets;
	private ListView lvTweets;

	private TwitterClient client;
	private FragmentManager fm;
	private SwipeRefreshLayout swipeContainer;

	private long maxIdValue = 0;
	private long since_id = 1;
	private Context applicationContext;

	public interface IDataFetcher extends Serializable {
		public List<Tweet> getInitialTweets();
		public void getMoreTweets(Fragment fragment, long from_id, JsonHttpResponseHandler responseHandler);

		public void getLastestTweets(Fragment fragment, long since_id,
				JsonHttpResponseHandler responseHandler);
		public void persist(List<Tweet> tweets);
	}
	


	private final class TimelineFromResponseHandler extends JsonHttpResponseHandler {
		@SuppressLint("NewApi")
		@Override
		public void onSuccess(JSONArray jsonArray) {
			ArrayList<Tweet> newTweets = Tweet.fromJSONArray(jsonArray);
			aTweets.addAll(newTweets);
			updateIds();
			((IDataFetcher)getActivity()).persist(newTweets);
		}

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			// TODO Auto-generated method stub
			Utils.showToast(applicationContext, "Failure: " + arg1);
			Utils.log("Failure: " + arg1);
			// Handle known errors more elegantl.
			super.onFailure(arg0, arg1);
		}
	}

	private final class TimelineSinceResponseHandler extends JsonHttpResponseHandler {
		@Override
		public void onSuccess(JSONArray jsonArray) {
			swipeContainer.setRefreshing(false);
			List<Tweet> newTweets =Tweet.fromJSONArray(jsonArray);
			for (int i = newTweets.size() - 1; i >= 0; i--) {
				// de dup. make sure we don't add duplciate tweets.
				Tweet aTweet = newTweets.get(i);
				Tweet existingTweet = Tweet.getTweet(aTweet.getUid());
				if (existingTweet == null || !tweets.contains(existingTweet)) {
					tweets.add(0, newTweets.get(i));
				}
			}
			updateIds();
			aTweets.notifyDataSetChanged();
			((IDataFetcher)getActivity()).persist(newTweets);
		}

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			// TODO Auto-generated method stub
			Utils.showToast(getActivity().getBaseContext(), "Error: " + arg1);
			Utils.log("Failed to fetch new tweets: " + arg1);
			swipeContainer.setRefreshing(false);
		}

		@Override
		public void handleFailureMessage(Throwable e, String responseBody) {
			Utils.showToast(getActivity().getBaseContext(), "Unexpected failure: "
					+ responseBody);
			Utils.log("Unexpected failure: " + responseBody);
			swipeContainer.setRefreshing(false);
		}
	}

	public interface DataFetcher {
		public List<Tweet> loadMore();
		public List<Tweet> fetchLatest();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_tweet_list, container, false);
		this.applicationContext = getActivity().getApplicationContext();
		if (client == null) {
			client = TwitterApplication.getRestClient();
		}

		lvTweets = (ListView) v.findViewById(R.id.lvTweets);
		lvTweets.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				fetchMore();

			}

		});
		// Load existing tweets;
		tweets = ((IDataFetcher)getActivity()).getInitialTweets();
		aTweets = new TweetArrayAdapter(getActivity(), tweets, client,
				this.getFragmentManager(), this);
		lvTweets.setAdapter(aTweets);

		setupSwipeRefreshLayout(v);
		this.updateIds();

		this.fetchLatest();
		return v;
	}

	public void showComposeDialog() {
		ComposeDialog dialog = new ComposeDialog(client);
		dialog.setResponseHandler(this);
		dialog.show(this.getFragmentManager(), "fragment_compose");
	}

	private void setupSwipeRefreshLayout(View v) {
		swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
		swipeContainer.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				swipeContainer.setRefreshing(true);
				fetchLatest();
			}
		});
		// Configure the refreshing colors
		swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
	}

	private void fetchMore() {
		if (!hasInternet()) {
			return;
		}
		((IDataFetcher)getActivity()).getMoreTweets(TwitterListFragment.this, maxIdValue, new TimelineFromResponseHandler());
	}

	private boolean hasInternet() {
		if (!Utils.isNetworkAvailable(getActivity().getApplicationContext())) {
			Utils.showToast(applicationContext, "Internet unavailale");
			return false;
		}
		return true;
	}

	private void fetchLatest() {
		if (!hasInternet()) {
			swipeContainer.setRefreshing(false);
			return;
		}
		((IDataFetcher)getActivity()).getLastestTweets(
				TwitterListFragment.this, since_id, new TimelineSinceResponseHandler());
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

	@Override
	public void onPostNewTweet(Tweet newTweet, Tweet sourceTweet, Mode mode) {
		
		if (mode == Mode.RETWEET) {
			sourceTweet.setRetweetCount(sourceTweet.getRetweetCount() + 1);
			newTweet.setRetweeted(true);
			newTweet.setIsHomeTimeline(true);
		}
		this.tweets.add(0, newTweet);
		aTweets.notifyDataSetChanged();
		
		List<Tweet> tempTweets= new ArrayList<Tweet>();
		tempTweets.add(newTweet);
		tempTweets.add(sourceTweet); // we updated the retweet count
		// Note that twitter client doesn ot show retweets in timeline
		// but doing it here for demo.
		((IDataFetcher)getActivity()).persist(tempTweets);
		Utils.showToast(getActivity(), "Retweeted!");
		
	}

	public void addTweet(Tweet newTweet) {
		onPostNewTweet(newTweet, null, Mode.COMPOSE);
	}
}

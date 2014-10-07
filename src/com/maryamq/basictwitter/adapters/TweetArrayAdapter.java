package com.maryamq.basictwitter.adapters;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.activities.DetailActivity;
import com.maryamq.basictwitter.activities.ProfileActivity;
import com.maryamq.basictwitter.client.TwitterClient;
import com.maryamq.basictwitter.client.Utils;
import com.maryamq.basictwitter.dialog.ComposeDialog;
import com.maryamq.basictwitter.dialog.ComposeDialog.ComposeDialogListener;
import com.maryamq.basictwitter.dialog.ComposeDialog.Mode;
import com.maryamq.basictwitter.models.Tweet;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

	private final class FavoriteResponseHandler extends JsonHttpResponseHandler {
		private final Button mIbFav;
		private final Tweet mClickedTweet;

		private FavoriteResponseHandler(Button ibFav, Tweet clickedTweet) {
			mIbFav = ibFav;
			mClickedTweet = clickedTweet;

		}

		@Override
		public void onSuccess(JSONArray jsonArray) {
			// no work here

		}

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			// TODO Auto-generated method stub
			Utils.showToast(mIbFav.getContext(), "Failure: " + arg1);
			Utils.log("Failure: " + arg1);
			boolean revertedStatus = !mClickedTweet.isFavorited();
			mClickedTweet.setIsFavorited(revertedStatus);
			mClickedTweet.save();
			int favImgId = revertedStatus ? R.drawable.ic_fav_on : R.drawable.ic_fav_off;
			Drawable img = getContext().getResources().getDrawable(favImgId);
			mIbFav.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
			// Revert UI state
			super.onFailure(arg0, arg1);
		}
	}

	private final class ShowDetailsClickListener implements OnClickListener {
		private final Tweet mClickedTweet;

		private ShowDetailsClickListener(Tweet clickedTweet) {
			mClickedTweet = clickedTweet;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(v.getContext(), DetailActivity.class);
			intent.putExtra("tweet", mClickedTweet);
			v.getContext().startActivity(intent);
		}
	}

	TwitterClient client;
	private FragmentManager fm;
	private ComposeDialogListener dialogListener;

	public TweetArrayAdapter(Context context, List<Tweet> tweets, TwitterClient client,
			FragmentManager fm, ComposeDialogListener dialogListener) {
		super(context, 0, tweets);
		this.client = client;
		this.fm = fm;
		this.dialogListener = dialogListener;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.tweet_item, parent, false);
		}
		ImageView ivProfileImg = (ImageView) convertView.findViewById(R.id.ivProfileImg);
		TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
		TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
		TextView tvName = (TextView) convertView.findViewById(R.id.tvTweeterName);
		TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
		TextView tvRetweeted = (TextView) convertView.findViewById(R.id.tvRetweeted);
		
		Button ibRetweet = (Button) convertView.findViewById(R.id.ibRetweet);
		Button ibReply = (Button) convertView.findViewById(R.id.ibReply);
		final Button ibFav = (Button) convertView.findViewById(R.id.ibFav);

		final Tweet clickedTweet = this.getItem(position);

		int favImgId = clickedTweet.isFavorited() ? R.drawable.ic_fav_on
				: R.drawable.ic_fav_off;
		Drawable img = getContext().getResources().getDrawable(favImgId);
		ibFav.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
		ibFav.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Setting this prematurely for perceived performance.
				boolean newFavStatus = !clickedTweet.isFavorited();
				int favImgId = newFavStatus ? R.drawable.ic_fav_on
						: R.drawable.ic_fav_off;
				Drawable img = getContext().getResources().getDrawable(favImgId);
				ibFav.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
				clickedTweet.setIsFavorited(newFavStatus);
				clickedTweet.save();
				if (newFavStatus) {
					client.favouriteTweet(clickedTweet.getUid(),
							new FavoriteResponseHandler(ibFav, clickedTweet));
				} else {
					client.destroyFavouriteTweet(clickedTweet.getUid(),
							new FavoriteResponseHandler(ibFav, clickedTweet));
				}
			}
		});

		tvBody.setOnClickListener(new ShowDetailsClickListener(clickedTweet));
		ibRetweet.setText(clickedTweet.getRetweetCount() > 0 ? clickedTweet
				.getRetweetCount() + "" : "");
		ibRetweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				ComposeDialog dialog = new ComposeDialog(client, clickedTweet,
						Mode.RETWEET);
				dialog.setResponseHandler(dialogListener);
				dialog.show(fm, "fragment_retweet");*/
				client.retweet(clickedTweet.getUid(), new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, JSONObject json) {
						// TODO Auto-generated method stub
						Tweet newTweet = Tweet.fromJSON(json);
						dialogListener.onPostNewTweet(newTweet, clickedTweet, Mode.RETWEET);
						
					}
					
					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						Utils.log("Retweet failed!");
						super.onFailure(arg0, arg1);
					}
					
				});
			}

		});
		ibReply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ComposeDialog dialog = new ComposeDialog(client, clickedTweet, Mode.REPLY);
				dialog.setResponseHandler(dialogListener);
				dialog.show(fm, "fragment_reply");
			}

		});

		ivProfileImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), ProfileActivity.class);
				intent.putExtra(ProfileActivity.USER_DATA_KEY, clickedTweet.getUser());
				v.getContext().startActivity(intent);
			}
		});
		Tweet tweet = this.getItem(position);
		tvBody.setText(Html.fromHtml(tweet.getBody()));
		tvScreenName.setText("@" + tweet.getUser().getScreenName());
		tvName.setText(tweet.getUser().getName());
		tvName.setTypeface(null, Typeface.BOLD);
		tvTime.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
		Utils.loadImage(ivProfileImg, tweet.getUser().getProfileImageUrl());
		if (tweet.isRetweeted()) {
			tvRetweeted.setText(tweet.getUser().getName() + " retweeted");
			tvRetweeted.setVisibility(View.VISIBLE);
		} else {
			tvRetweeted.setVisibility(View.INVISIBLE);
		}

		// SHow media
		ImageView ivMedia = (ImageView) convertView.findViewById(R.id.ivMedia);
		ivMedia.setOnClickListener(new ShowDetailsClickListener(clickedTweet));
		ivMedia.setImageResource(android.R.color.transparent);
		String mediaUrl = tweet.getMediaUrl();
		Utils.log("Twitter url: " + mediaUrl);
		if (mediaUrl != null && !mediaUrl.isEmpty()) {
			Utils.loadImage(ivMedia, mediaUrl);
			ivMedia.setVisibility(View.VISIBLE);
		} else {
			ivMedia.setVisibility(View.GONE);
		}
		return convertView;
	}

}

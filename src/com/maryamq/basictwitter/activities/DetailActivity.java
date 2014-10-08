package com.maryamq.basictwitter.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.TwitterApplication;
import com.maryamq.basictwitter.Utils;
import com.maryamq.basictwitter.client.TwitterClient;
import com.maryamq.basictwitter.dialog.ComposeDialog;
import com.maryamq.basictwitter.dialog.ComposeDialog.Mode;
import com.maryamq.basictwitter.listeners.FavoriteResponseHandler;
import com.maryamq.basictwitter.models.Tweet;

public class DetailActivity extends FragmentActivity {
	static final String TWEET_DATA_KEY = "tweet";
	private TwitterClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.client = TwitterApplication.getRestClient();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		getActionBar().hide();
		final Tweet tweetData = (Tweet) this.getIntent().getSerializableExtra(
				TWEET_DATA_KEY);

		TextView tvUser = (TextView) this.findViewById(R.id.tvUserName);
		tvUser.setText(tweetData.getUser().getName());

		TextView tvScreenName = (TextView) this.findViewById(R.id.tvHandle);
		tvScreenName.setText("@" + tweetData.getUser().getScreenName());

		TextView tvBody = (TextView) this.findViewById(R.id.tvTweetBody);
		tvBody.setText(Html.fromHtml(tweetData.getBody()));

		ImageView ivProfileImg = (ImageView) this.findViewById(R.id.ivProfilePicture);
		ProgressBar pbLoading = (ProgressBar)this.findViewById(R.id.pbLoading);
		//ImageLoader imgLoader = ImageLoader.getInstance();
		ivProfileImg.setImageResource(android.R.color.transparent);
		Utils.loadImage(ivProfileImg, pbLoading, tweetData.getUser().getProfileImageUrl());

		// SHow media
		ImageView ivMedia = (ImageView) this.findViewById(R.id.ivDetailMedia);
		ivMedia.setImageResource(android.R.color.transparent);
		String mediaUrl = tweetData.getMediaUrl();
		if (mediaUrl != null && !mediaUrl.isEmpty()) {
			Utils.loadImage(ivMedia, pbLoading, mediaUrl);
			ivMedia.setVisibility(View.VISIBLE);
		}

		Button ibRetweet = (Button) this.findViewById(R.id.ibRetweet);
		Button ibReply = (Button) this.findViewById(R.id.ibReply);
		ibRetweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ComposeDialog dialog = new ComposeDialog(client, tweetData, Mode.RETWEET);
				dialog.show(DetailActivity.this.getSupportFragmentManager(),
						"fragment_retweet");
			}

		});
		ibReply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ComposeDialog dialog = new ComposeDialog(client, tweetData, Mode.REPLY);
				dialog.show(DetailActivity.this.getSupportFragmentManager(),
						"fragment_reply");
			}

		});
		
		final Button ibFav = (Button) this.findViewById(R.id.ibFav);
		int favImgId = tweetData.isFavorited() ? R.drawable.ic_fav_on
				: R.drawable.ic_fav_off;
		Drawable img = this.getResources().getDrawable(favImgId);
		ibFav.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
		ibFav.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Setting this prematurely for perceived performance.
				boolean newFavStatus = !tweetData.isFavorited();
				int favImgId = newFavStatus ? R.drawable.ic_fav_on
						: R.drawable.ic_fav_off;
				Drawable img = DetailActivity.this.getResources().getDrawable(favImgId);
				ibFav.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
				tweetData.setIsFavorited(newFavStatus);
				tweetData.persist();
				if (newFavStatus) {
					client.favouriteTweet(tweetData.getUid(),
							new FavoriteResponseHandler(DetailActivity.this, ibFav, tweetData));
				} else {
					client.destroyFavouriteTweet(tweetData.getUid(),
							new FavoriteResponseHandler(DetailActivity.this, ibFav, tweetData));
				}
			}
		});

	}

}

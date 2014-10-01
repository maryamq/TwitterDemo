package com.maryamq.basictwitter.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.R.id;
import com.maryamq.basictwitter.R.layout;
import com.maryamq.basictwitter.client.TwitterClient;
import com.maryamq.basictwitter.dialog.ComposeDialog;
import com.maryamq.basictwitter.dialog.ComposeDialog.Mode;
import com.maryamq.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailActivity extends FragmentActivity {
	static final String TWEET_DATA_KEY = "tweet";
	private TwitterClient client;

	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.client = new TwitterClient(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		getActionBar().hide();
		final Tweet tweetData = (Tweet)this.getIntent().getSerializableExtra(TWEET_DATA_KEY);
		
		TextView tvUser = (TextView)this.findViewById(R.id.tvUserName);
		tvUser.setText(tweetData.getUser().getName());
		
		TextView tvScreenName = (TextView)this.findViewById(R.id.tvHandle);
		tvScreenName.setText("@" + tweetData.getUser().getScreenName());
		
		TextView tvBody = (TextView)this.findViewById(R.id.tvTweetBody);
		tvBody.setText(Html.fromHtml(tweetData.getBody()));
		
		ImageView ivProfileImg = (ImageView)this.findViewById(R.id.ivProfilePicture);
		ImageLoader imgLoader = ImageLoader.getInstance();
		ivProfileImg.setImageResource(android.R.color.transparent);
		imgLoader.displayImage(tweetData.getUser().getProfileImageUrl(),
				ivProfileImg);
		
		Button ibRetweet = (Button) this.findViewById(R.id.ibRetweet);
		Button ibReply = (Button) this.findViewById(R.id.ibReply);
		ibRetweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ComposeDialog dialog = new ComposeDialog(client, tweetData,
						Mode.RETWEET);
				dialog.show(DetailActivity.this.getSupportFragmentManager(), "fragment_retweet");
			}

		});
		ibReply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ComposeDialog dialog = new ComposeDialog(client, tweetData,
						Mode.REPLY);
				dialog.show(DetailActivity.this.getSupportFragmentManager(), "fragment_reply");
			}

		});
	}

	

}

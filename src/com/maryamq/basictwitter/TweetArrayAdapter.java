package com.maryamq.basictwitter;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.maryamq.basictwitter.ComposeDialog.Mode;
import com.maryamq.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TweetArrayAdapter extends ArrayAdapter<Tweet> {

	TwitterClient client;
	private FragmentManager fm;
	
	public TweetArrayAdapter(Context context, List<Tweet> tweets, TwitterClient client, FragmentManager fm) {
		super(context, 0, tweets);
		this.client = client;
		this.fm = fm;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.tweet_item, parent, false);
		}
		ImageView ivProfileImg = (ImageView) convertView
				.findViewById(R.id.ivProfileImg);
		TextView tvScreenName = (TextView) convertView
				.findViewById(R.id.tvScreenName);
		TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
		TextView tvName = (TextView) convertView.findViewById(R.id.tvTweeterName);
		TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
		ImageButton ibRetweet = (ImageButton)convertView.findViewById(R.id.ibRetweet);
		ImageButton ibReply = (ImageButton)convertView.findViewById(R.id.ibReply);
		
		final Tweet clickedTweet = this.getItem(position);
		ibRetweet.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				 ComposeDialog dialog = new ComposeDialog(client, clickedTweet, Mode.RETWEET);
				 dialog.show(fm, "fragment_retweet");				
			}
			
		});
		ibReply.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				 ComposeDialog dialog = new ComposeDialog(client, clickedTweet, Mode.REPLY);
				 dialog.show(fm, "fragment_reply");				
			}
			
		});
		Tweet tweet = this.getItem(position);
		tvBody.setText(Html.fromHtml(tweet.getBody()));
		tvScreenName.setText("@" + tweet.getUser().getScreenName());
		tvName.setText(tweet.getUser().getName());
		tvName.setTypeface(null, Typeface.BOLD);
		tvTime.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
		ImageLoader imgLoader = ImageLoader.getInstance();
		ivProfileImg.setImageResource(android.R.color.transparent);
		imgLoader.displayImage(tweet.getUser().getProfileImageUrl(), ivProfileImg);
		return convertView;
	}

}

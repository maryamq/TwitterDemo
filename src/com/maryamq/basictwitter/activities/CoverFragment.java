package com.maryamq.basictwitter.activities;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.client.Utils;
import com.maryamq.basictwitter.models.Tweet;

public class CoverFragment extends Fragment {

	private Tweet tweet;

	public CoverFragment (Tweet tweet) {
		this.tweet = tweet;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_cover, container, false);
		ImageView ivCover = (ImageView)v.findViewById(R.id.ivCoverPhoto);
		ImageView ivProfilePic = (ImageView)v.findViewById(R.id.ivProfilePic);
		TextView tvUserName = (TextView)v.findViewById(R.id.tvUserName);
		TextView tvHandle = (TextView)v.findViewById(R.id.tvHandle);
		Utils.loadImage(ivProfilePic, tweet.getUser().getProfileImageUrl());
		Utils.loadImage(ivCover, tweet.getUser().getCoverUrl());
		tvHandle.setText("@" + tweet.getUser().getScreenName());
		tvUserName.setText(tweet.getUser().getName());
	
		return v;
	}
	
	
}

package com.maryamq.basictwitter.activities;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.client.Utils;
import com.maryamq.basictwitter.models.User;

public class CoverFragment extends Fragment {

	private User user;

	public CoverFragment (User user) {
		this.user = user;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	

	@SuppressLint("NewApi") @Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_cover, container, false);
		ImageView ivCover = (ImageView)v.findViewById(R.id.ivCoverPhoto);
		ImageView ivProfilePic = (ImageView)v.findViewById(R.id.ivProfilePic);
		TextView tvUserName = (TextView)v.findViewById(R.id.tvUserName);
		TextView tvHandle = (TextView)v.findViewById(R.id.tvHandle);
		TextView tvTweets = (TextView)v.findViewById(R.id.tvTweets);
		TextView tvFollowing = (TextView)v.findViewById(R.id.tvFollowing);
		TextView tvFollowers = (TextView)v.findViewById(R.id.tvFollowers);
		
		Utils.loadImage(ivProfilePic, user.getProfileImageUrl());
		Utils.loadImage(ivCover, user.getCoverUrl());
		tvHandle.setText("@" + user.getScreenName());
		
		WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		tvUserName.setText(user.getName());
		
		String htmlText = "%s<br/><span style='color:#8D8D8D'>%s</span>";
		tvTweets.setText(Html.fromHtml(String.format(htmlText, user.getTweetsCount(), "TWEETS")));
		tvTweets.setWidth(display.getWidth()/3);
		
		tvFollowers.setText(Html.fromHtml(String.format(htmlText, user.getFollowersCount(), "FOLLOWERS")));
		tvFollowers.setWidth(display.getWidth()/3);
		
		tvFollowing.setText(Html.fromHtml(String.format(htmlText, user.getFriendsCount(), "FOLLOWING")));
		tvFollowing.setWidth(display.getWidth()/3);
	
		return v;
	}
	
	
}

package com.maryamq.basictwitter.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.Utils;
import com.maryamq.basictwitter.adapters.CoverPagerAdapter;
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
		//ImageView ivProfilePic = (ImageView)v.findViewById(R.id.ivProfilePic);
		//TextView tvUserName = (TextView)v.findViewById(R.id.tvUserName);
		//TextView tvHandle = (TextView)v.findViewById(R.id.tvHandle);
		TextView tvTweets = (TextView)v.findViewById(R.id.tvTweets);
		TextView tvFollowing = (TextView)v.findViewById(R.id.tvFollowing);
		TextView tvFollowers = (TextView)v.findViewById(R.id.tvFollowers);
		
		ProgressBar pbLoading = (ProgressBar)v.findViewById(R.id.pbLoading);
		//Utils.loadImage(ivProfilePic, user.getProfileImageUrl());
		Utils.loadImage(ivCover, pbLoading, user.getCoverUrl());
		//tvHandle.setText("@" + user.getScreenName());
		

		//tvUserName.setText(user.getName());
		
		String htmlText = "<b>%s</b><br/><font color='color:##8899a6'>%s</font>";
		tvTweets.setText(Html.fromHtml(String.format(htmlText, user.getTweetsCount(), "TWEETS")));

		tvFollowers.setText(Html.fromHtml(String.format(htmlText, user.getFollowersCount(), "FOLLOWERS")));
		
		tvFollowing.setText(Html.fromHtml(String.format(htmlText, user.getFriendsCount(), "FOLLOWING")));
		
		ViewPager vpPager = (ViewPager) v.findViewById(R.id.vpPager);
		CoverPagerAdapter adapterViewPager = new CoverPagerAdapter(this.getFragmentManager(), user);
		vpPager.setAdapter(adapterViewPager);
	
		return v;
	}
	
	
}

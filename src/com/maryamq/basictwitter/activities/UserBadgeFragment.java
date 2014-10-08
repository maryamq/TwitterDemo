package com.maryamq.basictwitter.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.client.Utils;
import com.maryamq.basictwitter.models.User;

public class UserBadgeFragment extends Fragment {

	private User user;

	public UserBadgeFragment(User user) {
		this.user = user;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_user_badge, container, false);
		ImageView ivProfilePic = (ImageView) v.findViewById(R.id.ivProfilePic);
		TextView tvUserName = (TextView) v.findViewById(R.id.tvUserName);
		TextView tvHandle = (TextView) v.findViewById(R.id.tvHandle);
		ProgressBar pbLoading = (ProgressBar)v.findViewById(R.id.pbLoading);

		Utils.loadImage(ivProfilePic, pbLoading, user.getProfileImageUrl());
		tvHandle.setText("@" + user.getScreenName());
		tvUserName.setText(user.getName());
		return v;
	}
}

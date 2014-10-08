package com.maryamq.basictwitter.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.Utils;
import com.maryamq.basictwitter.models.User;

public class UserTaglineFragment extends Fragment {

	private String tagline;

	public UserTaglineFragment(String tagline) {
		this.tagline = tagline;
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
		View v = inflater.inflate(R.layout.fragment_user_tagline, container, false);
		TextView tvTagline = (TextView) v.findViewById(R.id.tvTagline);
		tvTagline.setText(tagline);
		return v;
	}
}

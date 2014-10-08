package com.maryamq.basictwitter.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.maryamq.basictwitter.fragments.UserBadgeFragment;
import com.maryamq.basictwitter.fragments.UserTaglineFragment;
import com.maryamq.basictwitter.models.User;

public class CoverPagerAdapter extends FragmentPagerAdapter {
	private static int NUM_ITEMS = 2;
	private User user;
	

	public CoverPagerAdapter(FragmentManager fragmentManager,  User user) {
		super(fragmentManager);
		this.user = user;
	}

	// Returns total number of pages
	@Override
	public int getCount() {
		return NUM_ITEMS;
	}

	// Returns the fragment to display for that page
	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case 0: // Fragment # 0 - This will show FirstFragment
			return new UserBadgeFragment(user);
		case 1: // Fragment # 0 - This will show FirstFragment different title
			return new UserTaglineFragment(user.getTagline());

		default:
			return null;
		}
	}

}

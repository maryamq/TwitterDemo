package com.maryamq.basictwitter.adapters;

import org.json.JSONArray;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Button;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.client.Utils;
import com.maryamq.basictwitter.models.Tweet;

public final class FavoriteResponseHandler extends JsonHttpResponseHandler {
	private final Context context;
	private final Button mIbFav;
	private final Tweet mClickedTweet;

	public FavoriteResponseHandler(Context context, Button ibFav, Tweet clickedTweet) {
		this.context = context;
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
		mClickedTweet.persist();
		int favImgId = revertedStatus ? R.drawable.ic_fav_on : R.drawable.ic_fav_off;
		Drawable img = context.getResources().getDrawable(favImgId);
		mIbFav.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
		// Revert UI state
		super.onFailure(arg0, arg1);
	}
}
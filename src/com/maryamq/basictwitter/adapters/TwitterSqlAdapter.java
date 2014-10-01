package com.maryamq.basictwitter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.R.id;
import com.maryamq.basictwitter.R.layout;
import com.maryamq.basictwitter.dialog.ComposeDialog.Mode;
import com.maryamq.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class TwitterSqlAdapter extends SimpleCursorAdapter {

	Context context;
	
	public TwitterSqlAdapter(Context context) {
		super(context, R.layout.tweet_item, null, null, null, 0);
		this.context = context;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		return LayoutInflater.from(this.context).inflate(R.layout.tweet_item, parent);
	}
	@Override
	public void bindView(View convertView, Context context, Cursor cursor) {
	
		int bodyIndex = cursor.getColumnIndex("text");
		TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
		tvBody.setText(Html.fromHtml(cursor.getString(bodyIndex)));
		/*
		tvScreenName.setText("@" + tweet.getUser().getScreenName());
		tvName.setText(tweet.getUser().getName());
		tvName.setTypeface(null, Typeface.BOLD);
		tvTime.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
		ImageLoader imgLoader = ImageLoader.getInstance();
		ivProfileImg.setImageResource(android.R.color.transparent);
		imgLoader.displayImage(tweet.getUser().getProfileImageUrl() */
		
	}

}

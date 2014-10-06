package com.maryamq.basictwitter.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.maryamq.basictwitter.models.Tweet;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Utils {

	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void log(String message) {
		Log.d("DEBUG", message);
	}

	public static void loadImage(ImageView ivProfileImg, String url) {
		if (url == null || url.isEmpty()) {
			return;
		}
		ImageLoader imgLoader = ImageLoader.getInstance();
		ivProfileImg.setImageResource(android.R.color.transparent);
		imgLoader.displayImage(url, ivProfileImg);
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public static String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
		sf.setLenient(true);

		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS,
					DateUtils.FORMAT_ABBREV_RELATIVE | DateUtils.FORMAT_ABBREV_TIME)
					.toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		relativeDate = relativeDate.replace(" hour", "hr");
		relativeDate = relativeDate.replace(" mins", "m");
		relativeDate = relativeDate.replace(" ago", "");
		return relativeDate;
	}

}

package com.maryamq.basictwitter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.Toast;

public class Utils {
	
	public static void showToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static String getRelativeTimeAgo(String rawJsonDate) {
		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat,
				Locale.ENGLISH);
		sf.setLenient(true);

		String relativeDate = "";
		try {
			long dateMillis = sf.parse(rawJsonDate).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE | DateUtils.FORMAT_ABBREV_TIME)
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

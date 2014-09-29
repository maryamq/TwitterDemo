package com.codepath.apps.restclienttemplate;

import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.TwitterClient;
import com.maryamq.basictwitter.Utils;
import com.maryamq.basictwitter.models.Tweet;

public class ComposeDialog extends DialogFragment {
	private TwitterClient client;
	private EditText etNewTweet;
	private Button btnPostNewTweet;
	private ImageButton btnBack;

	public ComposeDialog(TwitterClient client) {
		this.client = client;
	}

	public interface ComposeDialogListener {
		void onPostNewTweet(Tweet newTweet);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_compose_tweet, container);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		etNewTweet = (EditText) v.findViewById(R.id.etNewTweet);
		btnBack = (ImageButton)v.findViewById(R.id.btnBack);
		etNewTweet.requestFocus();
		btnPostNewTweet = (Button) v.findViewById(R.id.btnPostNewTweet);
		btnPostNewTweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String text = etNewTweet.getText().toString();
				if (text.isEmpty()) {
					Utils.showToast(v.getContext(), "No Tweet specified");
				}

				if (text.length() > 140) {
					Utils.showToast(v.getContext(),
							"Message longer than 140 chars");
				}
				final View view = v;
				client.postTweet(text, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int arg0, JSONObject json) {
						Tweet newTweet = Tweet.fromJSON(json);
						if (newTweet == null) {
							Utils.showToast(view.getContext(),
									"Unable to retrieve tweet");
						} else {
							ComposeDialogListener listener = (ComposeDialogListener) getActivity();
							listener.onPostNewTweet(newTweet);
							dismiss();
						}
					}
				});

			}

		});
		
		
		btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				dismiss();
				
			}
			
		});
		return v;
	}

}

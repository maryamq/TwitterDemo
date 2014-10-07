package com.maryamq.basictwitter.dialog;

import java.io.Serializable;

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
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.maryamq.basictwitter.R;
import com.maryamq.basictwitter.client.TwitterClient;
import com.maryamq.basictwitter.client.Utils;
import com.maryamq.basictwitter.models.Tweet;

public class ComposeDialog extends DialogFragment {
	private TwitterClient client;
	private EditText etNewTweet;
	private Button btnPostNewTweet;
	private ImageButton btnBack;

	private Tweet tweet;
	private Mode mode;
	private ComposeDialogListener listener;

	private final class PostTweetClickHandler implements OnClickListener {
		private final class PostTweetJsonResponseHandler extends
				JsonHttpResponseHandler {
			private final View mView;

			private PostTweetJsonResponseHandler(View view) {
				mView = view;
			}

			@Override
			public void onSuccess(int arg0, JSONObject json) {
				Tweet newTweet = Tweet.fromJSON(json);
				if (newTweet == null) {
					Utils.showToast(mView.getContext(),
							"Unable to retrieve tweet");
				} else {
					listener.onPostNewTweet(newTweet, tweet, mode);
					dismiss();
				}
			}
		}

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
			if (mode == Mode.COMPOSE) {
				client.postNewTweet(text, new PostTweetJsonResponseHandler(view));
			} else {
				client.postNewTweet(text, tweet.getUid(), new PostTweetJsonResponseHandler(view));
			}

		}
	}

	public enum Mode {
		COMPOSE, RETWEET, REPLY
	};

	public ComposeDialog(TwitterClient client) {
		this(client, null, Mode.COMPOSE);
	}

	public ComposeDialog(TwitterClient client, Tweet tweet, Mode mode) {
		this.client = client;
		this.tweet = tweet;
		this.mode = mode;
		
	}

	public interface ComposeDialogListener extends Serializable {
		void onPostNewTweet(Tweet newTweet, Tweet sourceTweet, Mode mode);
	}

	private void setTitle(TextView tvTitle) {
		String title = "";
		switch (this.mode) {
		case RETWEET:
			title = "Retweet";
			break;
		case REPLY:
			title = "Reply";
			break;
		default:
			title = "Compose";
		}
		tvTitle.setText(title);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_compose_tweet, container);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setTitle((TextView) v.findViewById(R.id.tvCompose)); // set dialog title
		
		etNewTweet = (EditText) v.findViewById(R.id.etNewTweet);
		if (this.mode == Mode.REPLY) {
			etNewTweet.setText("@" + tweet.getUser().getScreenName());
		} 
		
		
		etNewTweet.requestFocus();
		btnPostNewTweet = (Button) v.findViewById(R.id.btnPostNewTweet);
		btnPostNewTweet.setOnClickListener(new PostTweetClickHandler());

		btnBack = (ImageButton) v.findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}

		});
		return v;
	}

	public void setResponseHandler(ComposeDialogListener listener) {
		this.listener = listener;
		// TODO Auto-generated method stub
		
	}

}

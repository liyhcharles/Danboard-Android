package com.danboard.activity;

import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.danboard.R;

public class ViewPostActivity extends Activity {

	private Bitmap postBitmap;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_post);

		Intent intent = getIntent();
		String message = intent.getStringExtra(DisplayPostsActivity.OPEN_POST);
		
		try {
			URL postFileUrl = new URL(message);
			GetPostBitmapTask task = new GetPostBitmapTask();
			task.execute(postFileUrl);
		} catch (Exception e) {
			Log.e("post", e.toString());
		}
		
		setupActionBar();

	}

	private class GetPostBitmapTask extends AsyncTask<URL, Void, Void> {

		ProgressDialog progress;

		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(ViewPostActivity.this,
					res.getString(R.string.load_post),
					res.getString(R.string.load_message), true);
		}

		@Override
		protected Void doInBackground(URL... urls) {
			try {
				ViewPostActivity.this.postBitmap = BitmapFactory
						.decodeStream(urls[0].openStream());
			} catch (Exception e) {
				Log.e("post",
						"Error while downloading post bitmap:\n" + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout01);
			rl.removeAllViews();
			ImageView postImage = new ImageView(ViewPostActivity.this);
			postImage.setImageBitmap(ViewPostActivity.this.postBitmap);
			rl.addView(postImage);
			try {
				progress.dismiss();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_image, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		//case R.id.action_search:
			//openSearch();
		//	return true;
		case R.id.action_settings:
			//openSettings();
			return true;
		};
		return super.onOptionsItemSelected(item);
	}
	

}

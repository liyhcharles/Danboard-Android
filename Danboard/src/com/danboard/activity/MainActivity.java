package com.danboard.activity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.danboard.R;

public class MainActivity extends ListActivity {
	
	private static final String[] SOURCES = new String[] { "Danbooru", "Konachan" };
	public static final String HOST = "com.danboard.activity.MainActivity.HOST";
	
	// Sources Map will contain map of host to URL
	public static final Map<String, String> sourcesMap = initializeMap();

	private static Map<String, String> initializeMap() {
	  Map<String, String> m = new HashMap<String, String>();
	  m.put("Danbooru","http://danbooru.donmai.us");  
	  m.put("Konachan","http://konachan.net");  
	  return Collections.unmodifiableMap(m);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		// List of Sources
		setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_main, SOURCES));
 
		final ListView listView = getListView();
		listView.setTextFilterEnabled(true);
 
		// Selects which image host to choose from, based on what is clicked
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String source = (String) listView.getItemAtPosition(position);
			    showPosts(sourcesMap.get(source));
			}
		});
		
		// Message if no network connection is found
		if (!isNetworkConnected()) {
			Toast.makeText(getApplicationContext(), "No internet connection is available, connect to one to use Danboard",
					   Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Method to call DisplayPostsActivity, while sending in host name
	 * @param host
	 */
	public void showPosts(String host) {
		Intent intent = new Intent(this, DisplayPostsActivity.class);
		intent.putExtra(HOST, host);
		startActivity(intent);
	}
	
	/**
	 * Determines if there is a network
	 * @return
	 */
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		} else {
			return true;
		}
	}

}

package com.danboard.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import com.danboard.PostRequest;
import com.danboard.R;
import com.danboard.model.Post;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

public class DisplayPostsActivity extends Activity {
	
	public static final String OPEN_POST = "com.danboard.activity.DisplayPostsActivity";
	
	// initialize posts and ImageLoader
	private List<Post> posts;
	protected ImageLoader imageLoader;
	private GridView gridView;
	
	//check if loading more images
	private boolean loadingMore = true;
	private boolean stopLoadingData = false;
		
	// host is site name
	private static String host;
	private int page;		
	private List<String> tags = new ArrayList<String>();	
	private static String keepQuery; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_image);
		setup();
		// show action bar
        setupActionBar();
        handleIntent(getIntent());
	}
	
	/**
	 * Initialize values and setup GridView and ImageLoader
	 */
	private void setup() {
		
		// Get host name if not from a search
		Bundle bundle = getIntent().getExtras();
		if (bundle.getString(MainActivity.HOST) != null) {
			host = bundle.getString(MainActivity.HOST);
		}
		
		page = 1;
		
		// Regenerate list of posts
		if (imageLoader != null) {
			imageLoader.destroy();
		}
		posts = new ArrayList<Post>();
		posts.clear();
		
		// Create global configuration and initialize ImageLoader with this configuration
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.resetViewBeforeLoading(true)
		.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(defaultOptions)
		.build();
		
		ImageLoader.getInstance().init(config);
		
		imageLoader = ImageLoader.getInstance();
		
		// Create GridView settings
		gridView = (GridView) findViewById(R.id.gridview);
		
		// GridView PauseOnScrollListener
		boolean pauseOnScroll = false; // or true
		boolean pauseOnFling = true; // or false
		PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader, pauseOnScroll, pauseOnFling);
		gridView.setOnScrollListener(listener);
				
		// GridView - Open image when tapped		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent intent = new Intent(DisplayPostsActivity.this, ImagePagerActivity.class);
				intent.putExtra("IMAGE_POSITION", position);
	            // Konachan has different format
				if (host.equals("http://konachan.net")) {
					intent.putExtra(MainActivity.HOST, "");
				}
				else {
					intent.putExtra(MainActivity.HOST, host);
				}
				startActivity(intent);
			}
		});
				
		// GridView - Endless scroll
		gridView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
					
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				int lastInScreen = firstVisibleItem + visibleItemCount;
				if ((lastInScreen == totalItemCount) && !(loadingMore)) {
					if (stopLoadingData == false) {
						// Get more photos on scroll
						new loadMorePhotos().execute();
					}
				}
			}
		});		
	}

	/**
	 * Display posts in GridView
	 */
	private void displayPosts() {
		// Get initial batch of photos
		new getPhotosData().execute();
	}
	
	/**
	 * Grabs initial batch of photos
	 * @author Charles
	 *
	 */
	private class getPhotosData extends AsyncTask<Void, Void, Void> {
		ProgressDialog progress;
		
		@Override
		protected void onPreExecute() {
			Resources res = getResources();
			progress = ProgressDialog.show(DisplayPostsActivity.this,
					res.getString(R.string.load_post),
					res.getString(R.string.load_message_posts), true);
		}
		
	    @Override
	    protected Void doInBackground(Void... arg0) {
	        // CHANGE THE LOADING MORE STATUS TO PREVENT DUPLICATE CALLS FOR
	        // MORE DATA WHILE LOADING A BATCH
	        loadingMore = true;
			PostRequest request = new PostRequest(host, page, tags);
			request.clearPosts();
			request.execute();
			posts = request.getPosts();
	        return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	    	try {
				progress.dismiss();
			} catch (Exception e) {
			}
	        // SET THE ADAPTER TO THE GRIDVIEW
	        gridView.setAdapter(new ImageAdapter());
	        // CHANGE THE LOADING MORE STATUS
	        loadingMore = false;
	    }
	}
	
	/**
	 * Loads more photos
	 * @author Charles
	 *
	 */
	private class loadMorePhotos extends AsyncTask<Void, Void, Void> {
	    @Override
	    protected Void doInBackground(Void... arg0) {
	    	// INCREMENT CURRENT PAGE
	        page++;
	        // CHANGE THE LOADING MORE STATUS TO PREVENT DUPLICATE CALLS FOR
	        // MORE DATA WHILE LOADING A BATCH
	        loadingMore = true;
	        PostRequest request = new PostRequest(host, page, tags);
			request.execute();
	        posts = request.getPosts();
	        return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	        // get listview current position - used to maintain scroll position
	        int currentPosition = gridView.getFirstVisiblePosition();
	        // APPEND NEW DATA TO THE ARRAYLIST AND SET THE ADAPTER TO THE LISTVIEW
	        gridView.setAdapter(new ImageAdapter());
	        // Setting new scroll position
	        gridView.setSelection(currentPosition + 1);
	        // SET LOADINGMORE "FALSE" AFTER ADDING NEW FEEDS TO THE EXISTING LIST
	        loadingMore = false;
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
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_image, menu);
		
		 // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    // Assumes current activity is the searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    searchView.setSubmitButtonEnabled(false);
	    searchView.setQuery(keepQuery, false);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*
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
		case R.id.action_search:
			//openSearch();
			//onSearchRequested();
			return true;
		case R.id.action_settings:
			//openSettings();
			return true;
		};
		*/
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}

	// handles search intent
	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			keepQuery = query;
			tags = Arrays.asList(query.split("\\s+"));
			displayPosts();
	    }
		else {
			displayPosts();
		}
	}
	
	
	/**
	 * ImageAdapter class for GridView
	 * @author Charles Li
	 *
	 */
	public class ImageAdapter extends BaseAdapter {
				
        @Override
        public int getCount() {
            return posts.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView imageView;
            
            // get display information
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics ();
            display.getMetrics(outMetrics);
            
            if (convertView == null) {            
            	convertView = (ImageView) getLayoutInflater().inflate(R.layout.item_grid_image, null);
            	imageView = (ImageView) convertView.findViewById(R.id.image);
            	imageView.setLayoutParams(new GridView.LayoutParams(outMetrics.widthPixels/3, outMetrics.widthPixels/3));
            	imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }
            
            // Konachan has different format
            if (host.equals("http://konachan.net")) {
                imageLoader.displayImage(posts.get(position).getPreviewUrl(), imageView);
            }
            else {
                imageLoader.displayImage(host + posts.get(position).getPreviewUrl(), imageView);
            }
            
            return imageView;
        }
		
    }

}

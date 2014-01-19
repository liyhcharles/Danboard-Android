package com.danboard;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.danboard.model.Post;
import com.danboard.util.IOHelpers;
import android.util.Log;

public class PostRequest {
	
	private JSONArray response;
	private String host;
	private String resource;
	private int limit;
	private String rating;
	private int page;
	private List<String> tags;
	
	private static ArrayList<Post> posts = new ArrayList<Post>();

	private void init() {
		host = "danbooru.donmai.us";
		rating = "Safe";
		resource = "/post/index.json";
		limit = 18;
		tags = new ArrayList<String>();
		page = 1;
	}

	/**
	 * A request returning a json file with a filtered post list
	 */
	public PostRequest() {
		init();
	}
	
	/**
	 * PostRequest with page number
	 */
	public PostRequest(int page) {
		init();
		this.page = page;
	}

	/**
	 * PostRequest with tags and page number
	 * @param page
	 * @param tags
	 */
	public PostRequest(int page, List<String> tags) {
		init();
		this.page = page;
		this.tags = tags;
	}
	
	/**
	 * Most used, PostRequest with host, page and tags
	 * @param host
	 * @param page
	 * @param tags
	 */
	public PostRequest(String host, int page, List<String> tags) {
		init();
		this.host = host;
		this.page = page;
		this.tags = tags;
	}
	
	/**
	 * Clear posts
	 */
	public void clearPosts() {
		posts.clear();
	}

	/**
	 * @return A json array containing all the posts
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws JSONException
	 */
	public List<Post> execute() {
		String url = host + resource + "?limit=" + limit
				+ "&page=" + page + "&tags=";
		if (!(rating.equalsIgnoreCase("All"))) {
			url += "rating:" + rating;
		}
		for (String t : tags) {
			url = url + "%20" + t;
		}

		try {
			URL jsonUrl = new URL(url);
			InputStream jsonStream = jsonUrl.openStream();
			String jsonString = IOHelpers.convertStreamToString(jsonStream);
			response = new JSONArray(jsonString);

			int i = 0;
			int length = response.length();

			// for each entry we create a post object and populate its
			// fields with the values obtained from the json file
			for (i = 0; i < length; i++) {
				JSONObject entry = response.getJSONObject(i);
				int id = entry.getInt("id");

				String previewUrl = entry.getString("preview_url");
				String fileUrl = entry.getString("file_url");
				int fileSize = entry.getInt("file_size");

				Post p = new Post(id, previewUrl, fileUrl, fileSize);

				// add the constructed post to the list
				posts.add(p);
			}

		} catch (Exception e) {
			Log.e("Request Exception", e.toString());
		}
		return posts;
	}

	public int getLimit() {
		return limit;
	}

	public int getPage() {
		return page;
	}
	
	/**
	 * Return list of posts
	 * @return
	 */
	public List<Post> getPosts() {
		return posts;
	}
}

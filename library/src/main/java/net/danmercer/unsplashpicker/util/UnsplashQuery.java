package net.danmercer.unsplashpicker.util;

import android.util.Log;

import net.danmercer.unsplashpicker.data.PhotoInfo;
import net.danmercer.unsplashpicker.util.async.JsonTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Dan Mercer
 */
public class UnsplashQuery {
	private static final String TAG = "UnsplashQuery";

	public interface OnLoadedListener {
		void onPhotosLoaded(List<PhotoInfo> photos);
	}

	private String searchQuery = null;
	private boolean isNew = false;
	private final String appID;
	private int pageNumber = 1;
	private int numPerPage = 20;
	private boolean isLoading;

	public UnsplashQuery(String appID) {
		this.appID = appID;
	}

	public UnsplashQuery numPerPage(int num) {
		this.numPerPage = num;
		return this;
	}

	public UnsplashQuery nextPage() {
		pageNumber++;
		return this;
	}

	public UnsplashQuery setSearch(String searchQuery) {
		if (!searchQuery.equals(this.searchQuery)) {
			reset();
			this.searchQuery = searchQuery;
		}
		return this; // for chaining
	}

	public UnsplashQuery cancelSearch() {
		if (searchQuery != null) {
			reset();
			this.searchQuery = null;
		}
		return this; // for chaining
	}

	private void reset() {
		isNew = true;
		pageNumber = 1;
	}

	public String toURL() {
		if (searchQuery == null) {
			return new QueryStringBuilder("https://api.unsplash.com/photos/curated")
					.add("client_id", appID)
					.add("page", pageNumber)
					.add("per_page", numPerPage)
					.build();
		} else {
			return new QueryStringBuilder("https://api.unsplash.com/search/photos")
					.add("client_id", appID)
					.add("query", searchQuery)
					.add("page", pageNumber)
					.add("per_page", numPerPage)
					.build();
		}
	}

	public void load(final OnLoadedListener onLoadedListener) {
		isNew = false;
		if (searchQuery == null) {

			// Just load photos
			new JsonTask<JSONArray>(toURL()) {
				@Override
				protected void onJsonObtained(JSONArray results) {
					onLoadedListener.onPhotosLoaded(PhotoInfo.getAllFromJson(results));
					isLoading = false;
				}
			}.execute();

		} else {

			// Load search result photos
			new JsonTask<JSONObject>(toURL()) {
				@Override
				protected void onJsonObtained(JSONObject result) {
					final JSONArray results;
					try {
						results = result.getJSONArray("results");
						onLoadedListener.onPhotosLoaded(PhotoInfo.getAllFromJson(results));

					} catch (JSONException e) {
						Log.e(TAG, "JSON error with search results", e);
						// Fire callback with empty list:
						onLoadedListener.onPhotosLoaded(new LinkedList<PhotoInfo>());
					}
					isLoading = false;
				}
			}.execute();
		}
		isLoading = true;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public boolean isNew() {
		return isNew;
	}
}

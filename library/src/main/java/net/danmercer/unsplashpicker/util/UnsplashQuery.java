package net.danmercer.unsplashpicker.util;

import android.util.Log;

import net.danmercer.unsplashpicker.data.PhotoInfo;
import net.danmercer.unsplashpicker.util.async.JsonTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
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

	private final String appID;
	private int pageNumber = 1;
	private boolean isLoading;

	public UnsplashQuery(String appID) {
		this.appID = appID;
	}

	public UnsplashQuery nextPage() {
		pageNumber++;
		return this;
	}

	public String toURL() {
		return new QueryStringBuilder("https://api.unsplash.com/photos")
				.add("client_id", appID)
				.add("page", pageNumber)
				.build();
	}

	public void load(final OnLoadedListener onLoadedListener) {
		new JsonTask<JSONArray>(toURL()) {
			@Override
			protected void onJsonObtained(JSONArray result) {
				onLoadedListener.onPhotosLoaded(getPhotosFromResult(result));
				isLoading = false;
			}
		}.execute();
		isLoading = true;
	}

	public boolean isLoading() {
		return isLoading;
	}

	public List<PhotoInfo> getPhotosFromResult(Object result) {
		final JSONArray photosData = (JSONArray) result;
		final int count = photosData.length();
		final List<PhotoInfo> photos = new LinkedList<>();
		for (int i = 0; i < count; i++) {
			try {
				photos.add(PhotoInfo.getFromJson(photosData.getJSONObject(i)));
			} catch (JSONException e) {
				Log.e(TAG, "Error parsing photo data.", e);
			}
		}
		return photos;
	}
}

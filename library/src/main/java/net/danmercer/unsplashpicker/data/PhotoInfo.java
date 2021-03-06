package net.danmercer.unsplashpicker.data;

import android.util.Log;

import net.danmercer.unsplashpicker.util.UnsplashApiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Dan Mercer
 */

public class PhotoInfo {
	private static final String TAG = "PhotoInfo";
	public final String authorName;
	public final String authorURL;
	public final String thumbPhotoURL;
	public final String webPhotoURL;
	public final String id;

	//=================================================
	//        Static factory methods

	private static PhotoInfo getFromJson(JSONObject json) throws JSONException {
		final JSONObject authorData = json.getJSONObject("user");
		String authorName = authorData.getString("name");
		String authorURL = authorData.getJSONObject("links").getString("html");
		String thumbPhotoUrl = json.getJSONObject("urls").getString("small");
		String webPhotoUrl = json.getJSONObject("links").getString("html");
		String id = json.getString("id");
		return new PhotoInfo(authorName, authorURL, thumbPhotoUrl, webPhotoUrl, id);
	}

	public static List<PhotoInfo> getAllFromJson(JSONArray results) {
		final int count = results.length();
		final List<PhotoInfo> photos = new LinkedList<>();
		for (int i = 0; i < count; i++) {
			try {
				photos.add(getFromJson(results.getJSONObject(i)));
			} catch (JSONException e) {
				Log.e(TAG, "Error parsing photo data.", e);
			}
		}
		return photos;
	}

	//=================================================
	//        Instance

	private PhotoInfo(String authorName, String authorURL, String thumbPhotoURL, String webPhotoUrl, String id) {
		this.authorName = authorName;
		this.authorURL = authorURL;
		this.thumbPhotoURL = thumbPhotoURL;
		this.webPhotoURL = webPhotoUrl;
		this.id = id;
	}

	public String getDetailsUrl(String appID) {
		return UnsplashApiUtils.getPhotoDetailsUrl(id, appID);
	}
}

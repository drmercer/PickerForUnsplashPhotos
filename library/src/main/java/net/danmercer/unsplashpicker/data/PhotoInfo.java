package net.danmercer.unsplashpicker.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Dan Mercer
 */

public class PhotoInfo {
	public final String authorName;
	public final String authorURL;
	public final String thumbPhotoURL;
	public final String id;

	//=================================================
	//        Static factory method

	public static PhotoInfo getFromJson(JSONObject json) throws JSONException {
		final JSONObject authorData = json.getJSONObject("user");
		String authorName = authorData.getString("name");
		String authorURL = authorData.getJSONObject("links").getString("html");
		String thumbPhotoUrl = json.getJSONObject("urls").getString("small");
		String id = json.getString("id");
		return new PhotoInfo(authorName, authorURL, thumbPhotoUrl, id);
	}

	//=================================================
	//        Instance

	public PhotoInfo(String authorName, String authorURL, String thumbPhotoURL, String id) {
		this.authorName = authorName;
		this.authorURL = authorURL;
		this.thumbPhotoURL = thumbPhotoURL;
		this.id = id;
	}
}

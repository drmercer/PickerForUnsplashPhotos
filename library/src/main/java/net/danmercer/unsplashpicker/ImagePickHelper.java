package net.danmercer.unsplashpicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import net.danmercer.unsplashpicker.util.QueryStringBuilder;
import net.danmercer.unsplashpicker.util.UnsplashApiUtils;
import net.danmercer.unsplashpicker.util.async.BitmapTask;
import net.danmercer.unsplashpicker.util.async.JsonTask;

import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

/**
 * @author Dan Mercer
 */

public class ImagePickHelper {
	public interface OnBitmapDownloadedListener {
		void onBitmapDownloaded(Bitmap bmp);
		void onBitmapDownloadError();
	}

	private static final String TAG = "ImagePickHelper";
	private static final int REQUEST_CODE = 10;

	//=================================================
	//        Fields

	// The Activity which is using this helper
	private final Activity activity;

	// The Unsplash API app ID.
	private final String appID;

	// The ID of the photo which was picked in the ImagePickActivity
	private String photoID;

	// The desired width and height of the downloaded image
	private int width = 0, height = 0;

	// The callback for when the Bitmap is loaded
	private OnBitmapDownloadedListener bitmapLoadListener;

	private boolean downloading = false;

	//=================================================
	//        Methods

	public ImagePickHelper(Activity activity) {
		this.activity = activity;
		this.appID = UnsplashApiUtils.getApiKey(activity);
	}

	public void launchPickerActivity() {
		activity.startActivityForResult(new Intent(activity, ImagePickActivity.class), REQUEST_CODE);
	}

	public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_CODE || resultCode != RESULT_OK) {
			return false;
		}
		// Get photo ID
		this.photoID = data.getStringExtra(ImagePickActivity.EXTRA_PHOTO_ID);

		return true;
	}

	public void setDimens(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void download(OnBitmapDownloadedListener callback) {
		if (downloading) throw new IllegalStateException("A bitmap is already being downloaded!");
		downloading = true;

		this.bitmapLoadListener = callback;

		// Construct proper URL
		String url = UnsplashApiUtils.getPhotoDetailsUrl(photoID, appID);
		if (width != 0 && height != 0) {
			url = new QueryStringBuilder(url)
					.add("w", width)
					.add("h", height)
					.build();
		}

		// Get photo details JSON
		new JsonTask<JSONObject>(url) {
			@Override
			protected void onJsonObtained(JSONObject result) {
				String url;
				try {
					JSONObject urls = result.getJSONObject("urls");
					if (urls.has("custom")) {
						url = urls.getString("custom");
					} else {
						url = urls.getString("regular");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Bad JSON for photo", e);
					endDownload(null);
					return;
				}

				downloadBitmapFromURL(url);
			}
		}.execute();
	}

	private void downloadBitmapFromURL(String url) {
		new BitmapTask(url) {
			@Override
			protected void onBitmapLoaded(Bitmap bitmap) {
				endDownload(bitmap);
			}

			@Override
			protected void onBitmapLoadFailed() {
				endDownload(null);
			}
		}.execute();
	}

	private void endDownload(Bitmap bmp) {
		downloading = false;
		if (bmp != null) {
			bitmapLoadListener.onBitmapDownloaded(bmp);
		} else {
			bitmapLoadListener.onBitmapDownloadError();
		}
		bitmapLoadListener = null;
	}
}

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
		/**
		 * Called when a Bitmap has been successfully downloaded.
		 * @param bmp The Bitmap.
		 */
		void onBitmapDownloaded(Bitmap bmp);
		/**
		 * Called when an error occurred while downloading the desired bitmap.
		 */
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

	/**
	 * Constructs a new <code>ImagePickHelper</code>.
	 * @param activity
	 *          The current {@link Activity}.
	 */
	public ImagePickHelper(Activity activity) {
		this.activity = activity;
		this.appID = UnsplashApiUtils.getApiKey(activity);
	}

	/**
	 * Call this to launch the photo picker Activity.
	 */
	public void launchPickerActivity() {
		activity.startActivityForResult(new Intent(activity, ImagePickActivity.class), REQUEST_CODE);
	}

	/**
	 * Call this in {@link Activity#onActivityResult(int, int, Intent)} of the calling activity.
	 * If the result is not one that this ImagePickHelper cares about, it will simply return
	 * <code>false</code>. Otherwise, it gets the necessary data from the result and returns
	 * <code>true</code>.
	 *
	 * @param requestCode
	 *      The <code>requestCode</code> given to <code>onActivityResult(requestCode, resultCode,
	 *      data)</code>.
	 * @param resultCode
	 *      The <code>resultCode</code> given to <code>onActivityResult(requestCode, resultCode,
	 *      data)</code>.
	 * @param data
	 *      The <code>data</code> given to <code>onActivityResult(requestCode, resultCode,
	 *      data)</code>.
	 * @return
	 *      <code>true</code> if the result was handled, <code>false</code> if it wasn't.
	 */
	public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_CODE || resultCode != RESULT_OK) {
			return false;
		}
		// Get photo ID
		this.photoID = data.getStringExtra(ImagePickActivity.EXTRA_PHOTO_ID);

		return true;
	}

	/**
	 * Sets the dimensions to request from Unsplash. The downloaded bitmap will have these
	 * dimensions. If this is never called, then the regular-sized image is downloaded.
	 *
	 * Must be called before {@link #download(OnBitmapDownloadedListener)}.
	 *
	 * @param width
	 *          The desired width in pixels.
	 * @param height
	 *          The desired height in pixels.
	 */
	public void setDimens(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Call this after {@link #handleActivityResult(int, int, Intent)} to asynchronously download
	 * the chosen photo as a Bitmap.
	 * @param callback
	 *          A listener which is fired when the bitmap is successfully downloaded (or when an
	 *          error occurs).
	 */
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

	// Called after a photo's info (including the proper download URL) has been obtained.
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

	// Called whenever the download ends, either by succeeding or by failing (in which case bmp is
	// null).
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

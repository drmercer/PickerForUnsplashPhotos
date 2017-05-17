package net.danmercer.unsplashpicker.util.async;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import net.danmercer.unsplashpicker.data.PhotoInfo;
import net.danmercer.unsplashpicker.util.QueryStringBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Dan Mercer
 */

public class PhotoDownloader {

	public interface OnDownloadCompleteListener {
		void onDownloadComplete(File file);
	}

	private static final String TAG = "PhotoDownloader";
	private final PhotoInfo src;
	private final File dst;
	private final String appID;
	private int width = 0, height = 0;
	private OnDownloadCompleteListener listener;

	public PhotoDownloader(PhotoInfo photo, File dest, String appID) {
		src = photo;
		dst = dest;
		this.appID = appID;
	}

	public PhotoDownloader setDimens(int width, int height) {
		this.width = width;
		this.height = height;
		return this;
	}

	public void download(OnDownloadCompleteListener listener) {
		this.listener = listener;

		// Construct proper URL
		String url = src.getDetailsUrl(appID);
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
				String url = null;
				try {
					JSONObject urls = result.getJSONObject("urls");
					if (urls.has("custom")) {
						url = urls.getString("custom");
					} else {
						url = urls.getString("regular");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Bad JSON for photo", e);
					// TODO: call an error callback or something
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
				saveBitmapToFile(bitmap);
			}

			@Override
			protected void onBitmapLoadFailed() {
				// TODO: call error callback or something
			}

		}.execute();
	}

	private void saveBitmapToFile(final Bitmap bitmap) {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... unused) {
				BufferedOutputStream out = null;

				try {
					out = new BufferedOutputStream(new FileOutputStream(dst));
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
				} catch (IOException e) {
					// TODO: handle error
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void unused) {
				if (listener != null) {
					listener.onDownloadComplete(dst);
				}
			}

		}.execute();
	}
}

package net.danmercer.unsplashpicker.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Dan Mercer
 */

public class UnsplashApiUtils {
	private static final String TAG = "UnsplashApiUtils";

	/**
	 * Returns the API key specified in the app's manifest.
	 *
	 * @param context A Context in the app.
	 * @return the API key specified in the app's manifest, or null if no valid key is specified.
	 */
	public static String getApiKey(Context context) {
		try {
			final ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			final Bundle metadata = info.metaData;

			return metadata.getString("net.danmercer.unsplashpicker.unsplash_app_id");

		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "Error getting API key:", e);
			return null;
		}
	}
}

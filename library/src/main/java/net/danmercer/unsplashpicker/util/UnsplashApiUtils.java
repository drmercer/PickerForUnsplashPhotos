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
		final String ERROR_MSG = "An app ID for the Unsplash API must be specified in the Manifest!";
		try {
			final ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			final Bundle metadata = info.metaData;

			final String appID = metadata.getString("net.danmercer.unsplashpicker.unsplash_app_id");
			if (appID == null) {
				throw new IllegalStateException(ERROR_MSG);
			}
			return appID;

		} catch (PackageManager.NameNotFoundException e) {
			throw new IllegalStateException(ERROR_MSG, e);
		}
	}

	public static String addUtmParams(String url, Context context) {
		final String ERROR_MSG = "An utm_source name ('unsplash_utm_source') must be specified in the Manifest!";

		String utmName;
		try {
			final ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			final Bundle metadata = info.metaData;

			utmName = metadata.getString("net.danmercer.unsplashpicker.unsplash_utm_source");
			if (utmName == null) {
				throw new IllegalStateException(ERROR_MSG);
			}

		} catch (PackageManager.NameNotFoundException e) {
			throw new IllegalStateException(ERROR_MSG, e);
		}

		return new QueryStringBuilder(url)
				.add("utm_source", utmName)
				.add("utm_medium", "referral")
				.add("utm_campaign", "api-credit")
				.build();
	}

	public static String getUnsplashAttribUrl(Context context) {
		return addUtmParams("https://unsplash.com/", context);
	}

	public static String getPhotoDetailsUrl(String id, String appID) {
		return new QueryStringBuilder("https://api.unsplash.com/photos/" + id)
				.add("client_id", appID)
				.build();
	}
}

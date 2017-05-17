package net.danmercer.unsplashpicker.util.async;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Dan Mercer
 */
public abstract class JsonTask<T> extends HttpTask<Object> {

	private static final String TAG = "JsonTask";

	protected JsonTask(String url) {
		super(url);
	}

	@Override
	Object readStream(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		StringBuilder sb = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			sb.append(line).append("\n");
			line = reader.readLine();
		}

		try {
			return new JSONTokener(sb.toString()).nextValue();

		} catch (JSONException e) {
			Log.e(TAG, "Bad JSON:", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(Object result) {
		if (result != null) {
			try {
				//noinspection unchecked (This unchecked cast is OK, any errors will be caught.)
				onJsonObtained((T) result);
			} catch (ClassCastException e) {
				Log.e(TAG, "Result of JSON parse is not right type.", e);
			}
		}
	}

	protected abstract void onJsonObtained(T result);
}

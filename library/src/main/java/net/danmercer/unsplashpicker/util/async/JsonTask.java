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
public abstract class JsonTask<T> extends HttpTask<T> {

	private static final String TAG = "JsonTask";

	protected JsonTask(String url) {
		super(url);
	}

	@Override
	T readStream(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		StringBuilder sb = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			sb.append(line).append("\n");
			line = reader.readLine();
		}

		try {
			final Object o = new JSONTokener(sb.toString()).nextValue();
			//noinspection unchecked (The unchecked cast is OK.)
			return (T) o;

		} catch (ClassCastException e) {
			Log.e(TAG, "Result of JSON parse is not right type.", e);
			return null;
		} catch (JSONException e) {
			Log.e(TAG, "Bad JSON:", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(T result) {
		if (result != null) {
			onJsonObtained(result);
		}
	}

	protected abstract void onJsonObtained(T result);
}

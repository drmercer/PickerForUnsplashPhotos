package net.danmercer.unsplashpicker.util.async;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A generic AsyncTask for loading stuff from an http URL.
 *
 * @author Dan Mercer
 */
abstract class HttpTask<T> extends AsyncTask<Void, Void, T> {

	private static final String TAG = "HttpTask";

	private final URL url;
	private String method = "GET";

	public HttpTask(String url) {
		try {
			this.url = new URL(url);
		} catch (MalformedURLException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	public HttpTask setMethod(String method) {
		this.method = method;
		return this; // for chaining
	}

	// Helps to avoid linter warnings for "Unchecked call to 'execute(Params...)' ..."
	public void execute() {
		Log.d(TAG, "Loading URL: " + url.toString());
		this.execute(new Void[]{});
	}

	@Override
	protected final T doInBackground(Void... voids) {
		InputStream reader = null;
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();

			// TODO: set timeouts?

			connection.setRequestMethod(method);

			connection.connect();

			reader = connection.getInputStream();

			return readStream(reader);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}

	abstract T readStream(InputStream stream) throws IOException;
}

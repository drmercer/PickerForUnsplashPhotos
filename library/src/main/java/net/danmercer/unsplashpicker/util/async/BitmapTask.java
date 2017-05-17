package net.danmercer.unsplashpicker.util.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Dan Mercer
 */
public abstract class BitmapTask extends HttpTask<Bitmap> {
	public BitmapTask(String imageUrl) {
		super(imageUrl);
	}

	@Override
	Bitmap readStream(InputStream stream) throws IOException {
		return BitmapFactory.decodeStream(stream);
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (bitmap != null) {
			onBitmapLoaded(bitmap);
		} else {
			onBitmapLoadFailed();
		}
	}

	protected abstract void onBitmapLoaded(Bitmap bitmap);
	protected abstract void onBitmapLoadFailed();
}

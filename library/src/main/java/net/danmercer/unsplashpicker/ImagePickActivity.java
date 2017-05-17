package net.danmercer.unsplashpicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.danmercer.unsplashpicker.util.UnsplashApiUtils;

/**
 * The main picker activity.
 *
 * @author Dan Mercer
 */
public class ImagePickActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String apiKey = UnsplashApiUtils.getApiKey(this);
		if (apiKey != null) {
			Toast.makeText(this, "API key: " + apiKey, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Error: No Unsplash API key :(", Toast.LENGTH_SHORT).show();
			finish();
		}
	}
}

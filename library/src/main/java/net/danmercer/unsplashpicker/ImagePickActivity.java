package net.danmercer.unsplashpicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * The main picker activity.
 *
 * @author Dan Mercer
 */
public class ImagePickActivity extends AppCompatActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Toast.makeText(this, "Image pick started!", Toast.LENGTH_SHORT).show();
	}
}

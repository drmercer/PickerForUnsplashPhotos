package net.danmercer.unsplashpicker.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.danmercer.unsplashpicker.ImagePickHelper;

public class MainActivity extends AppCompatActivity {

	private Button btn;
	private ImagePickHelper pickHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pickHelper = new ImagePickHelper(this);
		pickHelper.setPickerActivityClass(SubclassActivity.class);

		btn = new Button(this);
		btn.setText("Click me");
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				pickHelper.launchPickerActivity();
			}
		});

		setContentView(btn);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (pickHelper.handleActivityResult(requestCode, resultCode, data)) {

			// Tell helper to download image as bitmap
			pickHelper.setDimens(btn.getWidth(), btn.getHeight());
			pickHelper.download(new ImagePickHelper.OnBitmapDownloadedListener() {
				@Override
				public void onBitmapDownloaded(Bitmap bmp) {
					btn.setBackgroundDrawable(new BitmapDrawable(getResources(), bmp));
				}

				@Override
				public void onBitmapDownloadError() {
					Toast.makeText(MainActivity.this, "Error downloading image. :(", Toast.LENGTH_SHORT).show();
				}
			});

		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}
}

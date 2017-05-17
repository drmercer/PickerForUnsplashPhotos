package net.danmercer.unsplashpicker.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import net.danmercer.unsplashpicker.ImagePickActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_IMAGE = 10;
	private Button btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		btn = new Button(this);
		btn.setText("Click me");
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivityForResult(new Intent(MainActivity.this, ImagePickActivity.class), REQUEST_IMAGE);
			}
		});

		setContentView(btn);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE) {

			if (resultCode == RESULT_OK) {
				try {
					Uri uri = data.getData();
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
					btn.setBackgroundDrawable(new BitmapDrawable(bitmap));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}

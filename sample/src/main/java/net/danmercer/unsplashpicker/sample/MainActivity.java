package net.danmercer.unsplashpicker.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.danmercer.unsplashpicker.ImagePickActivity;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Button btn = new Button(this);
		btn.setText("Click me");
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(MainActivity.this, ImagePickActivity.class));
			}
		});

		setContentView(btn);
	}
}

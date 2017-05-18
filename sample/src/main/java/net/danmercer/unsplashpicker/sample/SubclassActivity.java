package net.danmercer.unsplashpicker.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;

import net.danmercer.unsplashpicker.ImagePickActivity;
import net.danmercer.unsplashpicker.ImagePickHelper;

/**
 * A sample subclass of {@link ImagePickActivity}. NOTE: to use a subclass, you must call
 * {@link ImagePickHelper#setPickerActivityClass(Class)}.
 *
 * @author Dan Mercer
 */
public class SubclassActivity extends ImagePickActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setTitle("Subclass Activity");
	}
}

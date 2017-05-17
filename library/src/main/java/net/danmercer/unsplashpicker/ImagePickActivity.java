package net.danmercer.unsplashpicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.danmercer.unsplashpicker.util.UnsplashApiUtils;
import net.danmercer.unsplashpicker.util.UnsplashQuery;
import net.danmercer.unsplashpicker.util.async.JsonTask;

import org.json.JSONArray;

/**
 * The main picker activity.
 *
 * @author Dan Mercer
 */
public class ImagePickActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String appID = UnsplashApiUtils.getApiKey(this);
		if (appID == null) {
			Toast.makeText(this, "Error: no app ID!", Toast.LENGTH_LONG).show();
			finish();
		}

		// Testing querying stuff:

		final UnsplashQuery query = new UnsplashQuery(appID);

		final JsonTask task = new JsonTask<JSONArray>(query.toURL()) {
			@Override
			protected void onJsonObtained(JSONArray arr) {
				final int length = arr.length();
				// length should be 10
				Toast.makeText(ImagePickActivity.this, length == 10 ? "Success!" : "Error. Wrong array length :(", Toast.LENGTH_SHORT).show();
			}
		};
		task.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.uip_picker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id == R.id.uip_action_about) {
			Toast.makeText(this, "TODO: show About info", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

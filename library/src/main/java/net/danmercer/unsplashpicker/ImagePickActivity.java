package net.danmercer.unsplashpicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import net.danmercer.unsplashpicker.data.PhotoInfo;
import net.danmercer.unsplashpicker.util.UnsplashApiUtils;
import net.danmercer.unsplashpicker.util.UnsplashQuery;
import net.danmercer.unsplashpicker.view.ImageQueryAdapter;
import net.danmercer.unsplashpicker.view.ImageRecyclerView;

/**
 * The main picker activity.
 *
 * @author Dan Mercer
 */
public class ImagePickActivity extends AppCompatActivity {

	private ImageQueryAdapter adapter;
	private UnsplashQuery query;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String appID = UnsplashApiUtils.getApiKey(this);
		if (appID == null) {
			Toast.makeText(this, "Error: no app ID!", Toast.LENGTH_LONG).show();
			finish();
		}

		ImageRecyclerView view = new ImageRecyclerView(this);

		final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
		view.setLayoutManager(layoutManager);

		adapter = new ImageQueryAdapter(this);
		view.setAdapter(adapter);

		query = new UnsplashQuery(appID);
		adapter.updateQuery(query);

		// Listen for scroll to the end, and load more photos
		view.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				final int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
				final int itemCount = adapter.getItemCount();
				if (lastVisibleItemPosition+1 >= itemCount && !query.isLoading()) {
					// Scroll has reached the end, load another page
					query.nextPage();
					adapter.updateQuery(query);
				}
			}
		});

		// Set up item click listener
		adapter.setOnPhotoChosenListener(new ImageQueryAdapter.OnPhotoChosenListener() {
			@Override
			public void onPhotoChosen(PhotoInfo choice) {
				Toast.makeText(ImagePickActivity.this, "Photo by " + choice.authorName + " chosen", Toast.LENGTH_SHORT).show();
			}
		});

		setContentView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.uip_picker, menu);

		// Set up search:
		final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.uip_action_search));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String text) {
				query.setSearch(text);
				adapter.updateQuery(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		searchView.setOnCloseListener(new SearchView.OnCloseListener() {
			@Override
			public boolean onClose() {
				query.cancelSearch();
				adapter.updateQuery(query);
				return false;
			}
		});

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

package net.danmercer.unsplashpicker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

	private ImageRecyclerView view;
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

		view = new ImageRecyclerView(this);

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
					Toast.makeText(ImagePickActivity.this, "Loading more photos", Toast.LENGTH_SHORT).show();
				}
			}
		});

		setContentView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.uip_picker, menu);

		// For debugging:
		menu.add("Search 'bagel'").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				query.setSearch("bagel");
				adapter.updateQuery(query);

				return true;
			}
		});
		menu.add("Search 'potato'").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				query.setSearch("potato");
				adapter.updateQuery(query);

				return true;
			}
		});
		menu.add("Clear search").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				query.cancelSearch();
				adapter.updateQuery(query);

				return true;
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

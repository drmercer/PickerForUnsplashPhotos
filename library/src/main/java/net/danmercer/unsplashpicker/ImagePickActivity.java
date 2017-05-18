package net.danmercer.unsplashpicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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
	static final String EXTRA_PHOTO_ID = "net.danmercer.unsplashpicker.PHOTO_ID";

	private ImageQueryAdapter adapter;
	private UnsplashQuery query;
	private SearchView searchView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final String appID = UnsplashApiUtils.getApiKey(this);

		ImageRecyclerView view = new ImageRecyclerView(this);

		adapter = new ImageQueryAdapter(this);
		view.setAdapter(adapter);

		final GridLayoutManager layoutManager = getLayoutManager();
		view.setLayoutManager(layoutManager);

		query = initQuery(appID);
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
				final Intent result = new Intent();
				result.putExtra(EXTRA_PHOTO_ID, choice.id);
				setResult(RESULT_OK, result);
				finish();
			}
		});

		setContentView(view);
	}

	/**
	 * Called by {@link #onCreate(Bundle)} to get a layout manager for the recycler view. The
	 * default implementation returns a GridLayoutManager with 2 spans per row. Override this method
	 * to tweak the LayoutManager.
	 *
	 * @return The GridLayoutManager to use.
	 */
	@NonNull
	protected GridLayoutManager getLayoutManager() {
		return new GridLayoutManager(this, 2);
	}

	/**
	 * Called to initialze the UnsplashQuery object that is used to show photos. The default
	 * implementation just returns <code>new UnsplashQuery(appID)</code>, which just loads curated
	 * photos 20 at a time. Override this to modify the initial state of the query.
	 *
	 * @param appID
	 *          The app ID. Pass this to the {@link UnsplashQuery#UnsplashQuery(String)}
	 *          constructor.
	 * @return
	 *          The new UnsplashQuery object.
	 */
	@NonNull
	protected UnsplashQuery initQuery(String appID) {
		return new UnsplashQuery(appID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.uip_picker, menu);

		// Set up search:
		searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.uip_action_search));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String text) {
				onSearchSubmitted(text);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				onSearchTextChanged(newText);
				return false;
			}
		});
		searchView.setOnCloseListener(new SearchView.OnCloseListener() {
			@Override
			public boolean onClose() {
				onSearchClosed();
				return false;
			}
		});

		return true;
	}

	/**
	 * Called whenever the search text changes (e.g. as the user types). The default implementation
	 * does nothing - the user must submit the search for anything to happen - but if you want to
	 * show results instantly then just call {@link #onSearchSubmitted(String)} from this method.
	 *
	 * @param text
	 *          The current text in the search field.
	 */
	protected void onSearchTextChanged(String text) { /* Does nothing by default */ }

	/**
	 * Called whenever a search query is submitted by the user. The default implementation performs
	 * the search by updating the current query.
	 * @param text
	 *          The search string.
	 */
	protected void onSearchSubmitted(String text) {
		query.setSearch(text);
		adapter.updateQuery(query);
	}

	/**
	 * Called whenever the search UI is closed (whether by the user or programmatically). The
	 * default implementation cancels the search by calling {@link UnsplashQuery#cancelSearch()}
	 * on the query.
	 */
	protected void onSearchClosed() {
		query.cancelSearch();
		adapter.updateQuery(query);
	}

	/**
	 * Hides the search UI.
	 *
	 * @return true if the search was active, false if not (and nothing was done).
	 */
	protected boolean cancelSearch() {
		if (searchView != null && !searchView.isIconified()) {
			// Empty the query:
			searchView.setQuery("", false);
			// Close the search view:
			searchView.setIconified(true);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int id = item.getItemId();
		if (id == R.id.uip_action_about) {

			// Show About dialog:
			AlertDialog.Builder db = new AlertDialog.Builder(this);
			db.setTitle(R.string.uip_about_title);

			final TextView msg = (TextView) getLayoutInflater().inflate(R.layout.uip_about, null);
			final String unsplashAttribUrl = UnsplashApiUtils.getUnsplashAttribUrl(this);
			//noinspection deprecation (Non-deprecated fromHtml() requires Android N.)
			msg.setText(Html.fromHtml(getString(R.string.uip_about, unsplashAttribUrl)));
			msg.setMovementMethod(LinkMovementMethod.getInstance());
			db.setView(msg);

			db.setPositiveButton(android.R.string.ok, null);

			db.show();

			return true;

		} else if (id == android.R.id.home) {

			// If Up button is pressed while the Search UI is shown, hide it. (Otherwise, proceed as
			// normal.)
			if (cancelSearch()) {
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * If the Search UI is shown, hides it. Otherwise, does the normal back button behavior.
	 */
	@Override
	public void onBackPressed() {
		// Only actually go back if the Search UI isn't shown. If it is, just hide it.
		if (!cancelSearch()) {
			super.onBackPressed();
		}
	}
}

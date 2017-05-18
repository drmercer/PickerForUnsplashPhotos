package net.danmercer.unsplashpicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import net.danmercer.unsplashpicker.data.PhotoInfo;
import net.danmercer.unsplashpicker.util.UnsplashApiUtils;
import net.danmercer.unsplashpicker.util.UnsplashQuery;
import net.danmercer.unsplashpicker.util.async.PhotoDownloader;
import net.danmercer.unsplashpicker.view.ImageQueryAdapter;
import net.danmercer.unsplashpicker.view.ImageRecyclerView;

import java.io.File;

/**
 * The main picker activity.
 *
 * @author Dan Mercer
 */
public class ImagePickActivity extends AppCompatActivity {
	public static final String EXTRA_IMAGE_WIDTH = "net.danmercer.unsplashpicker.IMAGE_WIDTH";
	public static final String EXTRA_IMAGE_HEIGHT = "net.danmercer.unsplashpicker.IMAGE_HEIGHT";
	public static final String EXTRA_OUTPUT_FILE_PATH = "net.danmercer.unsplashpicker.OUTPUT_FILE";

	private ImageQueryAdapter adapter;
	private UnsplashQuery query;
	private String appID;
	private SearchView searchView;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		appID = UnsplashApiUtils.getApiKey(this);
		if (appID == null) {
			Toast.makeText(this, "Development error: no app ID!", Toast.LENGTH_LONG).show();
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
				downloadAndFinish(choice);
			}
		});

		setContentView(view);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.uip_picker, menu);

		// Set up search:
		searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.uip_action_search));
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

	/**
	 * Hides the search UI.
	 * @return true if the search was active, false if not (and nothing was done).
	 */
	private boolean cancelSearch() {
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

	@Override
	public void onBackPressed() {
		// Only actually go back if the Search UI isn't shown. If it is, just hide it.
		if (!cancelSearch()) {
			super.onBackPressed();
		}
	}

	private void downloadAndFinish(PhotoInfo photo) {
		final Intent intent = getIntent();

		// Get destination for download
		String dest = intent.getStringExtra(EXTRA_OUTPUT_FILE_PATH);
		final File file = (dest != null) ? new File(dest) : new File(getCacheDir(), "unsplash-image.jpg");

		final PhotoDownloader downloader = new PhotoDownloader(photo, file, appID);

		// Get dimension parameters if specified
		downloader.setDimens(intent.getIntExtra(EXTRA_IMAGE_WIDTH, 0),
				intent.getIntExtra(EXTRA_IMAGE_HEIGHT, 0));

		downloader.download(new PhotoDownloader.OnDownloadCompleteListener() {
			@Override
			public void onDownloadComplete(File file) {
				final Intent result = new Intent();
				result.setData(Uri.fromFile(file));
				setResult(RESULT_OK, result);
				finish();
			}
		});
	}
}

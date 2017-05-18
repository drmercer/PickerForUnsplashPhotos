package net.danmercer.unsplashpicker.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.danmercer.unsplashpicker.R;
import net.danmercer.unsplashpicker.data.PhotoInfo;
import net.danmercer.unsplashpicker.util.UnsplashQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dan Mercer
 */

public class ImageQueryAdapter extends RecyclerView.Adapter<ViewHolder> {

	public static final int TYPE_IMAGE = 0;
	private static final int TYPE_LOADER = 1;
	private static final int TYPE_NO_RESULTS_TEXT = 2;

	public interface OnPhotoChosenListener {
		void onPhotoChosen(PhotoInfo choice);
	}

	private final LayoutInflater inflater;
	private final View.OnClickListener clickListener;
	private List<PhotoInfo> photos = new ArrayList<>();
	private ImageQueryAdapter.OnPhotoChosenListener photoChosenListener = null;
	private boolean showLoaderAtBottom = false;
	private boolean noMorePictures = false;

	public ImageQueryAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		clickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final Object tag = view.getTag();
				if (photoChosenListener != null && tag != null) {
					photoChosenListener.onPhotoChosen((PhotoInfo) tag);
				}
			}
		};
	}

	@Override
	public int getItemViewType(int position) {
		if (position < photos.size()) {
			return TYPE_IMAGE;
		} else if (isLoaderShown()){
			return TYPE_LOADER;
		} else {
			return TYPE_NO_RESULTS_TEXT;
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view;

		switch (viewType) {

		case TYPE_IMAGE:
			view = inflater.inflate(R.layout.uip_list_item, parent, false);
			return new ImageViewHolder(view, clickListener);

		case TYPE_LOADER:
			view = inflater.inflate(R.layout.uip_list_loader_item, parent, false);
			return new ViewHolder(view) {};

		case TYPE_NO_RESULTS_TEXT:
			view = inflater.inflate(R.layout.uip_list_no_results_item, parent, false);
			return new ViewHolder(view) {};

		default:
			throw new UnsupportedOperationException("Unrecognized viewType: " + viewType);
		}
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (position < photos.size()) {
			ImageViewHolder ivh = (ImageViewHolder) holder;
			ivh.loadPhoto(photos.get(position));
		}
	}

	@Override
	public void onViewRecycled(ViewHolder holder) {
		if (holder instanceof ImageViewHolder) {
			((ImageViewHolder) holder).recycle();
		}
	}

	/**
	 * @param showLoader <code>true</code> if the ProgressBar loader at the bottom should be shown.
	 */
	private void setLoaderShown(boolean showLoader) {
		if (showLoader != showLoaderAtBottom) {
			this.showLoaderAtBottom = showLoader;
			// Insert or remove an item at the bottom as appropriate
			if (showLoader) {
				notifyItemInserted(photos.size());
			} else {
				notifyItemRemoved(photos.size());
			}
		}
	}

	/**
	 * @return <code>true</code> if the ProgressBar loader at the bottom is currently shown.
	 */
	public boolean isLoaderShown() {
		return showLoaderAtBottom;
	}

	@Override
	public int getItemCount() {
		int itemCount = photos.size();
		// Count the loader as an item
		if (showLoaderAtBottom) itemCount += 1;
		// If there aren't any photos and it's not loading, count the "no photos" text.
		if (itemCount == 0 && noMorePictures) itemCount = 1;

		return itemCount;
	}

	public void updateQuery(UnsplashQuery query) {
		if (query.isNew()) {
			// Restart photo list, since it's a new search
			setLoaderShown(false);
			final int size = getItemCount();
			noMorePictures = false;
			photos.clear();
			notifyItemRangeRemoved(0, size);

		} else if (noMorePictures) {
			// Do nothing, since the last query didn't get anything
			return;
		}
		setLoaderShown(true);
		query.load(new UnsplashQuery.OnLoadedListener() {
			@Override
			public void onPhotosLoaded(List<PhotoInfo> newPhotos) {
				setLoaderShown(false);
				if (newPhotos.size() == 0) {
					noMorePictures = true;
				} else {
					int insertPosition = photos.size();
					photos.addAll(newPhotos);
					notifyItemRangeInserted(insertPosition, newPhotos.size());
				}
			}
		});
	}

	public void setOnPhotoChosenListener(OnPhotoChosenListener listener) {
		this.photoChosenListener = listener;
	}
}

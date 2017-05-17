package net.danmercer.unsplashpicker.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

public class ImageQueryAdapter extends RecyclerView.Adapter<ImageViewHolder> {


	private final LayoutInflater inflater;

	private List<PhotoInfo> photos = new ArrayList<>();

	public ImageQueryAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	@Override
	public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = inflater.inflate(R.layout.uip_list_item, parent, false);
		return new ImageViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ImageViewHolder holder, int position) {
		holder.loadPhoto(photos.get(position));
	}

	@Override
	public int getItemCount() {
		return photos.size();
	}

	public void updateQuery(UnsplashQuery query) {
		if (query.isNew()) {
			final int size = photos.size();
			photos.clear();
			notifyItemRangeRemoved(0, size);
		}
		query.load(new UnsplashQuery.OnLoadedListener() {
			@Override
			public void onPhotosLoaded(List<PhotoInfo> newPhotos) {
				int insertPosition = photos.size();
				photos.addAll(newPhotos);
				notifyItemRangeInserted(insertPosition, newPhotos.size());
			}
		});
	}
}

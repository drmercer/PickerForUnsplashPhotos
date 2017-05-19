package net.danmercer.unsplashpicker.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.danmercer.unsplashpicker.R;
import net.danmercer.unsplashpicker.data.PhotoInfo;
import net.danmercer.unsplashpicker.util.UnsplashApiUtils;
import net.danmercer.unsplashpicker.util.async.BitmapTask;

/**
 * @author Dan Mercer
 */

class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

	private final ImageView imageView;
	private final TextView labelView;
	private PhotoInfo currentPhoto;
	private Bitmap currentBitmap = null;
	private BitmapTask currentTask = null;

	ImageViewHolder(View itemView, View.OnClickListener listener) {
		super(itemView);
		this.imageView = (ImageView) itemView.findViewById(R.id.uip_item_image);
		imageView.setOnClickListener(listener);
		this.labelView = (TextView) itemView.findViewById(R.id.uip_item_label);

		imageView.setOnCreateContextMenuListener(this);
	}

	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View view,
	                                ContextMenu.ContextMenuInfo contextMenuInfo) {
		// Header:
		contextMenu.setHeaderTitle(R.string.uip_context_title);

		// "View in Browser" item:
		final MenuItem viewItem = contextMenu.add(R.string.uip_context_action_view_in_browser);
		final Context context = view.getContext();
		final String url = UnsplashApiUtils.addUtmParams(currentPhoto.webPhotoURL, context);
		viewItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				if (currentPhoto == null) {
					return true;
				}
				final Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				context.startActivity(i);
				return true;
			}
		});
	}

	void loadPhoto(final PhotoInfo photoInfo) {
		recycle();

		this.currentPhoto = photoInfo;

		labelView.setText(photoInfo.authorName);
		labelView.setClickable(true);
		labelView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Context context = view.getContext();
				final String authorURL = UnsplashApiUtils.addUtmParams(photoInfo.authorURL, context);
				final Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(authorURL));
				context.startActivity(i);
			}
		});

		imageView.setTag(photoInfo);

		currentTask = new BitmapTask(photoInfo.thumbPhotoURL) {
			@Override
			protected void onBitmapLoaded(Bitmap bitmap) {
				// If that photo is still the right one to show, show it.
				if (currentPhoto == photoInfo) {
					currentBitmap = bitmap;
					imageView.setImageBitmap(bitmap);
				} else {
					// Otherwise, recycle it.
					bitmap.recycle();
				}
				currentTask = null;
			}

			@Override
			protected void onBitmapLoadFailed() {
				// TODO: get a better error image
				imageView.setImageResource(android.R.drawable.stat_notify_error);
				currentTask = null;
			}
		};
		currentTask.execute();
	}

	void recycle() {
		currentPhoto = null;
		imageView.setTag(null);
		if (currentTask != null) {
			currentTask.cancel(false);
		}
		if (currentBitmap != null) {
			imageView.setImageDrawable(null);
			currentBitmap.recycle();
			currentBitmap = null;
		}
	}
}

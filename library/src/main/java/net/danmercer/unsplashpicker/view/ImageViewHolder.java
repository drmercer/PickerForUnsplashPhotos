package net.danmercer.unsplashpicker.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
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

class ImageViewHolder extends RecyclerView.ViewHolder {

	private final ImageView imageView;
	private final TextView labelView;
	private PhotoInfo currentPhoto;
	private Bitmap currentBitmap = null;

	public ImageViewHolder(View itemView) {
		super(itemView);
		this.imageView = (ImageView) itemView.findViewById(R.id.uip_item_image);
		this.labelView = (TextView) itemView.findViewById(R.id.uip_item_label);
	}

	public void loadPhoto(final PhotoInfo photoInfo) {
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

		new BitmapTask(photoInfo.thumbPhotoURL) {
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
			}

			@Override
			protected void onBitmapLoadFailed() {
				// TODO: get a better error image
				imageView.setImageResource(android.R.drawable.stat_notify_error);
			}
		}.execute();
	}

	public void recycle() {
		currentPhoto = null;
		if (currentBitmap != null) {
			imageView.setImageDrawable(null);
			currentBitmap.recycle();
			currentBitmap = null;
		}
	}
}

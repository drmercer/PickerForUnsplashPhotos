package net.danmercer.unsplashpicker.view;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import net.danmercer.unsplashpicker.R;
import net.danmercer.unsplashpicker.data.PhotoInfo;
import net.danmercer.unsplashpicker.util.async.BitmapTask;

/**
 * @author Dan Mercer
 */

class ImageViewHolder extends RecyclerView.ViewHolder {

	private final ImageView imageView;
	private PhotoInfo currentPhoto;

	public ImageViewHolder(View itemView) {
		super(itemView);
		this.imageView = (ImageView) itemView.findViewById(R.id.uip_item_image);
	}

	public void loadPhoto(final PhotoInfo photoInfo) {
		this.currentPhoto = photoInfo;

		new BitmapTask(photoInfo.thumbPhotoURL) {
			@Override
			protected void onBitmapLoaded(Bitmap bitmap) {
				// If that photo is still the right one to show, show it.
				if (currentPhoto == photoInfo) {
					imageView.setImageBitmap(bitmap);
				} else {
					// Otherwise, recycle it.
					bitmap.recycle();
				}
			}

			@Override
			protected void onBitmapLoadFailed() {
				imageView.setImageResource(android.R.drawable.stat_notify_error);
			}
		}.execute();
	}
}

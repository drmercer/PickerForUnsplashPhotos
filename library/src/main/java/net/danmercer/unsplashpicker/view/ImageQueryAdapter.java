package net.danmercer.unsplashpicker.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.danmercer.unsplashpicker.R;

/**
 * @author Dan Mercer
 */

public class ImageQueryAdapter extends RecyclerView.Adapter<ImageViewHolder> {


	private final LayoutInflater inflater;

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
		// TODO: load image
	}

	@Override
	public int getItemCount() {
		return 20;
	}
}

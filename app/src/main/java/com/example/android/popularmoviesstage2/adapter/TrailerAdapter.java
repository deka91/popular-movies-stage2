package com.example.android.popularmoviesstage2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.data.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Deniz Kalem on 11.08.17.
 */

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	Context context;
	ArrayList<Trailer> trailers = new ArrayList<>();

	private OnItemClicked onClick;

	public interface OnItemClicked {
		void onItemClick(int position);
	}

	public TrailerAdapter(Context context, ArrayList<Trailer> trailers) {
		this.context = context;
		this.trailers = trailers;
	}

	public void add(Trailer object) {
		trailers.add(object);
		notifyDataSetChanged();
	}

	public void clear() {
		trailers.clear();
		notifyDataSetChanged();
	}

	public Trailer getItem(int position) {
		return trailers.get(position);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder viewHolder;
		View view;
		view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
		viewHolder = new MyItemHolder(view);

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		String id = trailers.get(position).getKey();
		String thumbnailURL = "http://img.youtube.com/vi/".concat(id).concat("/hqdefault.jpg");
		Picasso.with(context).load(thumbnailURL).placeholder(R.drawable.thumbnail).into(((MyItemHolder) holder).ivTrailerThumbnail);

		((MyItemHolder) holder).ivTrailerThumbnail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClick.onItemClick(position);
			}
		});
	}

	@Override
	public int getItemCount() {
		return trailers.size();
	}

	public static class MyItemHolder extends RecyclerView.ViewHolder {
		ImageView ivTrailerThumbnail;

		public MyItemHolder(View itemView) {
			super(itemView);

			ivTrailerThumbnail = (ImageView) itemView.findViewById(R.id.iv_trailer_thumbnail);
		}

	}

	public void setOnClick(OnItemClicked onClick) {
		this.onClick = onClick;
	}

}
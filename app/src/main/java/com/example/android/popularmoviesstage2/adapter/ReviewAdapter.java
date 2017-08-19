package com.example.android.popularmoviesstage2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.data.Review;

import java.util.ArrayList;

/**
 * Created by Deniz Kalem on 11.08.17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	Context context;
	ArrayList<Review> reviews = new ArrayList<>();

	public ReviewAdapter(Context context, ArrayList<Review> reviews) {
		this.context = context;
		this.reviews = reviews;
	}

	public void add(Review object) {
		reviews.add(object);
		notifyDataSetChanged();
	}

	public void clear() {
		reviews.clear();
		notifyDataSetChanged();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder viewHolder;
		View view;
		view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
		viewHolder = new MyItemHolder(view);

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
		((MyItemHolder) holder).tvAuthor.setText(reviews.get(position).getAuthor());
		((MyItemHolder) holder).tvReview.setText(reviews.get(position).getContent());
	}

	@Override
	public int getItemCount() {
		return reviews.size();
	}

	public static class MyItemHolder extends RecyclerView.ViewHolder {
		public TextView tvAuthor;
		public TextView tvReview;


		public MyItemHolder(View itemView) {
			super(itemView);
			tvAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
			tvReview = (TextView) itemView.findViewById(R.id.tv_review_content);
		}

	}

}

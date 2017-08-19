package com.example.android.popularmoviesstage2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Deniz Kalem on 16.05.17.
 */

public class MovieAdapter extends BaseAdapter {

	public final static String BASE_URL   = "https://image.tmdb.org/t/p/";
	public final static String IMAGE_SIZE = "w500";
	private Context     context;
	private List<Movie> movies;

	public MovieAdapter(Context context, List<Movie> movies) {
		this.context = context;
		this.movies = movies;
	}

	@Override
	public int getCount() {
		return movies.size();
	}

	@Override
	public Movie getItem(int position) {
		return movies.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Movie movie = getItem(position);
		ImageView imageView;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageView = (ImageView) inflater.inflate(R.layout.movie_item, parent, false);
		} else {
			imageView = (ImageView) convertView;
		}

		String url = new StringBuilder().append(BASE_URL).append(IMAGE_SIZE).append(movie.getPosterPath().trim()).toString();

		Picasso.with(context).load(url).into(imageView);
		return imageView;
	}

	public void clear() {
		if (movies.size() > 0) {
			movies.clear();
		}
	}
}

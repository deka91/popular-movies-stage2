package com.example.android.popularmoviesstage2.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.adapter.MovieAdapter;
import com.example.android.popularmoviesstage2.adapter.ReviewAdapter;
import com.example.android.popularmoviesstage2.adapter.TrailerAdapter;
import com.example.android.popularmoviesstage2.data.Movie;
import com.example.android.popularmoviesstage2.data.Review;
import com.example.android.popularmoviesstage2.data.Trailer;
import com.example.android.popularmoviesstage2.database.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmoviesstage2.R.id.trailersRecyclerView;

/**
 * Created by Deniz Kalem on 18.05.17.
 */

public class MovieDetailActivity extends AppCompatActivity
		implements TrailerAdapter.OnItemClicked {

	private TextView tvTitle, tvRelease, tvRating, tvDescription;
	private ImageView            ivPoster;
	private ReviewAdapter        reviewAdapter;
	private TrailerAdapter       trailerAdapter;
	private Movie                movie;
	private RecyclerView         recyclerViewTrailer;
	private RecyclerView         recyclerViewReview;
	private FloatingActionButton ibFavorite;
	private Context              context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_detail);

		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvRelease = (TextView) findViewById(R.id.tv_release);
		tvRating = (TextView) findViewById(R.id.tv_rating);
		tvDescription = (TextView) findViewById(R.id.tv_description);
		ivPoster = (ImageView) findViewById(R.id.iv_poster);

		movie = getIntent().getParcelableExtra("movie");

		if (movie != null) {
			tvTitle.setText(movie.getTitle());
			tvRelease.setText(getResources().getString(R.string.release) + " "
									  + movie
					.getReleaseDate());
			tvRating.setText(getResources().getString(R.string.rating) + " " +
									 movie
					.getVoteAverage() + "/10");
			tvDescription.setText(getResources().getString(R.string.overview)
										  + movie
					.getOverview());
			loadPoster(movie.getPosterPath());
		}

		ibFavorite = (FloatingActionButton) findViewById(R.id.ib_favorite);

		LinearLayoutManager trailerLayoutManager = new LinearLayoutManager
				(this,
																		   LinearLayoutManager.HORIZONTAL,
																		   false);
		LinearLayoutManager reviewLayoutManager = new LinearLayoutManager
				(this);

		recyclerViewTrailer = (RecyclerView) findViewById
				(trailersRecyclerView);
		recyclerViewReview = (RecyclerView) findViewById(R.id
																 .reviewsRecyclerView);
		recyclerViewTrailer.setLayoutManager(trailerLayoutManager);
		recyclerViewReview.setLayoutManager(reviewLayoutManager);

		trailerAdapter = new TrailerAdapter(this, new ArrayList<Trailer>());
		recyclerViewTrailer.setAdapter(trailerAdapter);

		reviewAdapter = new ReviewAdapter(this, new ArrayList<Review>());
		recyclerViewReview.setAdapter(reviewAdapter);

		trailerAdapter.setOnClick(this);

		context = this;
	}

	@Override
	public void onItemClick(int position) {
		Trailer trailer = trailerAdapter.getItem(position);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer
				.getKey()));
		startActivity(intent);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (movie != null) {
			new FetchReviewsTask().execute(movie.getId());
			new FetchTrailerTask().execute(movie.getId());

			if (isInFavorites(movie.getId()) == 1) {
				ibFavorite.setImageResource(R.drawable.ic_star_black_24dp);
			} else {
				ibFavorite.setImageResource(R.drawable
													.ic_star_border_black_24dp);
			}
		}
	}

	private void loadPoster(String path) {
		String urlBuilder = new StringBuilder().append(MovieAdapter.BASE_URL)
											   .append(MovieAdapter.IMAGE_SIZE)
											   .append(path)
											   .toString();
		Picasso.with(getApplicationContext()).load(urlBuilder).into(ivPoster);
	}


	public class FetchTrailerTask
			extends AsyncTask<String, Void, List<Trailer>> {

		private List<Trailer> getTrailerDataFromJson(String jsonStr) throws
				JSONException {
			JSONObject trailerJson = new JSONObject(jsonStr);
			JSONArray trailerArray = trailerJson.getJSONArray("results");

			List<Trailer> results = new ArrayList<>();

			for (int i = 0; i < trailerArray.length(); i++) {
				JSONObject trailer = trailerArray.getJSONObject(i);
				if (trailer.getString("site").contentEquals("YouTube")) {
					Trailer trailerModel = new Trailer(trailer);
					results.add(trailerModel);
				}
			}

			return results;
		}

		@Override
		protected List<Trailer> doInBackground(String... params) {

			if (params.length == 0) {
				return null;
			}

			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;

			String jsonStr = null;

			try {
				final String BASE_URL = "http://api.themoviedb.org/3/movie/" +
						params[0] + "/videos";

				Uri builtUri = Uri.parse(BASE_URL)
								  .buildUpon()
								  .appendQueryParameter("api_key",
														context.getString(R
																				  .string.API_KEY))
								  .build();

				URL url = new URL(builtUri.toString());

				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				InputStream inputStream = urlConnection.getInputStream();
				StringBuffer buffer = new StringBuffer();
				if (inputStream == null) {
					return null;
				}
				reader = new BufferedReader(new InputStreamReader
													(inputStream));

				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line + "\n");
				}

				if (buffer.length() == 0) {
					return null;
				}
				jsonStr = buffer.toString();
			} catch (IOException e) {
				return null;
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException e) {
					}
				}
			}

			try {
				return getTrailerDataFromJson(jsonStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(List<Trailer> trailers) {
			if (trailers != null) {
				if (trailers.size() > 0) {
					recyclerViewTrailer.setVisibility(View.VISIBLE);
					if (trailerAdapter != null) {
						trailerAdapter.clear();
						for (Trailer trailer : trailers) {
							trailerAdapter.add(trailer);
						}
					}
				}
			}

		}
	}


	public class FetchReviewsTask
			extends AsyncTask<String, Void, List<Review>> {

		private List<Review> getReviewsDataFromJson(String jsonStr) throws
				JSONException {
			JSONObject reviewJson = new JSONObject(jsonStr);
			JSONArray reviewArray = reviewJson.getJSONArray("results");

			List<Review> results = new ArrayList<>();

			for (int i = 0; i < reviewArray.length(); i++) {
				JSONObject review = reviewArray.getJSONObject(i);
				results.add(new Review(review));
			}

			return results;
		}

		@Override
		protected List<Review> doInBackground(String... params) {

			if (params.length == 0) {
				return null;
			}

			HttpURLConnection urlConnection = null;
			BufferedReader reader = null;

			String jsonStr = null;

			try {
				final String BASE_URL = "http://api.themoviedb.org/3/movie/" +
						params[0] + "/reviews";

				Uri builtUri = Uri.parse(BASE_URL)
								  .buildUpon()
								  .appendQueryParameter("api_key",
														context.getString(R
																				  .string.API_KEY))
								  .build();
				URL url = new URL(builtUri.toString());

				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				InputStream inputStream = urlConnection.getInputStream();
				StringBuffer buffer = new StringBuffer();
				if (inputStream == null) {
					return null;
				}
				reader = new BufferedReader(new InputStreamReader
													(inputStream));

				String line;
				while ((line = reader.readLine()) != null) {
					buffer.append(line + "\n");
				}

				if (buffer.length() == 0) {
					return null;
				}
				jsonStr = buffer.toString();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				if (reader != null) {
					try {
						reader.close();
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				return getReviewsDataFromJson(jsonStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;

		}

		@Override
		protected void onPostExecute(List<Review> reviews) {
			if (reviews != null) {
				if (reviews.size() > 0) {
					recyclerViewReview.setVisibility(View.VISIBLE);
					if (reviewAdapter != null) {
						reviewAdapter.clear();
						for (Review review : reviews) {
							reviewAdapter.add(review);
						}
					}
				}
			}
		}
	}


	public void addToFavorite(View view) {
		if (movie != null) {
			new AsyncTask<Void, Void, Integer>() {

				@Override
				protected Integer doInBackground(Void... params) {
					return isInFavorites(movie.getId());
				}

				@Override
				protected void onPostExecute(Integer isInFavorites) {
					if (isInFavorites == 1) {
						new AsyncTask<Void, Void, Integer>() {
							@Override
							protected Integer doInBackground(Void... params) {
								return getContentResolver().delete
										(MovieContract.MovieEntry.CONTENT_URI,
																   MovieContract.MovieEntry.COLUMN_ID + " = ?",
																   new
												 String[]{
																		   movie.getId()});
							}

							@Override
							protected void onPostExecute(Integer rowsDeleted) {
								ibFavorite.setImageResource(R.drawable
																	.ic_star_border_black_24dp);
							}
						}.execute();
					} else {
						// adding to favorites
						new AsyncTask<Void, Void, Uri>() {
							@Override
							protected Uri doInBackground(Void... params) {
								ContentValues values = new ContentValues();

								values.put(MovieContract.MovieEntry.COLUMN_ID,
										   movie.getId());
								values.put(MovieContract.MovieEntry
												   .COLUMN_TITLE,
										   movie.getTitle());
								values.put(MovieContract.MovieEntry
												   .COLUMN_POSTER_PATH,
										   movie.getPosterPath());
								values.put(MovieContract.MovieEntry
												   .COLUMN_OVERVIEW,
										   movie.getOverview());
								values.put(MovieContract.MovieEntry
												   .COLUMN_VOTE_AVERAGE,
										   movie.getVoteAverage());
								values.put(MovieContract.MovieEntry
												   .COLUMN_RELEASE_DATE,
										   movie.getReleaseDate());

								return getContentResolver().insert
										(MovieContract.MovieEntry.CONTENT_URI,
																   values);
							}

							@Override
							protected void onPostExecute(Uri returnUri) {
								ibFavorite.setImageResource(R.drawable
																	.ic_star_black_24dp);
							}
						}.execute();
					}
				}
			}.execute();
		}

	}

	public int isInFavorites(String id) {
		Cursor cursor = getContentResolver().query(MovieContract.MovieEntry
														   .CONTENT_URI,
												   null,
												   MovieContract.MovieEntry
														   .COLUMN_ID + " = ?",
												   new String[]{id},
												   null);
		cursor.close();
		return cursor.getCount();
	}
}

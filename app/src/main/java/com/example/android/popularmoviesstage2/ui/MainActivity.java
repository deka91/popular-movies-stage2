package com.example.android.popularmoviesstage2.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.R;
import com.example.android.popularmoviesstage2.adapter.MovieAdapter;
import com.example.android.popularmoviesstage2.callback.Callback;
import com.example.android.popularmoviesstage2.data.Movie;
import com.example.android.popularmoviesstage2.database.MovieContract;

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

import static com.example.android.popularmoviesstage2.database.MovieContract
		.MOVIE_COLUMNS;

/**
 * Created by Deniz Kalem on 16.05.17.
 */

public class MainActivity extends AppCompatActivity {

	public static final String BASE_URL  = "https://api.themoviedb" + "" + "" + ".org/3/movie/";
	private final       String POPULAR   = "popular";
	private final       String TOP_RATED = "top_rated";
	private final       String FAVORITE  = "favorite";
	private MovieAdapter     movieAdapter;
	private ArrayList<Movie> movieList;
	private TextView         tvErrorMessage;
	private ProgressBar      pbLoadingIndicator;
	private String sorting = POPULAR;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tvErrorMessage = (TextView) findViewById(R.id.tv_error_message_display);
		pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey("sorting")) {
				sorting = savedInstanceState.getString("sorting");
			}

			if (savedInstanceState.containsKey("movies")) {
				movieList = savedInstanceState.getParcelableArrayList("movies");
			}
		} else {
			movieList = new ArrayList<>();
		}

		movieAdapter = new MovieAdapter(this, movieList);
		GridView gvMovies = (GridView) findViewById(R.id.gv_movies);
		gvMovies.setAdapter(movieAdapter);
		gvMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				Movie movie = movieAdapter.getItem(position);
				Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
				intent.putExtra("movie", movie);
				startActivity(intent);
			}
		});


		if (savedInstanceState != null) {
			if (!savedInstanceState.containsKey("movies")) {
				showMovies(sorting);
			}
		} else {
			showMovies(sorting);
		}

		context = this;
	}

	private void showMovies(String filter) {

		tvErrorMessage.setVisibility(View.INVISIBLE);

		if (filter.equals(FAVORITE)) {
			FetchFavoriteMoviesTask favoriteMoviesTask = new FetchFavoriteMoviesTask(new Callback() {
				@Override
				public void updateAdapter(List<Movie> movies) {
					if (movies != null) {
						movieAdapter.clear();
						movieList.addAll(movies);
						movieAdapter.notifyDataSetChanged();
					}

				}
			});

			favoriteMoviesTask.execute(filter);
		} else {
			FetchMovieTask moviesTask = new FetchMovieTask(new Callback() {
				@Override
				public void updateAdapter(List<Movie> movies) {
					if (movies != null) {
						movieAdapter.clear();
						movieList.addAll(movies);
						movieAdapter.notifyDataSetChanged();
					}

				}
			});

			moviesTask.execute(filter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_top_rated) {
			showMovies(TOP_RATED);
			sorting = TOP_RATED;
			return true;
		}

		if (id == R.id.action_most_popular) {
			showMovies(POPULAR);
			sorting = POPULAR;
			return true;
		}

		if (id == R.id.action_favorites) {
			showMovies(FAVORITE);
			sorting = FAVORITE;
			return true;
		}


		if (id == R.id.action_refresh) {
			showMovies(sorting);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	public class FetchMovieTask extends AsyncTask<String, Void, List<Movie>> {

		private final Callback callback;

		FetchMovieTask(Callback movieTaskCallback) {
			this.callback = movieTaskCallback;
		}

		private List<Movie> getMoviesFromJson(String jsonStringMovie) throws JSONException {
			if (jsonStringMovie == null || "".equals(jsonStringMovie)) {
				return null;
			}

			JSONObject jsonObjectMovie = new JSONObject(jsonStringMovie);
			JSONArray jsonArrayMovies = jsonObjectMovie.getJSONArray("results");

			List<Movie> results = new ArrayList<>();

			for (int i = 0; i < jsonArrayMovies.length(); i++) {
				JSONObject movie = jsonArrayMovies.getJSONObject(i);
				Movie movieModel = new Movie(movie);
				results.add(movieModel);
			}

			return results;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pbLoadingIndicator.setVisibility(View.VISIBLE);
		}

		@Override
		protected List<Movie> doInBackground(String... params) {

			if (params.length == 0) {
				return null;
			}

			HttpURLConnection urlConnection = null;
			String movieJsonString = null;
			BufferedReader reader = null;

			Uri uri = Uri.parse(BASE_URL).buildUpon().appendEncodedPath(params[0]).appendQueryParameter("api_key", context.getString(R.string.API_KEY))
						 .build();

			try {
				URL url = new URL(uri.toString());
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.connect();

				InputStream inputStream = urlConnection.getInputStream();
				StringBuilder builder = new StringBuilder();
				if (inputStream == null) {
					return null;
				}
				reader = new BufferedReader(new InputStreamReader(inputStream));

				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line + "\n");
				}

				if (builder.length() == 0) {
					return null;
				}

				movieJsonString = builder.toString();

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}

				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				return getMoviesFromJson(movieJsonString);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}


		@Override
		protected void onPostExecute(List<Movie> movies) {
			pbLoadingIndicator.setVisibility(View.INVISIBLE);
			if (movies != null) {
				callback.updateAdapter(movies);
			} else {
				tvErrorMessage.setVisibility(View.VISIBLE);
			}
		}

	}

	public class FetchFavoriteMoviesTask extends AsyncTask<String, Void, List<Movie>> {

		private Callback callback;
		private Context  context;


		FetchFavoriteMoviesTask(Callback favoriteMovieTask) {
			this.callback = favoriteMovieTask;
			context = getApplicationContext();
		}

		private List<Movie> getFavoriteMoviesDataFromCursor(Cursor cursor) {
			List<Movie> results = new ArrayList<>();
			if (cursor != null && cursor.moveToFirst()) {
				do {
					Movie movie = new Movie(cursor);
					results.add(movie);
				} while (cursor.moveToNext());
				cursor.close();
			}
			return results;
		}

		@Override
		protected List<Movie> doInBackground(String... params) {

			if (params.length == 0) {
				return null;
			}

			Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, MOVIE_COLUMNS, null, null, null);
			return getFavoriteMoviesDataFromCursor(cursor);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pbLoadingIndicator.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(List<Movie> movies) {
			pbLoadingIndicator.setVisibility(View.INVISIBLE);
			if (movies != null) {
				callback.updateAdapter(movies);
			} else {
				tvErrorMessage.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("sorting", sorting);
		if (movieList != null) {
			outState.putParcelableArrayList("movies", movieList);
		}
		super.onSaveInstanceState(outState);

	}
}
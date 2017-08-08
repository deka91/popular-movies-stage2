package com.example.android.popularmoviesstage1;

import android.content.Intent;
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
import java.util.Collections;

/**
 * Created by Deniz Kalem on 16.05.17.
 */

public class MainActivity extends AppCompatActivity {

    // TODO Insert your API-KEY here
    public static final String API_KEY = "";
    public static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private final String POPULAR = "popular";
    private final String TOP_RATED = "top_rated";
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieList;
    private TextView tvErrorMessage;
    private ProgressBar pbLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvErrorMessage = (TextView) findViewById(R.id.tv_error_message_display);
        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        movieList = new ArrayList<>();
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

        showMovies(POPULAR);
    }

    private void showMovies(String filter) {

        tvErrorMessage.setVisibility(View.INVISIBLE);

        FetchMovieTask moviesTask = new FetchMovieTask(new Callback() {
            @Override
            public void updateAdapter(Movie[] movies) {
                if (movies != null) {
                    movieAdapter.clear();
                    Collections.addAll(movieList, movies);
                    movieAdapter.notifyDataSetChanged();
                }

            }
        });

        moviesTask.execute(filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        showMovies(POPULAR);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_top_rated) {
            showMovies(TOP_RATED);
            return true;
        }

        if (id == R.id.action_most_popular || id == R.id.action_refresh) {
            showMovies(POPULAR);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final Callback callback;

        FetchMovieTask(Callback movieTaskCallback) {
            this.callback = movieTaskCallback;
        }

        private Movie[] getMoviesFromJson(String jsonStringMovie) throws JSONException {
            if (jsonStringMovie == null || "".equals(jsonStringMovie)) {
                return null;
            }

            JSONObject jsonObjectMovie = new JSONObject(jsonStringMovie);
            JSONArray jsonArrayMovies = jsonObjectMovie.getJSONArray("results");

            Movie[] movies = new Movie[jsonArrayMovies.length()];

            for (int i = 0; i < jsonArrayMovies.length(); i++) {
                JSONObject object = jsonArrayMovies.getJSONObject(i);
                movies[i] = new Movie(object.getString("original_title"),
                        object.getString("poster_path"),
                        object.getString("overview"),
                        object.getString("vote_average"),
                        object.getString("release_date"));
            }
            return movies;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            String movieJsonString = null;
            BufferedReader reader = null;

            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(params[0])
                    .appendQueryParameter("api_key", API_KEY)
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
        protected void onPostExecute(Movie[] movies) {
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movies != null) {
                callback.updateAdapter(movies);
            } else {
                tvErrorMessage.setVisibility(View.VISIBLE);
            }

        }

    }

}

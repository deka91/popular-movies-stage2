package com.example.android.popularmoviesstage1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.android.popularmoviesstage1.MovieAdapter.BASE_URL;
import static com.example.android.popularmoviesstage1.MovieAdapter.IMAGE_SIZE;

/**
 * Created by Deniz Kalem on 18.05.17.
 */

public class MovieDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvRelease, tvRating, tvDescription;
    private ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRelease = (TextView) findViewById(R.id.tv_release);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        tvDescription = (TextView) findViewById(R.id.tv_description);
        ivPoster = (ImageView) findViewById(R.id.iv_poster);

        Movie movies = getIntent().getParcelableExtra("movie");

        if (movies != null) {
            tvTitle.setText(movies.getTitle());
            tvRelease.setText(getResources().getString(R.string.release) + " " + movies.getReleaseDate());
            tvRating.setText(getResources().getString(R.string.rating) + " " + movies.getVoteAverage() + "/10");
            tvDescription.setText(getResources().getString(R.string.overview) + movies.getOverview());
            loadPoster(movies.getPosterPath());
        }
    }

    private void loadPoster(String path) {
        String urlBuilder = new StringBuilder()
                .append(BASE_URL)
                .append(IMAGE_SIZE)
                .append(path).toString();

        Picasso.with(getApplicationContext())
                .load(urlBuilder)
                .into(ivPoster);
    }

}

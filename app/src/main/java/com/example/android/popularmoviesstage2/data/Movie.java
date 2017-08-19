package com.example.android.popularmoviesstage2.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popularmoviesstage2.database.MovieContract;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deniz Kalem on 16.05.17.
 */

public class Movie implements Parcelable {
	private String id;
	private String title;
	private String posterPath;
	private String overview;
	private String voteAverage;
	private String releaseDate;

	public Movie(String id, String title, String posterPath, String overview, String voteAverage, String releaseDate) {
		this.id = id;
		this.title = title;
		this.posterPath = posterPath;
		this.overview = overview;
		this.voteAverage = voteAverage;
		this.releaseDate = releaseDate;
	}

	protected Movie(Parcel in) {
		id = in.readString();
		title = in.readString();
		posterPath = in.readString();
		overview = in.readString();
		voteAverage = in.readString();
		releaseDate = in.readString();
	}

	public Movie(JSONObject movie) throws JSONException {
		this.id = movie.getString("id");
		this.title = movie.getString("original_title");
		this.posterPath = movie.getString("poster_path");
		this.overview = movie.getString("overview");
		this.voteAverage = movie.getString("vote_average");
		this.releaseDate = movie.getString("release_date");
	}


	public Movie(Cursor cursor) {
		this.id = cursor.getString(MovieContract.COL_MOVIE_ID);
		this.title = cursor.getString(MovieContract.COL_TITLE);
		this.posterPath = cursor.getString(MovieContract.COL_IMAGE);
		this.overview = cursor.getString(MovieContract.COL_OVERVIEW);
		this.voteAverage = cursor.getString(MovieContract.COL_RATING);
		this.releaseDate = cursor.getString(MovieContract.COL_DATE);
	}

	public static final Creator<Movie> CREATOR = new Creator<Movie>() {
		@Override
		public Movie createFromParcel(Parcel in) {
			return new Movie(in);
		}

		@Override
		public Movie[] newArray(int size) {
			return new Movie[size];
		}
	};

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getPosterPath() {
		return posterPath;
	}

	public String getOverview() {
		return overview;
	}

	public String getVoteAverage() {
		return voteAverage;
	}

	public String getReleaseDate() {
		return releaseDate;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(title);
		dest.writeString(posterPath);
		dest.writeString(overview);
		dest.writeString(voteAverage);
		dest.writeString(releaseDate);
	}
}

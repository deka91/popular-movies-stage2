package com.example.android.popularmoviesstage2.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Deniz Kalem on 17.08.17.
 */

public final class MovieContract {

	public static final String CONTENT_AUTHORITY = "com.example.android" + "" +
			".popularmoviesstage2";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +
																 CONTENT_AUTHORITY);

	public static final String PATH_MOVIE = "movie";

	public static final class MovieEntry implements BaseColumns {
		// table name
		public static final String TABLE_MOVIE         = "movie";
		// columns
		public static final String COLUMN_ID           = "movie_id";
		public static final String COLUMN_TITLE        = "title";
		public static final String COLUMN_POSTER_PATH  = "poster_path";
		public static final String COLUMN_OVERVIEW     = "overview";
		public static final String COLUMN_VOTE_AVERAGE = "vote_average";
		public static final String COLUMN_RELEASE_DATE = "release_date";

		// create content uri
		public static final Uri    CONTENT_URI      = BASE_CONTENT_URI
				.buildUpon()
																	  .appendPath(
																			  PATH_MOVIE)
																	  .build();
		// create cursor of base type directory for multiple entries‚
		public static final String CONTENT_DIR_TYPE = ContentResolver
				.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +
				PATH_MOVIE;

		// for building URIs on insertion
		public static Uri buildMoviesri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI, id);
		}
	}

	public static final String[] MOVIE_COLUMNS = {
			MovieContract.MovieEntry._ID,
			MovieContract.MovieEntry.COLUMN_ID,
			MovieContract.MovieEntry.COLUMN_TITLE,
			MovieContract.MovieEntry.COLUMN_POSTER_PATH,
			MovieContract.MovieEntry.COLUMN_OVERVIEW,
			MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
			MovieContract.MovieEntry.COLUMN_RELEASE_DATE};

	public static final int COL_MOVIE_ID = 1;
	public static final int COL_TITLE    = 2;
	public static final int COL_IMAGE    = 3;
	public static final int COL_OVERVIEW = 4;
	public static final int COL_RATING   = 5;
	public static final int COL_DATE     = 6;

}

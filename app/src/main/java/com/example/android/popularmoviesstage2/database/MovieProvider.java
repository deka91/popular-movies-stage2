package com.example.android.popularmoviesstage2.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by Deniz Kalem on 17.08.17.
 */

public class MovieProvider extends ContentProvider {
	private static final UriMatcher sUriMatcher = buildUriMatcher();
	private MovieDbHelper mOpenHelper;

	static final int MOVIE = 100;

	static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = MovieContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, MovieContract.MovieEntry.TABLE_MOVIE, MOVIE);

		return matcher;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new MovieDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor retCursor;
		switch (sUriMatcher.match(uri)) {
			case MOVIE: {
				retCursor = mOpenHelper.getReadableDatabase()
									   .query(MovieContract.MovieEntry.TABLE_MOVIE, projection, selection, selectionArgs, null, null, sortOrder);
				break;
			}
			default:
				// By default we assume a bad URI
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return retCursor;
	}

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);

		switch (match) {
			case MOVIE:
				return MovieContract.MovieEntry.CONTENT_DIR_TYPE;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		Uri returnUri;

		switch (sUriMatcher.match(uri)) {
			case MOVIE: {
				long _id = db.insert(MovieContract.MovieEntry.TABLE_MOVIE, null, values);
				if (_id > 0) {
					returnUri = MovieContract.MovieEntry.buildMoviesri(_id);
				} else {
					throw new android.database.SQLException("Failed to insert row into " + uri);
				}
				break;
			}
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int rowsDeleted;
		// this makes delete all rows return the number of rows deleted
		if (null == selection) { selection = "1"; }
		switch (sUriMatcher.match(uri)) {
			case MOVIE:
				rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_MOVIE, selection, selectionArgs);
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		// Because a null deletes all rows
		if (rowsDeleted != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int rowsUpdated;

		switch (sUriMatcher.match(uri)) {
			case MOVIE:
				rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_MOVIE, values, selection, selectionArgs);
				break;
			default:
				throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		if (rowsUpdated != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}

		return rowsUpdated;
	}


}

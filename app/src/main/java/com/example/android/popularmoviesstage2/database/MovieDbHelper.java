package com.example.android.popularmoviesstage2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Deniz Kalem on 17.08.17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	static final String DATABASE_NAME = "movie.db";

	public MovieDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract
				.MovieEntry.TABLE_MOVIE + " (" + MovieContract.MovieEntry._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + MovieContract
				.MovieEntry.COLUMN_ID + " TEXT NOT NULL, " + MovieContract
				.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " + MovieContract
				.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " + MovieContract
				.MovieEntry.COLUMN_OVERVIEW + " TEXT, " + MovieContract
				.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
				MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT);";

		db.execSQL(SQL_CREATE_MOVIE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry
				.TABLE_MOVIE);
		onCreate(db);
	}

}

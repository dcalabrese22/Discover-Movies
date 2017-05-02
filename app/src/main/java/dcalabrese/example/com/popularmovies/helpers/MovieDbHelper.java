/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class that contains SQL to build the favorites database
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movieFavorites.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE =
                "Create Table " + MovieContract.MovieEntry.MOVIE_TABLE + " (" +
                        MovieContract.MovieEntry._ID                  + " Integer Primary Key Autoincrement, " +
                        MovieContract.MovieEntry.MOVIE_COLUMN_NAME    + " Text Not null, " +
                        MovieContract.MovieEntry.MOVIE_COLUMN_DATE    + " Text Not Null, " +
                        MovieContract.MovieEntry.MOVIE_COLUMN_RATING  + " Real Not Null, " +
                        MovieContract.MovieEntry.MOVIE_COLUMN_SYNOPSIS + " Text Not Null, " +
                        MovieContract.MovieEntry.MOVIE_COLUMN_POSTER + " Blob Not Null" +
                        ")";

        final String SQL_CREATE_REVIEW_TABLE =
                "Create Table " + MovieContract.ReviewEntry.REVIEW_TABLE + " (" +
                        MovieContract.ReviewEntry._ID                  + " Integer Primary Key Autoincrement, " +
                        MovieContract.ReviewEntry.REVIEW_COLUMN_CONTENT+ " Text Not null, " +
                        MovieContract.ReviewEntry.REVIEW_COLUMN_FOREIGN_KEY + " Integer Not null, " +
                        "Foreign Key (" + MovieContract.ReviewEntry.REVIEW_COLUMN_FOREIGN_KEY + ") References " +
                        MovieContract.MovieEntry.MOVIE_TABLE + "(" + MovieContract.MovieEntry._ID + ")" +
                        ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.helpers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Custom content provider for delivering data from the database
 */
public class MyContentProvider extends ContentProvider {

    private MovieDbHelper mOpenHelper;
    public static final int CODE_MOVIE = 100;

    public static final int CODE_REVIEW = 200;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Builds a urimatcher to map incoming requests
     *
     * @return UriMatcher object
     */
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_REVIEW, CODE_REVIEW);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /**
     * Handles inserting data in the corresponding table
     *
     * @param uri Uri of the table to insert data to
     * @param contentValues The data to insert
     * @return
     *
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long id;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                id = db.insert(MovieContract.MovieEntry.MOVIE_TABLE, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.MOVIE_CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case CODE_REVIEW:
                id = db.insert(MovieContract.ReviewEntry.REVIEW_TABLE, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.ReviewEntry.REVIEW_CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    /**
     * Handles querying data from the corresponding table
     *
     * @param uri The Uri of the table to query
     * @param projection The columns in the table to return in the query
     * @param selection The SQL where clause (e.g. Column=?)
     * @param selectionArgs String array that defines the where clauses' arguments (e.g. what the ?
     *                      represent from the selection)
     * @param sortOrder Sort order of the query
     * @return Cursor object containing the query results
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                retCursor = db.query(MovieContract.MovieEntry.MOVIE_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case CODE_REVIEW:
                retCursor = db.query(MovieContract.ReviewEntry.REVIEW_TABLE, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /**
     * Handles deleting data from the corresponding table
     *
     * @param uri The Uri of the table to delete from
     * @param selection The SQL where clause (e.g. COLUMN = ?)
     * @param selectionArgs String array that defines the where clauses' arguments (e.g. what the ?
     *                      represent from the selection)
     * @return The number of deletions that took place.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int tasksDeleted;

        switch (sUriMatcher.match(uri)) {
            case CODE_MOVIE:
                tasksDeleted = db.delete(MovieContract.MovieEntry.MOVIE_TABLE, selection, selectionArgs);
                break;
            case CODE_REVIEW:
                tasksDeleted = db.delete(MovieContract.ReviewEntry.REVIEW_TABLE, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        if (tasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

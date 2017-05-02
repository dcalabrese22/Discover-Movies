/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.helpers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Class for declaring column names for the favorites database
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "dcalabrese.example.com.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //path to movie table for content provider
    public static final String PATH_MOVIE = "movie";
    //path to review table for content provider
    public static final String PATH_REVIEW = "review";


    /**
     * Class for movie table columns
     */
    public static class MovieEntry implements BaseColumns {

        //uri of movie table
        public static final Uri MOVIE_CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String MOVIE_TABLE = "movie";

        public static final String MOVIE_COLUMN_NAME = "name";
        public static final String MOVIE_COLUMN_DATE = "release_date";
        public static final String MOVIE_COLUMN_RATING = "rating";
        public static final String MOVIE_COLUMN_SYNOPSIS = "synopsis";
        public static final String MOVIE_COLUMN_POSTER = "poster";


    }

    /**
     * Class for review table columns
     */
    public static class ReviewEntry implements BaseColumns {
        public static final Uri REVIEW_CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_REVIEW)
                .build();


        public static final String REVIEW_TABLE = "review";

        public static final String REVIEW_COLUMN_CONTENT = "content";
        public static final String REVIEW_COLUMN_FOREIGN_KEY = "movie_id";
    }
}

/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.objects;

/**
 * Movie trailers that are retrieved from the movie database
 */
public class MovieTrailer extends Object {

    private String mYoutubeKey;
    private String mName;

    public MovieTrailer(String key, String name) {
        mYoutubeKey = key;
        mName = name;
    }

    public String getYoutubeKey() {
        return mYoutubeKey;
    }

    public String getName() {
        return mName;
    }
}

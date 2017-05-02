/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies;


import dcalabrese.example.com.popularmovies.objects.MovieFromDatabase;

/**
 * Interface for handling clicks on a movie object in the mainactivity recyclerview
 */
public interface AdapterOnClickHandler {

    void onMovieClick(MovieFromDatabase movie);
}

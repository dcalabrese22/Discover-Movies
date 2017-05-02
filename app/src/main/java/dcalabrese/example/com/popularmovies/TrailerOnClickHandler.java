/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies;

import dcalabrese.example.com.popularmovies.objects.MovieTrailer;

/**
 * Interface for handling clicks of a movie trailer
 */
public interface TrailerOnClickHandler {

    void OnTrailerClick(MovieTrailer trailer);
}

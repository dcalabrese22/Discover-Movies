/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies;

import android.database.Cursor;
import android.view.View;

/**
 * Interface for handling clicks of a favorite movie item
 */
public interface FavoriteOnClickHandler {

    void onFavoriteClick(String title, int position, View sharedView);
}

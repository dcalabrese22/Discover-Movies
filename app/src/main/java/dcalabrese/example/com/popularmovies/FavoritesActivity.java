/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import dcalabrese.example.com.popularmovies.adapters.FavoritesAdapter;
import dcalabrese.example.com.popularmovies.helpers.MovieContract;

/**
 * Activity that displays the movies the user has marked as a favorite
 */
public class FavoritesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
            FavoriteOnClickHandler {

    FavoritesAdapter mFavoritesAdapter;
    RecyclerView mRecyclerView;

    public static final String MOVIE_TITLE = "title";

    public static final int LOADER_ID = 449;


    /**
     * Creates the view filled with recyclerview items
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        getSupportActionBar().setTitle(R.string.favorites_actionbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_favorites);

        LinearLayoutManager ll = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(ll);
        mFavoritesAdapter = new FavoritesAdapter(this, this);
        mRecyclerView.setAdapter(mFavoritesAdapter);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public static final String FAVORITES_TRANSITION_NAME = "fav_transition_name";
    /**
     * Handles opening the detail activity coming from this activity
     *
     * @param v The view clicked
     */
    @Override
    public void onFavoriteClick(String title, int position, View v) {
        TextView titleTv = (TextView) v.findViewById(R.id.tv_favorite_title);
        Context context = getApplicationContext();
        Class destination = DetailActivity.class;
        Intent intent = new Intent(context, destination);
        //this extra is used for determining the intent that started the detail activity
        intent.putExtra(MainActivity.SOURCE, "favorites");
        intent.putExtra(MOVIE_TITLE, title);
        intent.putExtra(MainActivity.INTENT_EXTRA_TRANSITION_NAME, ViewCompat.getTransitionName(v));
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                v,
                ViewCompat.getTransitionName(v));
        startActivity(intent, optionsCompat.toBundle());
    }

    /**
     * Requeries the favorites database to check for any movies that might have been removed
     */
    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    /**
     * Queries and loads the results from the Favorites database on a background thread
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor movieData = null;

            @Override
            protected void onStartLoading() {
                if (movieData != null) {
                    deliverResult(movieData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                String[] projection = {MovieContract.MovieEntry.MOVIE_COLUMN_POSTER,
                        MovieContract.MovieEntry.MOVIE_COLUMN_NAME};
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                            projection, null, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return  null;
                }
            }

            public void deliverResult(Cursor data) {
                movieData = data;
                super.deliverResult(data);
            }
        };
    }

    /**
     * Passes the loaded data as a cursor to the adapter
     *
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavoritesAdapter.swapCursor(data);
    }

    /**
     * Removes the data from the adapter
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFavoritesAdapter.swapCursor(null);
    }
}

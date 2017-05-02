/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;

import dcalabrese.example.com.popularmovies.adapters.MovieAdapter;
import dcalabrese.example.com.popularmovies.helpers.JsonResponseParser;
import dcalabrese.example.com.popularmovies.helpers.NetworkUtils;
import dcalabrese.example.com.popularmovies.objects.MovieFromDatabase;

/**
 * Main activity of the app that displays either the most popular or top rated movies
 */
public class MainActivity extends AppCompatActivity implements AdapterOnClickHandler{

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private MovieAdapter mMovieAdapter;

    public int mPageNumber = 1;
    private int mTotalPages;

    private ArrayList<MovieFromDatabase> mMoviesFromDatabase;


    public static final String MOVIE_TITLE = "movie_title";
    public static final String MOVIE_IMAGE_PATH = "movie_image_path";
    public static final String MOVIE_SYNOPSIS = "movie_synopsis";
    public static final String MOVIE_RATING = "movie_rating";
    public static final String MOVIE_RELEASE_DATE = "move_release_date";
    public static final String MOVIE_ID = "movie_id";
    public static final String SOURCE = "source";
    public static final String MAIN = "main";

    private final int GRID_COLUMNS_PORTRAIT = 2;
    private final int GRID_COLUMNS_LANDSCAPE = 4;

    private GridLayoutManager mGridLayoutManager;

    private boolean mIsPopular = true;

    Parcelable mListState;
    private static final String LIST_STATE_KEY = "list_state";


    /**
     * Creates the main view that displays movie poster images in a grid layout
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridLayoutManager = new GridLayoutManager(this, GRID_COLUMNS_PORTRAIT);
            mRecyclerView.setLayoutManager(mGridLayoutManager);

        } else {
            mGridLayoutManager = new GridLayoutManager(this, GRID_COLUMNS_LANDSCAPE);
            mRecyclerView.setLayoutManager(mGridLayoutManager);

        }
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.addOnScrollListener(createInfiniteScrollListener());

        getMoviesFromDatabase(mPageNumber);

    }

    /**
     * Hides the movies view and shows the error message
     */
    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * Create the menu that serves for sorting movies by most popular or top rated
     *
     * @param menu The menu to create
     * @return boolean true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        return true;
    }

    /**
     * Displays either the most popular or top rated movies depending on selection
     *
     * @param item The menu item that was selected
     * @return boolean true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular) {
            mIsPopular = true;
            mMovieAdapter.setMovieData(null);
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerView.clearOnScrollListeners();
            mPageNumber = 1;
            getMoviesFromDatabase(mPageNumber);
            mRecyclerView.addOnScrollListener(createInfiniteScrollListener());
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            return true;
        }

        if (id == R.id.action_rated) {
            mIsPopular = false;
            mMovieAdapter.setMovieData(null);
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerView.clearOnScrollListeners();
            mPageNumber = 1;
            getMoviesFromDatabase(mPageNumber);
            mRecyclerView.addOnScrollListener(createInfiniteScrollListener());
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            return true;
        }

        if (id == R.id.action_favorites) {
            Context context = this;
            Class destinationClass = FavoritesActivity.class;
            Intent favorites = new Intent(context, destinationClass);
            startActivity(favorites);

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Creates and starts the detail activity for showing specifics of movie selected
     *
     * @param movie The movie selected
     */
    @Override
    public void onMovieClick(MovieFromDatabase movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(SOURCE, MAIN);
        intentToStartDetailActivity.putExtra(MOVIE_TITLE, movie.getTitle());
        intentToStartDetailActivity.putExtra(MOVIE_IMAGE_PATH, movie.getImagePath());
        intentToStartDetailActivity.putExtra(MOVIE_SYNOPSIS, movie.getSynopsis());
        intentToStartDetailActivity.putExtra(MOVIE_RATING, movie.getRating());
        intentToStartDetailActivity.putExtra(MOVIE_RELEASE_DATE, movie.getReleaseDate());
        intentToStartDetailActivity.putExtra(MOVIE_ID, movie.getMovieId());
        startActivity(intentToStartDetailActivity);
    }

    /**
     * Method for loading movies on a background thread
     *
     * @param page The results pages to be loaded
     */
    public void getMoviesFromDatabase(int page) {
        String url;
        if (mIsPopular) {
            url = NetworkUtils.buildPopularURL(page);
        } else {
            url = NetworkUtils.buildTopRatedURL(page);
        }
        mLoadingIndicator.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    ArrayList<MovieFromDatabase> movies = new ArrayList<>();

                    @Override
                    public void onResponse(JSONObject response) {
                        movies = JsonResponseParser.parseJsonMovieData(response);
                        if (mMovieAdapter.getItemCount() == 0) {
                            mMovieAdapter.setMovieData(movies);
                        } else {
                            mMovieAdapter.addMoviedata(movies);
                        }
                        mTotalPages = JsonResponseParser.getTotalPages();
                        mPageNumber++;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                showErrorMessage();
            }
        });
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * Method for loading more movies as the user scrolls through the list
     *
     * @return
     */
    public InfiniteScrollListener createInfiniteScrollListener() {
        return new InfiniteScrollListener(mGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (mPageNumber <= mTotalPages) {
                    getMoviesFromDatabase(mPageNumber);
                }
            }
        };
    }
}

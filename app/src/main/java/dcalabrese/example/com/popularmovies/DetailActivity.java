/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import dcalabrese.example.com.popularmovies.adapters.MovieDetailsAdapter;
import dcalabrese.example.com.popularmovies.helpers.JsonResponseParser;
import dcalabrese.example.com.popularmovies.helpers.MovieContract;
import dcalabrese.example.com.popularmovies.helpers.NetworkUtils;
import dcalabrese.example.com.popularmovies.objects.MovieReview;
import dcalabrese.example.com.popularmovies.objects.MovieTrailer;

/**
 * Class for the activity that is created when a user clicks on a specific movie
 */
public class DetailActivity extends AppCompatActivity implements TrailerOnClickHandler {

    //recyclerview for the movie trailers and reviews
    private RecyclerView mDetailsRecylerView;
    private MovieDetailsAdapter mMovieDetailsAdapter;
    private LinearLayoutManager mDetailsLinearLayoutManager;

    private boolean areTrailersLoaded = false;

    //favorites button
    private CheckBox mStarButton;

    private TextView mMovieTitle, mMovieReleaseDate, mMovieRating, mMovieSynopsis;
    private ImageView mMoviePoster;

    private String mTitle, mReleaseDate, mRating, mSynopsis, mPosterPath, mMovieId;

    Cursor mCursor;

    /**
     * Creates the view to display a movie's details
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setTitle(R.string.detail_actionbar);

        mMovieTitle = (TextView) findViewById(R.id.tv_title);
        mMovieReleaseDate = (TextView) findViewById(R.id.tv_release_date);
        mMovieRating = (TextView) findViewById(R.id.tv_rating);
        mMovieSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        mMoviePoster = (ImageView) findViewById(R.id.iv_detail_poster);
        String imageTransitionName = getIntent().getExtras()
                .getString(MainActivity.INTENT_EXTRA_TRANSITION_NAME);
        mMoviePoster.setTransitionName(imageTransitionName);
        mStarButton = (CheckBox) findViewById(R.id.cb_star);

        mDetailsRecylerView = (RecyclerView) findViewById(R.id.rv_trailers);
        mDetailsRecylerView.setHasFixedSize(true);
        mDetailsLinearLayoutManager = new LinearLayoutManager(this);
        mDetailsRecylerView.setLayoutManager(mDetailsLinearLayoutManager);
        mMovieDetailsAdapter = new MovieDetailsAdapter(this);
        mDetailsRecylerView.setAdapter(mMovieDetailsAdapter);
        mDetailsRecylerView.addOnScrollListener(createInfiniteScrollListener());
        mDetailsRecylerView.setNestedScrollingEnabled(false);

        mDetailsRecylerView.setFocusable(false);

        Intent intentThatStartedThisActivity = getIntent();

        getIntentData(intentThatStartedThisActivity);
        setFavorite();
    }

    /**
     * Method for determining if a movie is favorite. If the movie is a favorite the favorite icon
     * should show as pressed
     */
    public void setFavorite() {
        mCursor = getContentResolver().query(MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                new String[] {MovieContract.MovieEntry.MOVIE_COLUMN_NAME },
                MovieContract.MovieEntry.MOVIE_COLUMN_NAME + "=?",
                new String[] {mMovieTitle.getText().toString()},
                null);
        mCursor.moveToFirst();

        if (mCursor.moveToFirst()) {
            mStarButton.setChecked(true);
        } else {
            mStarButton.setChecked(false);
        }
        mCursor.close();
    }

    /**
     * Method for converting an imageview's contents to a byte array for storage in the database
     *
     * @param poster The imageview of the image
     * @return Byte array of the image
     */
    public static byte[] getBitmapAsByteArray(ImageView poster) {
        Bitmap bitmap = ((BitmapDrawable) poster.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Method for adding a movie to the favorites database when the favorite button is pressed
     *
     * @param view The view of the button
     */
    public void updateFavorites(View view) {
        boolean isChecked = ((CheckBox) view).isChecked();
        Uri movieUri = MovieContract.MovieEntry.MOVIE_CONTENT_URI;
        Uri reviewUri = MovieContract.ReviewEntry.REVIEW_CONTENT_URI;
        if (isChecked) {
            ContentValues movieValues = new ContentValues();
            movieValues.put(MovieContract.MovieEntry.MOVIE_COLUMN_DATE, mMovieReleaseDate.getText().toString());
            movieValues.put(MovieContract.MovieEntry.MOVIE_COLUMN_NAME, mMovieTitle.getText().toString());
            movieValues.put(MovieContract.MovieEntry.MOVIE_COLUMN_RATING, mMovieRating.getText().toString());
            movieValues.put(MovieContract.MovieEntry.MOVIE_COLUMN_SYNOPSIS, mMovieSynopsis.getText().toString());
            byte[] posterBytes = getBitmapAsByteArray(mMoviePoster);
            movieValues.put(MovieContract.MovieEntry.MOVIE_COLUMN_POSTER, posterBytes);
            Uri uriId = getContentResolver().insert(movieUri, movieValues);
            long id = ContentUris.parseId(uriId);

            ContentValues reviewValues = new ContentValues();
            ArrayList<Object> list = mMovieDetailsAdapter.getMovieDetailList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof MovieReview) {
                    reviewValues.put(MovieContract.ReviewEntry.REVIEW_COLUMN_CONTENT,
                            ((MovieReview) list.get(i)).getReviewContent());
                    reviewValues.put(MovieContract.ReviewEntry.REVIEW_COLUMN_FOREIGN_KEY, id);
                    getContentResolver().insert(reviewUri, reviewValues);
                }

            }
        } else {
            String selection = mMovieTitle.getText().toString();
            String[] queryProjection = new String[] {MovieContract.MovieEntry._ID};
            Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                    null,
                    MovieContract.MovieEntry.MOVIE_COLUMN_NAME + "=?",
                    new String[] {selection}, null);
            getContentResolver().delete(movieUri, MovieContract.MovieEntry.MOVIE_COLUMN_NAME + "=?",
                    new String[] {selection});
            cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex(MovieContract.ReviewEntry._ID);
            long id = cursor.getLong(idIndex);
            getContentResolver().delete(reviewUri, MovieContract.ReviewEntry.REVIEW_COLUMN_FOREIGN_KEY + "=?",
                    new String[] {Long.toString(id)});

        }
    }




    /**
     * Handles opening the youtube url of a movie trailer
     *
     * @param trailer The movie trailer pressed
     */
    @Override
    public void OnTrailerClick(MovieTrailer trailer) {
        String url = NetworkUtils.buildYoutubeUrl(trailer.getYoutubeKey());
        Intent intentToStartYoutubeTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intentToStartYoutubeTrailer);
    }

    /**
     * Gets movie trailers for a specific movie from the movie database
     *
     * @param movieId The id of the movie whose trailers to get
     */
    public void getMovieTrailers(String movieId) {
        String url;
        url = NetworkUtils.buildTrailerUrl(movieId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    ArrayList<Object> detailsList = new ArrayList<>();
                    @Override
                    public void onResponse(JSONObject response) {
                        detailsList = JsonResponseParser.parseJsonTrailerData(response);
                        mMovieDetailsAdapter.setData(detailsList);
                        areTrailersLoaded = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Gets movie reviews for a specific movie from the movie database
     *
     * @param movieId The id of the movie whose reviews to get
     */
    public void getMovieReviews(String movieId) {
        String url;
        url = NetworkUtils.buildReviewUrl(movieId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    ArrayList<Object> reviews = new ArrayList<>();
                    @Override
                    public void onResponse(JSONObject response) {
                        reviews = JsonResponseParser.parseJsonReviewData(response);

                        mMovieDetailsAdapter.addData(reviews);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MyRequestQueue.getInstance(this.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Creates a scroll listener to load the movie reviews when the movie trailers have all been
     * loaded
     *
     * @return
     */
    public InfiniteScrollListener createInfiniteScrollListener() {
        return new InfiniteScrollListener(mDetailsLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (areTrailersLoaded) {
                    getMovieReviews(mMovieId);
                    areTrailersLoaded = false;
                }
            }
        };
    }

    /**
     * Determines how to proceed depending on the intent that started the activity
     * If the intent was started from the MainActivity, the the movie data was passed with the intent
     * If the intent was started from the FavoritesActivity, the movie data needs to be queried
     * from the local database
     *
     * @param intent The intent that started the activity
     */
    public void getIntentData(Intent intent) {
        if (intent.getStringExtra(MainActivity.SOURCE).equals(MainActivity.MAIN)) {
            mTitle = intent.getStringExtra(MainActivity.MOVIE_TITLE);
            mSynopsis = intent.getStringExtra(MainActivity.MOVIE_SYNOPSIS);
            mReleaseDate = intent.getStringExtra(MainActivity.MOVIE_RELEASE_DATE);
            mRating = intent.getStringExtra(MainActivity.MOVIE_RATING);
            mPosterPath = intent.getStringExtra(MainActivity.MOVIE_IMAGE_PATH);
            mMovieId = intent.getStringExtra(MainActivity.MOVIE_ID);

            mMovieTitle.setText(mTitle);

            Resources res = getResources();
            mReleaseDate = mReleaseDate.substring(0,4);
            mMovieReleaseDate.setText(mReleaseDate);

            String rating = res.getString(R.string.rating_text, mRating);
            mMovieRating.setText(rating);
            mMovieSynopsis.setText(mSynopsis);
            Context context = getApplicationContext();
            Picasso.with(context)
                    .load(NetworkUtils.builtPosterURL(mPosterPath))
                    .into(mMoviePoster);
            getMovieTrailers(mMovieId);
        } else {
            String title = intent.getStringExtra(FavoritesActivity.MOVIE_TITLE);
            mCursor = getContentResolver().query(MovieContract.MovieEntry.MOVIE_CONTENT_URI, null,
                    MovieContract.MovieEntry.MOVIE_COLUMN_NAME + "=?",
                    new String[] {title}, null);
            mCursor.moveToFirst();
            String date = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_DATE));
            String rating = mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_RATING));
            String synopsis =
                    mCursor.getString(mCursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_SYNOPSIS));
            byte[] poster =
                    mCursor.getBlob(mCursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_POSTER));
            Bitmap bmp = BitmapFactory.decodeByteArray(poster, 0, poster.length);
            mMovieTitle.setText(title);
            mMovieReleaseDate.setText(date);
            mMovieRating.setText(rating);
            mMovieSynopsis.setText(synopsis);
            mMoviePoster.setImageBitmap(bmp);
            mMovieDetailsAdapter.setData(getReviewsForFavorite());

        }
    }

    /**
     * Gets movie reviews for a favorite movie from the database
     *
     * @return Arraylist containing MovieReview objects
     */
    public ArrayList<Object> getReviewsForFavorite() {
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                null,
                MovieContract.MovieEntry.MOVIE_COLUMN_NAME + "=?",
                new String[] {mMovieTitle.getText().toString()},
                null);

        cursor.moveToFirst();
        int keyIndex = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
        long foreignKey = cursor.getLong(keyIndex);

        cursor.close();
        cursor = getContentResolver().query(MovieContract.ReviewEntry.REVIEW_CONTENT_URI,
                null,
                MovieContract.ReviewEntry.REVIEW_COLUMN_FOREIGN_KEY + "=?",
                new String[]{Long.toString(foreignKey)},
                null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            int contentIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.REVIEW_COLUMN_CONTENT);
            ArrayList<Object> reviews = new ArrayList<>();
            reviews.add(new MovieReview(cursor.getString(contentIndex)));
            while (cursor.moveToNext()) {
                reviews.add(new MovieReview(cursor.getString(contentIndex)));
            }
            cursor.close();
            return reviews;
        }

         return null;
    }
}

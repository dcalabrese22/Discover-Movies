/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dcalabrese.example.com.popularmovies.objects.MovieFromDatabase;
import dcalabrese.example.com.popularmovies.objects.MovieReview;
import dcalabrese.example.com.popularmovies.objects.MovieTrailer;

/**
 * Class for parsing json http responses
 */
public class JsonResponseParser {

    final static String RESULTS = "results";

    final static String MOVIE_ID = "id";
    final static String POSTER_PATH = "poster_path";
    final static String ORIGINAL_TITLE = "original_title";
    final static String PLOT_SYNOPSIS = "overview";
    final static String USER_RATING = "vote_average";
    final static String RELEASE_DATE = "release_date";
    final static String TOTAL_PAGES = "total_pages";

    final static String YOUTUBE_KEY = "key";
    final static String TRAILER_NAME = "name";

    final static String REVIEW_CONTENT = "content";

    private static int mTotalPages;

    /**
     * Parses json response data that details movies
     *
     * @param response The response as a JSONObject
     * @return An arraylist of MovieFromDatabase objects
     */
    public static ArrayList<MovieFromDatabase> parseJsonMovieData(JSONObject response) {
        ArrayList<MovieFromDatabase> movies = new ArrayList<>();
        try {
            JSONArray moviesArray = response.getJSONArray(RESULTS);

            for (int i = 0; i < moviesArray.length(); i++) {
                String id;
                String title;
                String poster;
                String synopsis;
                String rating;
                String releaseDate;
                MovieFromDatabase movie;

                mTotalPages = response.getInt(TOTAL_PAGES);

                JSONObject movieData = moviesArray.getJSONObject(i);

                id = movieData.getString(MOVIE_ID);
                title = movieData.getString(ORIGINAL_TITLE);
                poster = movieData.getString(POSTER_PATH);
                synopsis = movieData.getString(PLOT_SYNOPSIS);
                rating = movieData.getString(USER_RATING);
                releaseDate = movieData.getString(RELEASE_DATE);

                movie = new MovieFromDatabase(id, title, poster, synopsis, rating, releaseDate);
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    /**
     * Parses json http response for movie trailer data
     *
     * @param response The http response as a JSONObject
     * @return Arraylist containing movie trailers
     */
    public static ArrayList<Object> parseJsonTrailerData(JSONObject response) {
        ArrayList<Object> trailersList = new ArrayList<>();

        try {
            JSONArray trailersJsonArray = response.getJSONArray(RESULTS);

            for (int i = 0; i < trailersJsonArray.length(); i++) {
                String youtubeKey;
                String name;
                MovieTrailer movieTrailer;

                JSONObject trailerData = trailersJsonArray.getJSONObject(i);
                youtubeKey = trailerData.getString(YOUTUBE_KEY);
                name = trailerData.getString(TRAILER_NAME);

                movieTrailer = new MovieTrailer(youtubeKey, name);
                trailersList.add(movieTrailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailersList;
    }

    /**
     * Parses json http response for movie review data
     *
     * @param response The http response as a JSONObject
     * @return Arraylist containing movie reviews
     */
    public static ArrayList<Object> parseJsonReviewData(JSONObject response) {
        ArrayList<Object> reviewList = new ArrayList<>();

        try {
            JSONArray reviewJsonArray = response.getJSONArray(RESULTS);

            for (int i = 0; i < reviewJsonArray.length(); i++) {
                String reviewContent;
                MovieReview movieReview;
                JSONObject reviewData = reviewJsonArray.getJSONObject(i);
                reviewContent = reviewData.getString(REVIEW_CONTENT);

                movieReview = new MovieReview(reviewContent);
                reviewList.add(movieReview);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewList;
    }

    public static int getTotalPages() {
        return mTotalPages;
    }
}

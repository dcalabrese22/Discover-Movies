/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.helpers;

/**
 * Class used to communicate with the movie database
 */
public class NetworkUtils {

    private static final String DB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String DB_POPULAR_URL = "https://api.themoviedb.org/3/movie/popular";
    private static final String DB_TOP_RATED_URL = "https://api.themoviedb.org/3/movie/top_rated";
    private static final String DB_RESULTS_PAGE = "&page=";
    private static final String DB_POSTER_URL = "http://image.tmdb.org/t/p/w185/";
    private static final String DB_VIDEOS_URLS = "/videos";
    private static final String DB_REVIEW_URL = "/reviews";
    private static final String API_KEY = "?api_key=" + "7609aa2463f2fe7101ac9701637598a7";
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    /**
     * Builds the url to query the most popular movies
     *
     * @param page The specific popular movie page
     * @return The complete url as a string
     */
    public static String buildPopularURL(int page) {
        String urlString;
        urlString = DB_POPULAR_URL + API_KEY + DB_RESULTS_PAGE + Integer.toString(page);
        return urlString;
    }

    /**
     * Builds the url to query the top rated movies
     *
     * @param page The specific top rated movie page
     * @return The complete url as a string
     */
    public static String buildTopRatedURL(int page) {
        String urlString;
        urlString = DB_TOP_RATED_URL + API_KEY + DB_RESULTS_PAGE + Integer.toString(page);
        return urlString;
    }

    /**
     * Builds the url to query the movie poster
     *
     * @param posterPath Path to the movie poster
     * @return The complete url as a string
     */
    public static String builtPosterURL(String posterPath) {
        String urlString;
        urlString = DB_POSTER_URL + posterPath;
        return urlString;
    }

    /**
     * Builds the url to query movie trailers
     *
     * @param movieId The MovieFromDatabase movie id
     * @return The complete url as a string
     */
    public static String buildTrailerUrl(String movieId) {
        String urlString;
        urlString = DB_BASE_URL + movieId + DB_VIDEOS_URLS + API_KEY;
        return urlString;
    }

    /**
     * Builds the url for the movie trailer youtube link
     *
     * @param youtubeId The MovieTrailer youtube id
     * @return The complete url as a string
     */
    public static String buildYoutubeUrl(String youtubeId) {
        String urlString;
        urlString = YOUTUBE_URL + youtubeId;
        return urlString;
    }

    /**
     * Builds the url for the movie reviews
     *
     * @param movieId The MovieFromDatabase movie id
     * @return The complete url as a string
     */
    public static String buildReviewUrl(String movieId) {
        String urlString;
        urlString = DB_BASE_URL + movieId + DB_REVIEW_URL + API_KEY;
        return urlString;
    }
}


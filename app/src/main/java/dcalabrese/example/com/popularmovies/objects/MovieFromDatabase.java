/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.objects;

/**
 * Movie objects that are retrieved from the movie database
 */
public class MovieFromDatabase {

    private String mMovieId;
    private String mTitle;
    private String mImagePath;
    private String mSynopsis;
    private String mRating;
    private String mReleaseDate;


    /**
     * Constructor for how to create a new MovieFromDatabase object
     *
     * @param id Id of the movie
     * @param title Title of the movie
     * @param imagePath Image path of the movie poster
     * @param synopsis Synopsis of the movie
     * @param rating Rating of the movie
     * @param releaseDate Release data of the movie
     */
    public MovieFromDatabase(String id, String title, String imagePath, String synopsis, String rating,
                             String releaseDate) {
        mMovieId = id;
        mTitle = title;
        mImagePath = imagePath;
        mSynopsis = synopsis;
        mRating = rating;
        mReleaseDate = releaseDate;
    }

    public String getMovieId() {
        return mMovieId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public String getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }
}

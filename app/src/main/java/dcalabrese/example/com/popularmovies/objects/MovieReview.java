/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.objects;

/**
 * Movie reviews that are retrieved from the movie database
 */
public class MovieReview {

    private String mReviewContent;

    public MovieReview(String reviewContent) {
        mReviewContent = reviewContent;
    }

    public String getReviewContent() {
        return mReviewContent;
    }

}

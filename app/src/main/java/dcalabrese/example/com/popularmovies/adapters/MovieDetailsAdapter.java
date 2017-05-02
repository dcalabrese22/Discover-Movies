/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import dcalabrese.example.com.popularmovies.R;
import dcalabrese.example.com.popularmovies.TrailerOnClickHandler;
import dcalabrese.example.com.popularmovies.objects.MovieReview;
import dcalabrese.example.com.popularmovies.objects.MovieTrailer;

/**
 * Adapter for recyclerview that displays trailers and reviews of a movie
 */
public class MovieDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //arraylist that will contain either movie trailers or movie review objects
    private ArrayList<Object> mMovieDetailsList;
    private TrailerOnClickHandler mOnClickHandler;

    //viewtype constants that differentiate between trailer or review viewholders
    private static final int ITEM_TYPE_TRAILER = 2;
    private static final int ITEM_TYPE_REVIEW = 3;

    public MovieDetailsAdapter(TrailerOnClickHandler clickHandler) {
        mOnClickHandler = clickHandler;
    }

    public ArrayList<Object> getMovieDetailList() {
        return mMovieDetailsList;
    }

    /**
     * Sets the data for the recyclerview
     *
     * @param details The arraylist of trailer and review objects
     */
    public void setData(ArrayList<Object> details) {
        mMovieDetailsList = details;
        notifyDataSetChanged();
    }

    /**
     * Adds more data to arraylist of trailers and review objects
     *
     * @param details The arraylist of objects to add
     */
    public void addData(ArrayList<Object> details) {
        if (mMovieDetailsList == null) {
            return;
        } else {
            mMovieDetailsList.addAll(details);
            notifyDataSetChanged();
        }
    }

    /**
     * Gets the size of the data
     *
     * @return the size of the data
     */
    @Override
    public int getItemCount() {
        if (mMovieDetailsList == null) {
            return 0;
        } else {
            return mMovieDetailsList.size();
        }
    }

    /**
     * Creates and returns either a MovieTrailerViewHolder or ReviewViewHolder depending on the
     * viewType
     *
     * @param parent
     * @param viewType The specified view type (either type Trailer or type Review)
     * @return the new view holder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == ITEM_TYPE_TRAILER) {
            int layoutIdForTrailerItem = R.layout.trailer_item;
            View view = inflater.inflate(layoutIdForTrailerItem, parent, false);
            return new MovieTrailerViewHolder(view);
        } else {
            int layoutIdForReviewItem = R.layout.review_item;
            View view = inflater.inflate(layoutIdForReviewItem, parent, false);
            return new ReviewViewHolder(view);
        }
    }

    /**
     * Binds either a movie trailer or movie review data to the viewholder based on the view type
     * of the holder
     *
     * @param holder The view holder to bind data to
     * @param position The position of the view holder in the recyclerview
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //the viewtype of the view holder
        int itemType = getItemViewType(position);
        //if the view type is for a trailer, bind the data to the trailer name
        if (itemType == ITEM_TYPE_TRAILER) {
            TextView target = ((MovieTrailerViewHolder) holder).mTrailerName;
            MovieTrailer trailer = (MovieTrailer) mMovieDetailsList.get(position);
            String trailerName = trailer.getName();
            target.setText(trailerName);
        //if the view type if for a review, bind the data bind the review content to the view holder
        } else if (itemType == ITEM_TYPE_REVIEW) {
            TextView target = ((ReviewViewHolder) holder).mReview;
            MovieReview review = (MovieReview) mMovieDetailsList.get(position);
            String reviewContent = review.getReviewContent();
            target.setText(reviewContent);
        }
    }

    /**
     * Gets the view type of the view holder
     *
     * @param position The position of the view holder in the recycler view
     * @return The item view type int
     */
    @Override
    public int getItemViewType(int position) {
        if (mMovieDetailsList.get(position) instanceof MovieTrailer) {
            return ITEM_TYPE_TRAILER;
        } else {
            return ITEM_TYPE_REVIEW;
        }
    }

    /**
     * Class for a view holder that is movie trailer
     */
    public class MovieTrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTrailerName;

        public MovieTrailerViewHolder(View view) {
            super(view);
            mTrailerName = (TextView) view.findViewById(R.id.tv_trailer_name);
            view.setOnClickListener(this);
        }

        /**
         * Handles trailer items presses. This will eventually open the trailer to watch
         *
         * @param view
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            if (mMovieDetailsList.get(adapterPosition) instanceof MovieTrailer) {
                MovieTrailer trailer = (MovieTrailer) mMovieDetailsList.get(adapterPosition);
                mOnClickHandler.OnTrailerClick(trailer);
            } else {
                return;
            }
        }
    }

    /**
     * Class for a view holder that for a movie review
     */
    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mReview;

        public ReviewViewHolder(View view) {
            super(view);
            mReview = (TextView) view.findViewById(R.id.tv_review);
        }
    }

}

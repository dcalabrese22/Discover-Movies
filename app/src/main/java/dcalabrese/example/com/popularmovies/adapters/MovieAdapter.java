/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dcalabrese.example.com.popularmovies.AdapterOnClickHandler;
import dcalabrese.example.com.popularmovies.R;
import dcalabrese.example.com.popularmovies.helpers.NetworkUtils;
import dcalabrese.example.com.popularmovies.objects.MovieFromDatabase;

/**
 * Adapter for handling recyclerviews in the main activity
 */

public class    MovieAdapter extends RecyclerView.Adapter<MovieAdapter.AdapterViewHolder> {

    private ArrayList<MovieFromDatabase> mMoviesFromDatabase;
    private AdapterOnClickHandler mOnClickHandler;
    private Context mContext;

    /**
     * Constructor for class
     *
     * @param onClickHandler
     */
    public MovieAdapter(Context context, AdapterOnClickHandler onClickHandler) {
        mOnClickHandler = onClickHandler;
        mContext = context;

    }

    public void setMovieData(ArrayList<MovieFromDatabase> moviesFromDatabase) {
        mMoviesFromDatabase = moviesFromDatabase;
        notifyDataSetChanged();
    }

    /**
     * Adds MovieFromDatabase ojects in an ArrayList to another ArrayList
     * @param moviesFromDatabse The ArrayList to add
     */
    public void addMoviedata(ArrayList<MovieFromDatabase> moviesFromDatabse) {
        mMoviesFromDatabase.addAll(moviesFromDatabse);
        notifyDataSetChanged();
    }

    /**
     * Get the number of items in the ArrayList
     *
     * @return Size of ArrayList
     */
    @Override
    public int getItemCount() {
        if (mMoviesFromDatabase == null) {
            return 0;
        } else {
            return mMoviesFromDatabase.size();
        }
    }

//------------------------------------------------------------------------------------------------//

    /**
     * Class for creating each item view in the RecyclerView
     */
    public class AdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView mMoviePosterDisplay;

        /**
         * Creates a new AdapterViewHolder
         *
         * @param view The view to create
         */
        public AdapterViewHolder(View view) {
            super(view);
            mMoviePosterDisplay = (ImageView) view.findViewById(R.id.iv_movie_item);
//            view.setOnClickListener(this);
        }

        /**
         * Handles clicks in each view holder
         *
         */
//        @Override
//        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
//            MovieFromDatabase movie = mMoviesFromDatabase.get(adapterPosition);
//            mOnClickHandler.onMovieClick(movie, adapterPosition, mMoviePosterDisplay);
//        }

        public int moviePosition() {
            return getAdapterPosition();
        }
    }
//------------------------------------------------------------------------------------------------//

    AdapterViewHolder viewHolder;

    public int getViewHolderPosition() {
        return viewHolder.moviePosition();
    }

    /**
     * Creates and returns a new AdapterViewHolder
     *
     * @param parent
     * @param viewType
     * @return A new AdapterViewHolder in the view
     */
    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        viewHolder = new AdapterViewHolder(view);
        return viewHolder;
    }

    /**
     * Binds a movie poster image to the specified view holder
     * @param holder The holder to bind to
     * @param position The position in the recyclerview
     */
    @Override
    public void onBindViewHolder(final AdapterViewHolder holder, final int position) {
        final ImageView target = holder.mMoviePosterDisplay;
        String moviePosterPath = mMoviesFromDatabase.get(position).getImagePath();
        String moviePosterUrl = NetworkUtils.builtPosterURL(moviePosterPath);
        Picasso.with(holder.mMoviePosterDisplay.getContext())
                .load(moviePosterUrl)
                .into(target);

        final MovieFromDatabase movie = mMoviesFromDatabase.get(position);
        target.setTransitionName(movie.getMovieId());
        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickHandler.onMovieClick(movie, holder.getAdapterPosition(), target);
            }
        });

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickHandler.onMovieClick(movie, position, target);
            }
        });
    }
}

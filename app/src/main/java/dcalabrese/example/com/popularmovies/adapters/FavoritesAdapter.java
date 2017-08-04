/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies.adapters;

import android.content.Context;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dcalabrese.example.com.popularmovies.FavoriteOnClickHandler;
import dcalabrese.example.com.popularmovies.R;
import dcalabrese.example.com.popularmovies.helpers.MovieContract;

/**
 * Adapter for handling recyclerviews in the favorites activity
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private FavoriteOnClickHandler mClickHandler;
    private Cursor mCursor;
    private Context mContext;

    /**
     *
     * @param context
     * @param favorite
     */
    public FavoritesAdapter(Context context, FavoriteOnClickHandler favorite) {
        mClickHandler = favorite;
        mContext = context;
    }

    /**
     * Gets the number of items in the cursor
     *
     * @return The number of items in the cursor
     */
    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        } else {
            return mCursor.getCount();
        }
    }

    /**
     * Swaps the cursor
     *
     * @param c The cursor to swap
     * @return The swapped cursor if it was made otherwise null
     */
    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        mCursor = c;

        if (c != null) {
            notifyDataSetChanged();
        }
        return  temp;
    }

    /**
     * Creates and returns a new FavoriteViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        int favoriteLayoutId = R.layout.favorite_item;
        View view = inflater.inflate(favoriteLayoutId, parent, false);
        return new FavoriteViewHolder(view);
    }

    /**
     * Binds the movie poster and movie title to the view holder
     *
     * @param holder FavoriteViewHolder to bind to
     * @param position Position of the FavoriteViewHolder in the recyclerview
     */
    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, final int position) {
        //column index of the movie posters in the database
        int picIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_POSTER);
        //column index of the title in the database
        int titleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.MOVIE_COLUMN_NAME);

        //move the cursor to the position in the database, this position is also the same as
        //the position of the view holder in the recyclerview
        mCursor.moveToPosition(position);

        //get the title and picture data from the database
        final String title = mCursor.getString(titleIndex);
        byte[] pic = mCursor.getBlob(picIndex);

        //set the Textview to the movie title
        holder.mTitle.setText(title);
        //decode the image and set it in the imageview
        Bitmap bmp = BitmapFactory.decodeByteArray(pic, 0, pic.length);
        holder.mPoster.setImageBitmap(bmp);

        holder.mPoster.setTransitionName(title);

        holder.mPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickHandler.onFavoriteClick(title, holder.getAdapterPosition(), holder.mPoster);
            }
        });

        holder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickHandler.onFavoriteClick(title, holder.getAdapterPosition(), holder.mPoster);
            }
        });
    }

    /**
     * Class for creating each view in the favorites recyclerview
     */
    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        ImageView mPoster;
        TextView mTitle;

        /**
         * Create a new FavoriteViewHolder
         *
         * @param view
         */
        public FavoriteViewHolder(View view) {
            super(view);
            mPoster = (ImageView) view.findViewById(R.id.iv_favorite_poster);
            mTitle = (TextView) view.findViewById(R.id.tv_favorite_title);
//            view.setOnClickListener(this);
        }

//        /**
//         * Handles click of each view holder
//         *
//         * @param v
//         */
//        @Override
//        public void onClick(View v) {
//            int adapterPosition = getAdapterPosition();
//            mCursor.moveToPosition(adapterPosition);
//            mClickHandler.onFavoriteClick(mCursor, adapterPosition, v);
//        }
    }
}

/*
 * Copyright (C) 2013 The Android Open Source Project
 */

package dcalabrese.example.com.popularmovies;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Custom queue that will last the lifetime of the entire application
 */
public class MyRequestQueue {

    private static MyRequestQueue mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;


    private MyRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     * Gets the instance of the requestqueue
     *
     * @param context
     * @return
     */
    public static synchronized MyRequestQueue getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyRequestQueue(context);
        }
        return mInstance;
    }

    /**
     * Get the requestqueue
     *
     * @return
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Add a network request to the queueu
     *
     * @param req
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

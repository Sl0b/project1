package com.example.android.project1;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sl0b on 25/05/16.
 */
public class Movies {
    @SuppressWarnings("unused")
    @SerializedName("page")
    private int mPage;

    @SuppressWarnings("unused")
    @SerializedName("results")
    private Movie[] mMovies;

    public Movie[] getMovies() {
        return mMovies;
    }

    public int getPage() {
        return mPage;
    }
}
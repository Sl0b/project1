package com.example.android.project1;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sl0b on 25/05/16.
 */
public class Trailers {
    @SuppressWarnings("unused")
    @SerializedName("results")
    private Trailer[] mTrailers;

    public Trailer[] getTrailers() {
        return mTrailers;
    }
}
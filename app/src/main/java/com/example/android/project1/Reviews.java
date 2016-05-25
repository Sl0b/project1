package com.example.android.project1;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sl0b on 25/05/16.
 */
public class Reviews {
    @SuppressWarnings("unused")
    @SerializedName("results")
    private Review[] mReviews;

    public Review[] getReviews() {
        return mReviews;
    }
}
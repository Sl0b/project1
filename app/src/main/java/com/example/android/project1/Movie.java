package com.example.android.project1;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sl0b on 23/03/16.
 */
public class Movie implements Serializable {
    private static final String POSTER_IMAGE_URL_PREFIX = "http://image.tmdb.org/t/p/w185";

    @SerializedName("id")
    private int mMovieId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("overview")
    private String mOverview;

    @SerializedName("poster_path")
    private String mPosterImageUrl;

    @SerializedName("release_date")
    private String mReleaseDate;

    @SerializedName("vote_average")
    private float mVoteAverage;

    @SerializedName("vote_count")
    private int mVoteCount;

    public int getMovieId() {
        return mMovieId;
    }

    @NonNull
    public String getTitle() {
        return TextUtils.isEmpty(mTitle) ? "" : mTitle;
    }

    @NonNull
    public String getOverview() {
        return TextUtils.isEmpty(mOverview) ? "" : mOverview;
    }

    @NonNull
    public String getPosterImageUrl() {
        return TextUtils.isEmpty(mPosterImageUrl) ? "" : POSTER_IMAGE_URL_PREFIX + mPosterImageUrl;
    }

    @NonNull
    public String getReleaseDate() {
        return TextUtils.isEmpty(mReleaseDate) ? "" : mReleaseDate;
    }

    public float getVoteAverage() {
        return mVoteAverage;
    }

    public int getVoteCount() {
        return mVoteCount;
    }
}
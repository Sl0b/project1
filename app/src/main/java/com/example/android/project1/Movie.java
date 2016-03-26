package com.example.android.project1;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sl0b on 23/03/16.
 */
public class Movie implements Serializable {
    private static final String IMG_BASE_URL = "http://image.tmdb.org/t/p";
    private static final String POSTER_REZ = "/w185";
    private static final String BACKDROP_REZ = "/w342";
    private static final String BACKDROP_LANDSCAPE_REZ = "/w500";

    @SerializedName("id")
    private int mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("overview")
    private String mOverview;

    @SerializedName("poster_path")
    private String mPosterPath;

    @SerializedName("backdrop_path")
    private String mBackdrop;

    @SerializedName("release_date")
    private String mReleaseDate;

    @SerializedName("vote_average")
    private float mVoteAverage;

    @SerializedName("vote_count")
    private int mVoteCount;

    public int getId() {
        return mId;
    }

    @NonNull
    public String getTitle() {
        return TextUtils.isEmpty(mTitle) ? "" : mTitle;
    }

    @NonNull
    public String getOverview() {
        return TextUtils.isEmpty(mOverview) ? "" : mOverview;
    }

    /**
     * Get the poster or the brackdrop url
     *
     * @param isPoster true is you want the poster url, false for the backdrop.
     * @param isLandscape true if the device is in landscape, false for portrait.
     */
    @NonNull
    public String getImageUrl(boolean isPoster, boolean isLandscape) {
        if (isPoster) {
            return TextUtils.isEmpty(mPosterPath) ? "" : IMG_BASE_URL + POSTER_REZ + mPosterPath;
        } else if (isLandscape) {
            return TextUtils.isEmpty(mBackdrop) ? "" : IMG_BASE_URL + BACKDROP_LANDSCAPE_REZ + mBackdrop;
        } else {
            return TextUtils.isEmpty(mBackdrop) ? "" : IMG_BASE_URL + BACKDROP_REZ + mBackdrop;
        }
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
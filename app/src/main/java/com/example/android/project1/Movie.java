package com.example.android.project1;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sl0b on 23/03/16.
 */
public class Movie implements Parcelable {
    private static final String IMG_BASE_URL = "http://image.tmdb.org/t/p";
    private static final String POSTER_REZ = "/w185";
    private static final String BACKDROP_REZ = "/w342";
    private static final String BACKDROP_LANDSCAPE_REZ = "/w500";

    @SerializedName("id")
    private String mId;

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

    public String getId() {
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

    // Parcelable stuff
    private Movie(Parcel in) {
        this.mId = in.readString();
        this.mTitle = in.readString();
        this.mOverview = in.readString();
        this.mPosterPath = in.readString();
        this.mBackdrop = in.readString();
        this.mReleaseDate = in.readString();
        this.mVoteAverage = in.readFloat();
        this.mVoteCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mOverview);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdrop);
        dest.writeString(mReleaseDate);
        dest.writeFloat(mVoteAverage);
        dest.writeInt(mVoteCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
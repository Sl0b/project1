package com.example.android.project1;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Review implements Parcelable {

    @SerializedName("author")
    private String mAuthor;

    @SerializedName("content")
    private String mContent;

    public String getAuthor() { return mAuthor; }

    public String getReview() { return mContent; }


    // Parcelable stuff
    private Review(Parcel in) {
        this.mAuthor = in.readString();
        this.mContent = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthor);
        dest.writeString(mContent);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {

        public Review createFromParcel(Parcel in) { return new Review(in); }

        public Review[] newArray(int size) { return new Review[size]; }
    };
}
package com.example.android.project1;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Trailer implements Parcelable {

    @SerializedName("key")
    private String mKey;

    public String getKey() { return mKey; }

    public String getTrailerUrl() { return "http://www.youtube.com/watch?v=" + mKey; }


    // Parcelable stuff
    private Trailer(Parcel in) {
        this.mKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {

        public Trailer createFromParcel(Parcel in) { return new Trailer(in); }

        public Trailer[] newArray(int size) { return new Trailer[size]; }
    };
}
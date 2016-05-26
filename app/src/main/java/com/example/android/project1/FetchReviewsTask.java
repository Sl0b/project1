package com.example.android.project1;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchReviewsTask extends AsyncTask<String, Void, Review[]> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    private DetailActivityFragment activity;
    private Review[] reviews;
    private boolean completed;

    public FetchReviewsTask(DetailActivityFragment activity) {
        this.activity = activity;
    }

    @Override
    protected Review[] doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";

            Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendPath("reviews")
                    .appendQueryParameter("api_key", BuildConfig.MOVIEDB_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to theMovieDB, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            Reviews reviews = new Gson().fromJson(reader, Reviews.class);

            return reviews.getReviews();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Review[] result) {
        if (result != null) {
            reviews = result;
            completed = true;
            notifyActivityTaskCompleted();
        }
    }

    private void notifyActivityTaskCompleted() {
        if ( null != activity ) {
            activity.onReviewTaskCompleted(reviews);
        }
    }

    public void setActivity(DetailActivityFragment activity) {
        this.activity = activity;
        if (completed) {
            notifyActivityTaskCompleted();
        }
    }
}
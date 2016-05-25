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

public class FetchTrailerTask extends AsyncTask<String, Void, Trailer[]> {

    private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

    private DetailActivity activity;
    private Trailer[] trailers;
    private boolean completed;

    public FetchTrailerTask(DetailActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Trailer[] doInBackground(String... params) {

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
                    .appendPath("videos")
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

            Trailers trailers = new Gson().fromJson(reader, Trailers.class);

            return trailers.getTrailers();
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
    protected void onPostExecute(Trailer[] result) {
        trailers = result;
        completed = true;
        notifyActivityTaskCompleted();
    }

    private void notifyActivityTaskCompleted() {
        if ( null != activity ) {
            activity.onTrailerTaskCompleted(trailers);
        }
    }

    public void setActivity(DetailActivity activity) {
        this.activity = activity;
        if (completed) {
            notifyActivityTaskCompleted();
        }
    }
}
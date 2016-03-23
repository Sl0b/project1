package com.example.android.project1;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by sl0b on 23/03/16.
 */
public class SortPosterFragment extends Fragment {

    private MovieAdapter mMoviesAdapter;

    public SortPosterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the GridView it's attached to.
        mMoviesAdapter = new MovieAdapter(
                getActivity(),
                R.layout.list_poster,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.posters_fragment, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_posters);
        gridView.setAdapter(mMoviesAdapter);

        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mMoviesAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });*/

        return rootView;
    }

    private void updateMovies() {
        FetchMoviesPostersTask postersTask = new FetchMoviesPostersTask();
        String sort = "top_rated";
        postersTask.execute(sort);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMoviesPostersTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesPostersTask.class.getSimpleName();

        @Override
        protected Movie[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter("api_key", /*YOUR_API_KEY*/)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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

                Movies movies = new Gson().fromJson(reader, Movies.class);

                Log.v(LOG_TAG, movies.getMovies()[0].getOverview());

                return movies.getMovies();
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
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                mMoviesAdapter.clear();
                for(Movie movie : result) {
                    mMoviesAdapter.add(movie.getPosterImageUrl());
                }
                // New data is back from the server.  Hooray!
            }
        }
    }

    private static class Movies {
        @SuppressWarnings("unused")
        @SerializedName("page")
        private int mResultPage;

        @SuppressWarnings("unused")
        @SerializedName("results")
        private Movie[] mMovies;

        public Movie[] getMovies() {
            return mMovies;
        }

        public int getResultPage() {
            return mResultPage;
        }
    }
}
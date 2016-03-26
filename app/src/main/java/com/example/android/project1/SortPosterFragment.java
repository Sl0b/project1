package com.example.android.project1;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.menu_sort_popular) {
            updateMovies(true);
            getActivity().setTitle(R.string.title_popular);
            Toast.makeText(getActivity().getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.menu_sort_rating) {
            updateMovies(false);
            getActivity().setTitle(R.string.title_rated);
            Toast.makeText(getActivity().getApplicationContext(), item.getTitle()   , Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take data from a source and
        // use it to populate the GridView it's attached to.
        mMoviesAdapter = new MovieAdapter(
                getActivity(),
                R.layout.list_poster,
                new ArrayList<Movie>());

        View rootView = inflater.inflate(R.layout.posters_fragment, container, false);

        // Make it 3 columns of posters if the device is in landscape
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_posters);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridView.setNumColumns(3);
        } else {
            gridView.setNumColumns(2);
        }

        gridView.setAdapter(mMoviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String movie = new Gson().toJson(mMoviesAdapter.getItem(position));
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("MOVIE", movie);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovies(boolean popular) {
        FetchMoviesPostersTask postersTask = new FetchMoviesPostersTask();
        if (popular) {
            postersTask.execute("popular");
        } else {
            postersTask.execute("top_rated");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies(true);
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

            try {
                final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";

                Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                        .appendPath(params[0])
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

                Movies movies = new Gson().fromJson(reader, Movies.class);

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
                    mMoviesAdapter.add(movie);
                }
            }
        }
    }

    private static class Movies {
        @SuppressWarnings("unused")
        @SerializedName("page")
        private int mPage;

        @SuppressWarnings("unused")
        @SerializedName("results")
        private Movie[] mMovies;

        public Movie[] getMovies() {
            return mMovies;
        }

        public int getPage() {
            return mPage;
        }
    }
}
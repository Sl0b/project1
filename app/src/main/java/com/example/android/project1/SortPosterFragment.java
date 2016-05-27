package com.example.android.project1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.project1.data.MovieContract;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by sl0b on 23/03/16.
 */
public class SortPosterFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = SortPosterFragment.class.getSimpleName();

    private static FetchMoviesTask fetchMoviesTask;

    public final static String MOST_POPULAR = "popular";
    public final static String TOP_RATED = "top_rated";
    public final static String FAVORITES = "favorites";

    private MovieAdapter mMoviesAdapter;

    public SortPosterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((fetchMoviesTask != null) && (fetchMoviesTask.getStatus() == AsyncTask.Status.RUNNING)) {
            fetchMoviesTask.setActivity(this);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem spinner = menu.findItem(R.id.action_spinner);
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String title = sharedPref.getString(getString(R.string.title_pref), getString(R.string.menu_most_popular));
        spinner.setTitle(title);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        int id = item.getItemId();
        if (id == R.id.menu_sort_popular) {
            updateMovies(MOST_POPULAR);
            editor.putString(getString(R.string.sort_pref), MOST_POPULAR);
            editor.putString(getString(R.string.title_pref), getString(R.string.menu_most_popular));
            editor.apply();
            return true;
        }
        if (id == R.id.menu_sort_rating) {
            updateMovies(TOP_RATED);
            editor.putString(getString(R.string.sort_pref), TOP_RATED);
            editor.putString(getString(R.string.title_pref), getString(R.string.menu_highest_rated));
            editor.apply();
            return true;
        }
        if (id == R.id.menu_sort_favorite) {
            updateMovies(FAVORITES);
            editor.putString(getString(R.string.sort_pref), FAVORITES);
            editor.putString(getString(R.string.title_pref), getString(R.string.menu_favorites));
            editor.apply();
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

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_posters);

        gridView.setAdapter(mMoviesAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (getActivity().findViewById(R.id.detail_container) != null) {
                    Movie movie = mMoviesAdapter.getItem(position);
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(DetailActivityFragment.ARG_MOVIE, movie);
                    DetailActivityFragment fragment = new DetailActivityFragment();
                    fragment.setArguments(arguments);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.detail_container, fragment)
                            .commit();
                } else {
                    String movie = new Gson().toJson(mMoviesAdapter.getItem(position));
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra("MOVIE", movie);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMoviesAdapter.clear();
        mMoviesAdapter.add(cursor);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // Not used
    }

    public void onTaskCompleted(Movie[] movies) {
        if (movies != null) {
            mMoviesAdapter.clear();
            for (Movie movie : movies) {
                mMoviesAdapter.add(movie);
            }
        }
    }

    private void updateMovies(String sortBy) {
        if (sortBy.equals(FAVORITES)) {
            getActivity().getSupportLoaderManager().initLoader(0, null, this);
        } else {
            FetchMoviesTask moviesTask = new FetchMoviesTask(this);
            fetchMoviesTask = moviesTask;
            moviesTask.execute(sortBy);
        }
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String sortBy = sharedPref.getString(getString(R.string.sort_pref), MOST_POPULAR);
        updateMovies(sortBy);
    }
}
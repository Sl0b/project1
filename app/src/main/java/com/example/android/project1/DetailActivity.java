package com.example.android.project1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private static Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailActivityFragment())
                    .commit();
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("MOVIE")) {
            mMovie = new Gson().fromJson(intent.getStringExtra("MOVIE"), Movie.class);
        }
    }

    public static class DetailActivityFragment extends Fragment {

        private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

        public DetailActivityFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            // Backdrop (top image)
            ImageView backdrop = (ImageView) rootView.findViewById(R.id.backdrop);
            Picasso.with(getActivity()).load(mMovie.getImageUrl(false)).into(backdrop);

            // Movie title
            ((TextView) rootView.findViewById(R.id.movie_title))
                    .setText(mMovie.getTitle());

            // Details: release date, vote average...
            TextView details = (TextView) rootView.findViewById(R.id.movie_details);
            // Release date, changing format, inside a try because need to handle ParseException
            String releaseDate = getString(R.string.release_date) + " ";
            try {
                String date = mMovie.getReleaseDate();
                SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date oldFormat = curFormater.parse(date);
                SimpleDateFormat postFormater = new SimpleDateFormat("yyyy, dd MMMM", Locale.US);
                releaseDate += postFormater.format(oldFormat);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "ParseException error.", e);
            }
            details.setText(releaseDate);
            // Vote average
            String voteAverage = "\n" +
                    getString(R.string.vote_average) +
                    " " +
                    mMovie.getVoteAverage() +
                    " (" +
                    mMovie.getVoteCount() +
                    " " +
                    getString(R.string.votes) +
                    ")";
            details.append(voteAverage);

            // Overview
            ((TextView) rootView.findViewById(R.id.movie_overview))
                    .setText(mMovie.getOverview());

            return rootView;
        }
    }
}

package com.example.android.project1;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private static FetchTrailerTask fetchTrailerTask;
    private static FetchReviewsTask fetchReviewsTask;
    private static Movie mMovie;
    private static String trailerUrl;

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

    @Override
    public void onResume() {
        super.onResume();
        if ((fetchTrailerTask != null) && (fetchTrailerTask.getStatus() == AsyncTask.Status.RUNNING)) {
            fetchTrailerTask.setActivity(this);
        }
        if ((fetchReviewsTask != null) && (fetchReviewsTask.getStatus() == AsyncTask.Status.RUNNING)) {
            fetchReviewsTask.setActivity(this);
        }
    }

    public void onTrailerTaskCompleted(Trailer[] trailers) {
        if (trailers != null) {
            trailerUrl = trailers[0].getTrailerUrl();
        }
    }

    public void onReviewTaskCompleted(Review[] reviews) {
        TextView author = (TextView)findViewById(R.id.review_author);
        TextView review = (TextView)findViewById(R.id.review);
        if (reviews.length > 0) {
            author.setText(reviews[0].getAuthor());
            review.setText(reviews[0].getReview());
        } else {
            review.setText("There is no review, sorry.");
        }
    }

    public void launchTrailer(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl)));
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
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Picasso.with(getActivity())
                        .load(mMovie.getImageUrl(false, true))
                        .error(R.drawable.backdrop_error)
                        .into(backdrop);
            } else {
                Picasso.with(getActivity())
                        .load(mMovie.getImageUrl(false, false))
                        .error(R.drawable.backdrop_error)
                        .into(backdrop);
            }

            final ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);

            scrollView.postDelayed(new Runnable() { @Override public void run() { scrollView.fullScroll(View.FOCUS_DOWN); } }, 1000);

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

    private void fetchTrailerAndReviews() {
        FetchTrailerTask trailerTask = new FetchTrailerTask(this);
        fetchTrailerTask = trailerTask;
        trailerTask.execute(mMovie.getId());
        FetchReviewsTask reviewsTask = new FetchReviewsTask(this);
        fetchReviewsTask = reviewsTask;
        reviewsTask.execute(mMovie.getId());
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchTrailerAndReviews();
    }
}

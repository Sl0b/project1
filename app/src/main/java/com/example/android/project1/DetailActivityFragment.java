package com.example.android.project1;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by sl0b on 26/05/16.
 */
public class DetailActivityFragment extends Fragment implements OnClickListener {

    private static FetchTrailerTask fetchTrailerTask;
    private static FetchReviewsTask fetchReviewsTask;
    private static Movie mMovie;
    private static String trailerUrl;

    ExpandableListAdapter reviewAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
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
        TextView errorPlaceholder = (TextView) getActivity().findViewById(R.id.no_reviews);
        if (reviews.length > 0) {
            listDataHeader.clear();
            listDataChild.clear();
            errorPlaceholder.setVisibility(View.GONE);
            for (int i = 0; i < reviews.length; i++) {
                listDataHeader.add(reviews[i].getAuthor());
                List<String> content = new ArrayList<String>();
                content.add(reviews[i].getReview());
                listDataChild.put(listDataHeader.get(i), content);
            }
            setListViewHeight(expListView);
        } else {
            errorPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trailer_button:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl)));
                break;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        reviewAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        expListView = (ExpandableListView) rootView.findViewById(R.id.reviews_listview);
        expListView.setAdapter(reviewAdapter);
        setListViewHeight(expListView);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });

        Button trailer = (Button) rootView.findViewById(R.id.trailer_button);
        trailer.setOnClickListener(this);

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

    private void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setListViewHeight(ExpandableListView listView,
                                   int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight() + 2;

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }
}

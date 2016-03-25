package com.example.android.project1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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

        public DetailActivityFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            ImageView backdrop = (ImageView) rootView.findViewById(R.id.backdrop);
            Picasso.with(getActivity()).load(mMovie.getImageUrl(false)).into(backdrop);
            ((TextView) rootView.findViewById(R.id.text_test))
                    .setText(mMovie.getTitle());

            return rootView;
        }
    }
}

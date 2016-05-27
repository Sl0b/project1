package com.example.android.project1;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.project1.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sl0b on 23/03/16.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    Context context;
    int layoutResId;
    List<Movie> data = null;

    public MovieAdapter(Context context, int layoutResId, List<Movie> data) {
        super(context, layoutResId, data);
        this.layoutResId = layoutResId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieHolder holder = null;

        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);

            holder = new MovieHolder();
            holder.poster = (ImageView)convertView.findViewById(R.id.list_poster_imageview);
            holder.title = (TextView)convertView.findViewById(R.id.title_textview);

            convertView.setTag(holder);
        }
        else
        {
            holder = (MovieHolder)convertView.getTag();
        }

        String url = data.get(position).getImageUrl(true, false);
        Picasso.with(this.context).load(url).error(R.drawable.poster_error).into(holder.poster);
        String title = data.get(position).getTitle();
        holder.title.setText(title);

        return convertView;
    }

    static class MovieHolder
    {
        ImageView poster;
        TextView title;
    }

    public void add(Cursor cursor) {
        data.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String id = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_ID);
                String title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                String overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);
                String posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH);
                String backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP_PATH);
                String releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE_DATE);
                String rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_VOTE_AVERAGE);
                String count = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_VOTE_COUNT);
                Movie movie = new Movie(id, title, overview, posterPath, backdropPath, releaseDate, rating, count);
                data.add(movie);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }
}
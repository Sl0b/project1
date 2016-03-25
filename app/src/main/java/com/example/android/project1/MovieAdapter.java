package com.example.android.project1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

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

            convertView.setTag(holder);
        }
        else
        {
            holder = (MovieHolder)convertView.getTag();
        }

        String url = data.get(position).getImageUrl(true);
        Picasso.with(this.context).load(url).into(holder.poster);

        return convertView;
    }

    static class MovieHolder
    {
        ImageView poster;
    }
}
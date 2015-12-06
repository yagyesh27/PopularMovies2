package com.myappsworld.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by HP PC on 26-10-2015.
 */
public class GridCustomAdapter extends BaseAdapter {

    private Context mContext;
    private String[] movies;
    public GridCustomAdapter(Context c, String[] movies) {
        super();
        mContext = c;
        this.movies = movies;

    }
    @Override
    public long getItemId(int position) {
        return 0;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.poster_grid_cell, null);
            ImageView imageView = (ImageView)grid.findViewById(R.id.posterthumb);
            imageView.setAdjustViewBounds(true);
            // Toast.makeText(mContext, "URL is " + movies[position], Toast.LENGTH_SHORT).show();
            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w500" + movies[position]).into(imageView);
            /*imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("click", "click");


                }
            });*/

        } else {
            grid = (View) convertView;
        }

        return grid;
    }

    @Override
    public int getCount() {
        return movies.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public GridCustomAdapter() {
        super();
    }
}


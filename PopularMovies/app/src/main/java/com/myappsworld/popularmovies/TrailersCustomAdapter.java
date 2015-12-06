package com.myappsworld.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by HP PC on 10-11-2015.
 */
public class TrailersCustomAdapter extends BaseAdapter {

    private Context mContext;
    private String[] trailer;
    private String[] key;
    public TrailersCustomAdapter(Context c, String[] trailer, String[] key ) {
        super();
        mContext = c;
        this.trailer = trailer;
        this.key = key;

    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View list;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {

            list = new View(mContext);
            list = inflater.inflate(R.layout.trailer_list_cell, null);
            ImageView imageView = (ImageView)list.findViewById(R.id.trailerthumb);
            imageView.setAdjustViewBounds(true);
            // Toast.makeText(mContext, "URL is " + movies[position], Toast.LENGTH_SHORT).show();
            Picasso.with(mContext).load("https://img.youtube.com/vi/" + key[position] +"/default.jpg" ).into(imageView);
            ((TextView) list.findViewById(R.id.trailer_name)).setText(trailer[position]);
            Log.d("name", trailer[position]);
            /*imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("click", "click");


                }
            });*/

        } else {
            list = (View) convertView;
        }

        return list;
    }

    @Override
    public int getCount() {
        return trailer.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }
}

package com.myappsworld.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null){

            DetailActivityFragment fragment =new DetailActivityFragment();
            fragment.setArguments(getIntent().getExtras());


            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container,fragment)
                    .commit();
        }





    }



}

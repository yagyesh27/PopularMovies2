package com.myappsworld.popularmovies;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback{
    String DETAILFRAGMENT_TAG = "DFTAG";
    static boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.movie_detail_container) != null){

            MainActivity.mTwoPane = true;

            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }

        }else{

            MainActivity.mTwoPane = false;

        }

    }


    @Override
    public void onItemSelected(Bundle extras) {
        if(MainActivity.mTwoPane){

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(extras);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();


        }else {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtras(extras);
            startActivity(i);
        }
    }
}

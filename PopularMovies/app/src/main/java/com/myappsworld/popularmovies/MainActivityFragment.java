package com.myappsworld.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public String posters[];
    public String title[];
    public String release_date[];
    public String vote_avg[];
    public String plot_synopsis[];
    public String movie_id[];
    public String backdrop_path[];
    View rootView;

    static String JSONstring;
    SQLiteDatabase db;



    public interface Callback{

        public void onItemSelected(Bundle extras);
    }

     public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mainfragment, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_sort_by_popularity) {
            Fetchdata fetchobj = new Fetchdata();
            fetchobj.execute("popularity.desc");
            return true;
        }

        if (id == R.id.action_sort_by_highestrated) {
            Fetchdata fetchobj = new Fetchdata();
            fetchobj.execute("vote_average.desc");
            return true;
        }

        if(id == R.id.action_favourites) {

            db = getContext().openOrCreateDatabase("movies.db", Context.MODE_PRIVATE, null);
            Cursor resultSet = db.rawQuery("Select * from FavouriteMovies", null);
            resultSet.moveToFirst();
            int rowsleft = resultSet.getCount();
            int i = 0;
            String[] posters1 = new String[rowsleft];

            if(rowsleft>0){
            while (rowsleft > 0) {

                movie_id[i] = resultSet.getString(0);
                posters1[i] = resultSet.getString(1);
                title[i] = resultSet.getString(2);
                release_date[i] = resultSet.getString(3);
                vote_avg[i] = resultSet.getString(4);
                plot_synopsis[i] = resultSet.getString(5);
                backdrop_path[i] = resultSet.getString(6);
                Log.d("fav data", movie_id[i] + posters[i]);
                resultSet.moveToNext();
                i++;
                rowsleft--;

            }

            posters = posters1;

            Log.d("length", posters.length + "");


            if (isAdded()) {


                GridCustomAdapter adapter = new GridCustomAdapter(getActivity(), posters);
                GridView grid = (GridView) rootView.findViewById(R.id.gridView);
                grid.setAdapter(adapter);


                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("Clicked", "Clicked");


                        //Intent i = new Intent(getActivity(), DetailActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString("poster_url", posters[position]);
                        extras.putString("title", title[position]);
                        extras.putString("release_date", release_date[position]);
                        extras.putString("vote_avg", vote_avg[position]);
                        extras.putString("plot_synopsis", plot_synopsis[position]);
                        extras.putString("movie_id", movie_id[position]);
                        extras.putString("backdrop_path",backdrop_path[position]);
                        ((Callback) getActivity()).onItemSelected(extras);
                        /*i.putExtras(extras);
                        startActivity(i);*/

                    }
                });


            }

                    if(MainActivity.mTwoPane) {
                        Bundle extras = new Bundle();
                        extras.putString("poster_url", posters[0]);
                        extras.putString("title", title[0]);
                        extras.putString("release_date", release_date[0]);
                        extras.putString("vote_avg", vote_avg[0]);
                        extras.putString("plot_synopsis", plot_synopsis[0]);
                        extras.putString("movie_id", movie_id[0]);
                        extras.putString("backdrop_path",backdrop_path[0]);
                        ((Callback) getActivity()).onItemSelected(extras);
                    }


        }else{

                Toast.makeText(getActivity(),"No Favourites Available",Toast.LENGTH_LONG);

            }


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Fetchdata fetchobj = new Fetchdata();
            fetchobj.execute("popularity.desc");





        return rootView;

    }

    public class Fetchdata extends AsyncTask<String , Void , ArrayList> {


        protected ArrayList doInBackground(String[] params) {




            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String dataJsonStr = null;

            try {

                String baseurl ="http://api.themoviedb.org/3/discover/movie?";
                String sort_param = params[0];

                Uri builturi= Uri.parse(baseurl).buildUpon()
                        .appendQueryParameter("sort_by",sort_param)
                        .appendQueryParameter("api_key",getString(R.string.api_key))
                        .build();
                URL url = new URL(builturi.toString());


                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                dataJsonStr = buffer.toString();
                MainActivityFragment.JSONstring = dataJsonStr;
                Log.d("Jsondata", dataJsonStr);
                Log.d("Jsondata", MainActivityFragment.JSONstring);
            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            ArrayList a = new ArrayList();
            try {
                JSONObject movieJson = new JSONObject(JSONstring);
                JSONArray movie = movieJson.getJSONArray("results");
                String[] posters = new String[movie.length()];
                String[] title = new String[movie.length()];
                String[] release_date = new String[movie.length()];
                String[] vote_avg = new String[movie.length()];
                String[] plot_synopsis = new String[movie.length()];
                String[] movie_id = new String[movie.length()];
                String[] backdrop_path = new String[movie.length()];

                JSONObject ob;
                for(int i=0;i<movie.length();i++){
                    ob = movie.getJSONObject(i);
                    posters[i] = ob.getString("poster_path") ;
                    title[i] = ob.getString("original_title") ;
                    release_date[i] = ob.getString("release_date") ;
                    vote_avg[i] = ob.getString("vote_average") ;
                    plot_synopsis[i] = ob.getString("overview") ;
                    movie_id[i] = ob.getString("id");
                    backdrop_path[i] = ob.getString("backdrop_path");

                    a.add(0,posters);
                    a.add(1,title);
                    a.add(2,release_date);
                    a.add(3,vote_avg);
                    a.add(4,plot_synopsis);
                    a.add(5,movie_id);
                    a.add(6,backdrop_path);


                    Log.d("posters",posters[i]);
                }



            }catch (JSONException e) {
                e.printStackTrace();
            }


            return a;

        }



        @Override
        protected void onPostExecute(ArrayList list) {
            super.onPostExecute(list);
            posters = (String[]) list.get(0);
            title = (String[]) list.get(1);
            release_date = (String[]) list.get(2);
            vote_avg = (String[]) list.get(3);
            plot_synopsis = (String[]) list.get(4);
            movie_id = (String[]) list.get(5);
            backdrop_path = (String[]) list.get(6);

            /*Bundle extras = new Bundle();
            extras.putString("poster_url", posters[0]);
            extras.putString("title", title[0]);
            extras.putString("release_date", release_date[0]);
            extras.putString("vote_avg", vote_avg[0]);
            extras.putString("plot_synopsis", plot_synopsis[0]);
            extras.putString("movie_id", movie_id[0]);
            ((Callback)getActivity()).onItemSelected(extras);*/

            if (isAdded()){
                GridCustomAdapter adapter = new GridCustomAdapter(getActivity(), posters);
            GridView grid = (GridView) rootView.findViewById(R.id.gridView);
            grid.setAdapter(adapter);


            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Clicked", "Clicked");


                    //Intent i = new Intent(getActivity(), DetailActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("poster_url", posters[position]);
                    extras.putString("title", title[position]);
                    extras.putString("release_date", release_date[position]);
                    extras.putString("vote_avg", vote_avg[position]);
                    extras.putString("plot_synopsis", plot_synopsis[position]);
                    extras.putString("movie_id", movie_id[position]);
                    extras.putString("backdrop_path", backdrop_path[position]);
                    ((Callback)getActivity()).onItemSelected(extras);
                    /*i.putExtras(extras);

                    startActivity(i);*/

                }
            });


        }

                if(MainActivity.mTwoPane) {
                    Bundle extras = new Bundle();
                    extras.putString("poster_url", posters[0]);
                    extras.putString("title", title[0]);
                    extras.putString("release_date", release_date[0]);
                    extras.putString("vote_avg", vote_avg[0]);
                    extras.putString("plot_synopsis", plot_synopsis[0]);
                    extras.putString("movie_id", movie_id[0]);
                    extras.putString("backdrop_path", backdrop_path[0]);
                    ((Callback) getActivity()).onItemSelected(extras);
                }


        }


    }
}

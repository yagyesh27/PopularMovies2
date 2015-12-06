package com.myappsworld.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class DetailActivityFragment extends Fragment {

    View rootView;

    String movie_id;
    String poster_url ;
    String title ;
    String release_date ;
    String vote_avg;
    String plot_synopsis;
    String backdrop_path;

    String trailer[];
    String key[];
    String author[];
    String content[];

    static String JSONstringT;
    static String JSONstringR;

    Boolean is_review_avail = false;
    Boolean is_trailer_avail = false;
    Boolean is_favourite = false;

    SQLiteDatabase  db;

    public DetailActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle extras =  getArguments();
        if (extras != null){

            db = getContext().openOrCreateDatabase("movies.db", Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS FavouriteMovies(movie_id VARCHAR,poster_url VARCHAR,title VARCHAR,release_date VARCHAR,vote_avg VARCHAR,plot_synopsis VARCHAR,backdrop_path VARCHAR);");


        movie_id = extras.getString("movie_id");
        poster_url = extras.getString("poster_url");
            Toast.makeText(getActivity(),poster_url,Toast.LENGTH_LONG);
        title = extras.getString("title");
        release_date = extras.getString("release_date");
        vote_avg = extras.getString("vote_avg");
        plot_synopsis = extras.getString("plot_synopsis");
            backdrop_path = extras.getString("backdrop_path");
        //Log.d("Movie Id", movie_id);

        ImageView iv = (ImageView) rootView.findViewById(R.id.imageView);
        final ImageView ivfav = (ImageView) rootView.findViewById(R.id.favbutton);
            if(backdrop_path != null) {
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w780" + backdrop_path).into(iv);
            }else{
                Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w500" + poster_url).into(iv);
            }
        ((TextView) rootView.findViewById(R.id.title_content)).setText(title);
        ((TextView) rootView.findViewById(R.id.release_date_content)).setText(release_date);
        ((TextView) rootView.findViewById(R.id.vote_avg_content)).setText(vote_avg);
        ((TextView) rootView.findViewById(R.id.plot_synopsis_content)).setText(plot_synopsis);

        Fetchdata fetchobj = new Fetchdata();
        fetchobj.execute();

        Cursor resultSet = db.rawQuery("Select * from FavouriteMovies", null);
        resultSet.moveToFirst();
        int rowsleft = resultSet.getCount();
        while (rowsleft > 0) {
            if (resultSet.getString(0).equals(movie_id)) {
                is_favourite = true;
            }
            resultSet.moveToNext();
            rowsleft--;
            Log.d("db", "fav checked");
        }

        if (!is_favourite) {
            ivfav.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.favoff));
        } else {
            ivfav.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.favon));
        }

        ivfav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("fav", "fav");


                Log.d("db", "tblcreated");

                is_favourite = !is_favourite;
                if (is_favourite) {



                    db.execSQL("INSERT INTO FavouriteMovies VALUES(?, ?, ?, ?, ?, ?, ?)",new String[]{movie_id, poster_url, title, release_date, vote_avg, plot_synopsis, backdrop_path});
                    Log.d("db", "rowinserted");
                    ivfav.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.favon));
                } else {
                    db.delete("FavouriteMovies", "movie_id=?", new String[]{movie_id});
                    Log.d("db", "rowdeleted");
                    ivfav.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.favoff));
                }

                Cursor resultSet1 = db.rawQuery("Select * from FavouriteMovies", null);
                resultSet1.moveToFirst();
                String dbdata = "";
                int rowsleft = resultSet1.getCount();
                while (rowsleft > 0) {
                    dbdata += resultSet1.getString(0) + "-";
                    dbdata += resultSet1.getString(1) + "-";
                    dbdata += resultSet1.getString(2) + "-";
                    dbdata += resultSet1.getString(3) + "-";
                    dbdata += resultSet1.getString(4) + "-";
                    //dbdata += resultSet.getString(5) + "--------";
                    resultSet1.moveToNext();
                    rowsleft--;
                    Log.d("db", "fech");
                }

                Log.d("db", dbdata);

            }
        });


    }
        return rootView;

    }


    public class Fetchdata extends AsyncTask<String , Void , ArrayList> {


        protected ArrayList doInBackground(String[] params) {




            HttpURLConnection urlConnectionT = null;
            HttpURLConnection urlConnectionR = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String dataJsonStrT = null;
            String dataJsonStrR = null;

            //trailer fetch

            try {

                String baseurlT ="http://api.themoviedb.org/3/movie/" + movie_id +"/videos?";
                //String sort_param = params[0];

                Uri builturiT= Uri.parse(baseurlT).buildUpon()
                        .appendQueryParameter("api_key", getString(R.string.api_key))
                        .build();
                URL urlT = new URL(builturiT.toString());


                urlConnectionT = (HttpURLConnection) urlT.openConnection();
                urlConnectionT.setRequestMethod("GET");
                urlConnectionT.connect();


                InputStream inputStream = urlConnectionT.getInputStream();
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
                dataJsonStrT = buffer.toString();
                DetailActivityFragment.JSONstringT = dataJsonStrT;
                Log.d("JsondataT", dataJsonStrT);
                Log.d("JsondataT", DetailActivityFragment.JSONstringT);
            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error ", e);

                return null;
            } finally {
                if (urlConnectionT != null) {
                    urlConnectionT.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            //Reviews fetch

            try {

                String baseurlR ="http://api.themoviedb.org/3/movie/" + movie_id +"/reviews?";
                //String sort_param = params[0];

                Uri builturiR= Uri.parse(baseurlR).buildUpon()
                        .appendQueryParameter("api_key", getString(R.string.api_key))
                        .build();
                URL urlR = new URL(builturiR.toString());


                urlConnectionR = (HttpURLConnection) urlR.openConnection();
                urlConnectionR.setRequestMethod("GET");
                urlConnectionR.connect();


                InputStream inputStream = urlConnectionR.getInputStream();
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
                dataJsonStrR = buffer.toString();
                DetailActivityFragment.JSONstringR = dataJsonStrR;
                Log.d("JsondataR", dataJsonStrR);
                Log.d("JsondataR", DetailActivityFragment.JSONstringR);
            } catch (IOException e) {
                Log.e("MainActivityFragment", "Error ", e);

                return null;
            } finally {
                if (urlConnectionT != null) {
                    urlConnectionT.disconnect();
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

            //trailer set

            try {
                JSONObject movieJsonT = new JSONObject(JSONstringT);
                JSONArray movieT = movieJsonT.getJSONArray("results");

                if (movieT.length()>0){
                String[] trailer = new String[movieT.length()];
                String[] key = new String[movieT.length()];


                JSONObject obT;
                for(int i=0;i<movieT.length();i++) {
                    obT = movieT.getJSONObject(i);
                    trailer[i] = obT.getString("name");
                    key[i] = obT.getString("key");


                    a.add(0, trailer);
                    a.add(1, key);

                    is_trailer_avail = true;

                    Log.d("posters", trailer[i] + " - " + key[i]);
                }


                }




            }catch (JSONException e) {
                e.printStackTrace();
            }

            //review set
            Log.d("check","check1");
           try {
                JSONObject movieJsonR = new JSONObject(JSONstringR);
                JSONArray movieR = movieJsonR.getJSONArray("results");
                Log.d("check","check2");

                    if (movieR.length()>0){
                   String[] author = new String[movieR.length()];
                String[] content = new String[movieR.length()];
                        Log.d("check","check3");

                JSONObject obR;
                for(int i=0;i<movieR.length();i++){
                    obR = movieR.getJSONObject(i);
                    author[i] = obR.getString("author") ;
                    content[i] = obR.getString("content") ;


                    a.add(2,author);
                    a.add(3,content);

                    is_review_avail = true;

                    Log.d("auther",author[i] );
                }
                }



            }catch (JSONException e) {
                e.printStackTrace();
            }


            return a;

        }



        @Override
        protected void onPostExecute(ArrayList list) {
            super.onPostExecute(list);
            /*posters = (String[]) list.get(0);
            title = (String[]) list.get(1);
            release_date = (String[]) list.get(2);
            vote_avg = (String[]) list.get(3);
            plot_synopsis = (String[]) list.get(4);
            movie_id = (String[]) list.get(5);*/
            Log.d("check","check4");
            if(is_trailer_avail) {
                trailer = (String[]) list.get(0);
                key = (String[]) list.get(1);
            }
            if(is_review_avail) {
                author = (String[]) list.get(2);
                content = (String[]) list.get(3);
            }
            Log.d("check","check5");
            if (isAdded() && is_trailer_avail){

                ((TextView)rootView.findViewById(R.id.no_trailor_msg)).setText("");

                TrailersCustomAdapter adapter = new TrailersCustomAdapter(getActivity(), trailer, key);
                ListView trailerlist = (ListView) rootView.findViewById(R.id.trailer_list);
                trailerlist.setAdapter(adapter);

                ViewGroup.LayoutParams listparam = (ViewGroup.LayoutParams) trailerlist.getLayoutParams();
                listparam.height = (trailer.length)*120;//like int  200
                trailerlist.setLayoutParams(listparam);



                trailerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("Clicked", "Clicked");


                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + key[position]));
                        startActivity(i);

                    }
                });


            }

            String reviews_str = "";


            if(is_review_avail) {
                if (!author[0].equals(trailer[0])) {
                    for (int i = 0; i < author.length; i++) {
                        reviews_str += "_____________________<br><font  color='#28397e' ><b><u>" + author[i] + "</u></b> :- " + content[i] + "</font><br>";
                    }
                } else {
                    reviews_str = "No Reviews Available ";
                }
            }else{
                reviews_str = "No Reviews Available ";
            }

            //#6699ff
            TextView tvR = (TextView) rootView.findViewById(R.id.reviews_cont);
            tvR.setText(Html.fromHtml(reviews_str));




        }


    }

}

package com.example.Apps.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;

/**
 *
 */
public class DetailedActivityFragment extends Fragment implements View.OnClickListener {
    private Movie movie;
    private GetTrailer getTrailer;
    private MovieDBHelper DB;
    private  List<Trailers> data;
   //-----------------------------------
    private String id ,SortMethod ;
    private ListView list;
    private Button favourite , reviews ;
    private TextView title,vote,release,review ;
    private ImageView poster;
    private SharedPreferences SP;

    public DetailedActivityFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detailed, container, false);
        View header = inflater.inflate(R.layout.header, null, false);
        list = (ListView)rootView.findViewById(R.id.list_trailers);
        list.addHeaderView(header);
        reviews = (Button) rootView.findViewById(R.id.reviews);
        title = (TextView) rootView.findViewById(R.id.movie_title);
        vote = (TextView) rootView.findViewById(R.id.movie_vote_average);
        release = (TextView) rootView.findViewById(R.id.movie_release_date);
        review = (TextView) rootView.findViewById(R.id.movie_overview);
        poster = (ImageView) rootView.findViewById(R.id.movie_poster);
        favourite = (Button) rootView.findViewById(R.id.favor_id);
        UpdateData(getMovieObject());
        reviews.setOnClickListener(this);
        favourite.setOnClickListener(this);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(position-1).getKey())));
             }
         });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SortMethod = SP.getString(getString(R.string.sort_type), getString(R.string.pref_default_value));
        if(SortMethod.equals("favourite"))
            favourite.setText("Remove From Favourite");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.favor_id) {
            if(favourite.getText().toString().equals("Add to favorite")) {
                DB = new MovieDBHelper(getActivity());
                DB.insertMovie(movie);
                Toast.makeText(getActivity(), "Added to Favourite List", Toast.LENGTH_SHORT).show();
               }
            else {
                favourite.setText("Remove From Favourite");
                DB = new MovieDBHelper(getActivity());
                DB.deleteMovie(movie.getiD());

                Toast.makeText(getActivity(), "Removed from Favourite List", Toast.LENGTH_SHORT).show();
                movie.insertedToDB=false;
            }
        }
        else if (v.getId()==R.id.reviews){
         Intent intent = new Intent(getActivity(), Review.class);
         intent.putExtra("id", id);
         startActivity(intent);
        }
    }

    public Movie getMovieObject(){
        if(getArguments()!=null&&getArguments().getParcelable("movie")!=null) {
            movie = getArguments().getParcelable("movie");
        }
        return movie;
    }

    public void UpdateData(Movie movie){
        this.movie = movie;
        if(this.movie !=null) {
            Picasso.with(getContext()).load(this.movie.getMoviePoster()).into(poster);
            title.setText(this.movie.getTitle());
            vote.setText(this.movie.getVoteAverage());
            review.setText(this.movie.getPlotSynopsis());
            release.setText(this.movie.getRelease_data());
            id = this.movie.getiD();
            getTrailer = new GetTrailer();
            getTrailer.execute();
        }
  }
    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }


    class GetTrailer extends AsyncTask<String, Void, List<Trailers> > {
       private final String LOG_TAG = GetTrailer.class.getSimpleName();
       private ArrayAdapter<Trailers> Ad;

       public List<Trailers> getMovieTrailerFromJson(String MovieJsonStr)
               throws JSONException{
           data = new ArrayList<Trailers>();
           final String WEB_RESULT = "results";
           JSONObject initial = new JSONObject(MovieJsonStr);
           JSONArray moviesArray = initial.getJSONArray(WEB_RESULT);
           if(moviesArray==null){
               return null;
           }
           else{
               for(int i=0;i<moviesArray.length();i++){
                   JSONObject movieDetail = moviesArray.getJSONObject(i);
                   Trailers trailer = new Trailers();
                   trailer.setId(movieDetail.getString("id"));
                   trailer.setKey(movieDetail.getString("key"));
                   trailer.setName(movieDetail.getString("name"));
                   data.add(trailer);
               }
           }
           return data;
       }
       @Override
       protected List<Trailers> doInBackground(String... params) {

           HttpURLConnection urlConnection = null;
           BufferedReader reader = null;
           String MovieJsonStr = null;
           Uri builtUri;

           try {
               final String FETCH_MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
               final String API_KEY = "api_key";
               builtUri = Uri.parse(FETCH_MOVIE_BASE_URL).buildUpon()
                       .appendPath(id)
                       .appendPath("videos")
                       .appendQueryParameter(API_KEY, BuildConfig.Movie_App_API_KEY)
                       .build();
               URL url = new URL(builtUri.toString());
               urlConnection = (HttpURLConnection) url.openConnection();
               urlConnection.setRequestMethod("GET");
               urlConnection.connect();

               InputStream inputStream = urlConnection.getInputStream();
               StringBuffer buffer = new StringBuffer();

               if (inputStream == null) {
                   Log.d(LOG_TAG, "input stream is null");
                   return null;
               }
               reader = new BufferedReader(new InputStreamReader(inputStream));

               String line;
               while ((line = reader.readLine()) != null) {
                   buffer.append(line + "\n");
               }
               if (buffer.length() == 0){
                   return null;
               }
               MovieJsonStr = buffer.toString();
           }catch(IOException e) {
               Log.e(LOG_TAG, "Error ", e);
               return null;
           } finally {
               if (urlConnection != null) {
                   urlConnection.disconnect();
               }
               if (reader != null) {
                   try {
                       reader.close();
                   } catch (final IOException e) {
                       Log.e(LOG_TAG, "Error closing stream", e);
                   }
               }
           }
           try {
               return getMovieTrailerFromJson(MovieJsonStr);

           } catch (JSONException e) {
               e.printStackTrace();
           }
           return null;
       }
       @Override
       protected void onPostExecute(List<Trailers> result) {
           if (isNetworkAvailable(getContext())) {
               Ad = new ArrayAdapter<Trailers>(getActivity(), R.layout.trailer_list_item,R.id.trailers_text_view, result);
               list.setAdapter(Ad);
           }
           else {
               Ad = new ArrayAdapter<Trailers>(getActivity(),  R.layout.trailer_list_item,R.id.trailers_text_view, data);
               list.setAdapter(Ad);
           }
       }

   }


}
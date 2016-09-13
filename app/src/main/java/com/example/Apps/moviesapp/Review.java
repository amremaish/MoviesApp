package com.example.Apps.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

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


public class Review extends AppCompatActivity {
    private String id , FinishReviews;
    private TextView reviewTxt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        Intent intent = this.getIntent();
        id = intent.getStringExtra("id");
        reviewTxt = (TextView) findViewById(R.id.reviewview_id);
        GetReview fetchMovieTask = new GetReview();
        fetchMovieTask.execute();
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    class GetReview extends AsyncTask<String, Void, Integer> {
        private final String LOG_TAG = GetReview.class.getSimpleName();


        @Override
        protected void onPostExecute(Integer result) {
            if (FinishReviews == null || FinishReviews.equals("")) {
                if (isNetworkAvailable(getApplicationContext()))
                    FinishReviews = "No Reviews found, Try later";
                else FinishReviews = "Check connection";
            }
            reviewTxt.setText(FinishReviews);
        }

        @Override
        protected Integer doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String MovieJsonStr = null;
            Uri builtUri;

            try {
                final String FETCH_MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_KEY = "api_key";
                builtUri = Uri.parse(FETCH_MOVIE_BASE_URL).buildUpon()
                        .appendPath(id)
                        .appendPath("reviews")
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
                if (buffer.length() == 0) {
                    return null;
                }
                MovieJsonStr = buffer.toString();
            } catch (IOException e) {
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
                return getReviewFromJson(MovieJsonStr);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Integer getReviewFromJson(String MovieJsonStr)
                throws JSONException {
            ArrayList<String> arrRev = new ArrayList<>();
            JSONObject jsonRootObject = new JSONObject(MovieJsonStr);
            JSONArray jsonArray = jsonRootObject.optJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                arrRev.add("Written by: " + jsonObject.getString("author") + "\n" + jsonObject.getString("content"));
            }
            return parseResultrev(arrRev);
        }

    }

    private int parseResultrev(ArrayList<String> arr) {

        StringBuilder storage = new StringBuilder();
        for (int i = 0; i < arr.size(); i++) {
            storage.append(arr.get(i) + "\n\n-------------------------------------------------\n\n");
        }
        FinishReviews = storage.toString();
        return arr.size();
    }
}

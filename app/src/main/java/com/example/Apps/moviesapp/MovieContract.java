package com.example.Apps.moviesapp;

import android.provider.BaseColumns;

public class MovieContract {
    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";
        public static final String  MOVIE_TITLE = "title";
        public static final String  MOVIE_RELEASE ="year";
        public static final String  MOVIE_POSTER = "poster";
        public static final String  MOVIE_OVERVIEW ="overview";
        public static final String  MOVIE_VOTE ="vote";
    }
}

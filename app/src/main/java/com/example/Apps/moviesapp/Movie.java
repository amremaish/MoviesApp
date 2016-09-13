package com.example.Apps.moviesapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
   private String  title, release_data, moviePoster, voteAverage, plotSynopsis, iD;
    public boolean insertedToDB;

    public Movie(){
            insertedToDB=false;
      }

    protected Movie(Parcel in) {
        title = in.readString();
        release_data = in.readString();
        moviePoster = in.readString();
        voteAverage = in.readString();
        plotSynopsis = in.readString();
        iD = in.readString();
    }


    public void setTitle(String title){
        this.title=title;
    }
    public void setRelease_data(String release_data){
        if(release_data.length()>4) {
            release_data = release_data.substring(0, 4);
        }
        this.release_data =release_data;
    }
    public void setMoviePoster(String moviePoster){
        String MOVIES_BASE_URL= "http://image.tmdb.org/t/p/w185/";
        this.moviePoster= MOVIES_BASE_URL+moviePoster;
    }
    public void setiD(String iD){
        this.iD=iD;
    }
    public void setVoteAverage(String voteAverage){
        this.voteAverage=voteAverage;
    }
    public void setPlotSynopsis(String plotSynopsis){
        this.plotSynopsis= plotSynopsis;
    }

    public String getPlotSynopsis(){
        return plotSynopsis;
    }

    public String getRelease_data(){
        return release_data;
    }

    public String getiD(){
        return iD;
    }

    public String getTitle(){
        return title;
    }

    public String getMoviePoster(){
        return moviePoster;
    }
    public String getVoteAverage(){
        return voteAverage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(release_data);
        dest.writeString(moviePoster);
        dest.writeString(voteAverage);
        dest.writeString(plotSynopsis);
        dest.writeString(iD);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

}
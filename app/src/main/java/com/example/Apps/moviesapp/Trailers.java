package com.example.Apps.moviesapp;

public class Trailers {
   private  String id, key, name;

 public Trailers(){

 }
    public Trailers(String id, String key, String name) {
        this.id = id;
        this.key ="https://www.youtube.com/watch?v="+ key;
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setKey(String key){
        this.key="https://www.youtube.com/watch?v="+key;
    }

    public void setId(String id){
        this.id=id;
    }


    public String getId(){
        return id;
    }
    public void setName(String name){
        this.name=name;
    }

    public String getKey(){
        return key;
    }


    @Override
    public String toString() {
        return name;
    }
}

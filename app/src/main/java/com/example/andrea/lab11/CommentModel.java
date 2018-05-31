package com.example.andrea.lab11;

import java.util.Date;

public class CommentModel {

    private String text;
    private float rating;
    private int year;
    private int month;
    private int day;
    private String user;

    public CommentModel(){

    }

    public CommentModel(String user, float rating, String text, int year, int month, int day){
        this.text = text;
        this.rating = rating;
        this.user = user;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    //getters
    public String getText(){return text;}
    public float getRating(){return rating;}
    public int getYear(){return year;}
    public int getMonth(){return month;}
    public int getDay(){return day;}
    public String getUser(){return user;}

    //setters
    public void setText(String value){text = value;}
    public void setRating(float value){rating = value;}
    public void setYear(int value){year = value;}
    public void setMonth(int value){month = value;}
    public void setDay(int value){day = value;}
    public void setUser(String value){user = value;}

}

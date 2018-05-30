package com.example.andrea.lab11;

import java.util.Date;

public class CommentModel {

    private String text;
    private float rating;
    private Date date;
    private String user;

    public CommentModel(String user, float rating, String text, Date date){
        this.text = text;
        this.rating = rating;
        this.user = user;
        this.date = date;
    }
}

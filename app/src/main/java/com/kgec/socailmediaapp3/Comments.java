package com.kgec.socailmediaapp3;

public class Comments {

    private String Comment,Date,Time,username;

    public Comments() {}

    public Comments(String comment, String date, String time, String username) {
        Comment = comment;
        Date = date;
        Time = time;
        this.username = username;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}



package com.kgec.socailmediaapp3;

public class Messages {

    private String date,time,message,uid,type;

    public Messages() {}

    public Messages(String date, String time, String message, String uid, String type) {
        this.date = date;
        this.time = time;
        this.message = message;
        this.uid = uid;
        this.type = type;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

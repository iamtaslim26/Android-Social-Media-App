package com.kgec.socailmediaapp3;

public class Post {

    private String PostDescriptionKey,PostImage,ProfileImage,date,fullname,time,uid,description;

    public Post(){}

    public Post(String postDescriptionKey, String postImage, String profileImage, String date, String fullname, String time, String uid, String description) {
        PostDescriptionKey = postDescriptionKey;
        PostImage = postImage;
        ProfileImage = profileImage;
        this.date = date;
        this.fullname = fullname;
        this.time = time;
        this.uid = uid;
        this.description = description;
    }

    public String getPostDescriptionKey() {
        return PostDescriptionKey;
    }

    public void setPostDescriptionKey(String postDescriptionKey) {
        PostDescriptionKey = postDescriptionKey;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setPostImage(String postImage) {
        PostImage = postImage;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


package com.kgec.socailmediaapp3;

public class FindFriends {

    private String ProfileImage,username,fullname,status;

    public  FindFriends(){}

    public FindFriends(String profileImage, String username, String fullname, String status) {
        ProfileImage = profileImage;
        this.username = username;
        this.fullname = fullname;
        this.status = status;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

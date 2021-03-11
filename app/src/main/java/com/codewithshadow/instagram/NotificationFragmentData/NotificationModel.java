package com.codewithshadow.instagram.NotificationFragmentData;

public class NotificationModel {
    private String username;
    private String postimg;
    private String imgurl;

    public NotificationModel()
    {}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostimg() {
        return postimg;
    }

    public void setPostimg(String postimg) {
        this.postimg = postimg;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public NotificationModel(String username, String postimg, String imgurl, String time) {
        this.username = username;
        this.postimg = postimg;
        this.imgurl = imgurl;
        this.time=time;
    }
}

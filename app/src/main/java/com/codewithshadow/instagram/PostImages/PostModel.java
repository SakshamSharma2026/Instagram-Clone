package com.codewithshadow.instagram.PostImages;

public class PostModel {
    private String postimg;
    private String imgurl;
    private String location;

    public PostModel()
    {}

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostkey() {
        return postkey;
    }

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }

    private String postkey;
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    private String caption;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PostModel(String postimg, String imgurl, String username,String caption,String location,String postkey,String publisherid
    ,String time) {
        this.postimg = postimg;
        this.imgurl = imgurl;
        this.username = username;
        this.caption = caption;
        this.location=location;
        this.postkey=postkey;
        this.publisherid = publisherid;
        this.time = time;
    }


    public PostModel(String postimg)
    {
        this.postimg = postimg;
    }

    private String username;

    public String getPublisherid() {
        return publisherid;
    }

    public void setPublisherid(String publisherid) {
        this.publisherid = publisherid;
    }

    private String publisherid;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;
}

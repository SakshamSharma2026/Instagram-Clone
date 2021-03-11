package com.codewithshadow.instagram.FullScreenImages;

public class FullScreenModel {
    private String postimg;
    private String imgurl;
    private String location;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public FullScreenModel(String postimg, String imgurl, String location, String username) {
        this.postimg = postimg;
        this.imgurl = imgurl;
        this.location = location;
        this.username = username;
    }

    private String username;
}

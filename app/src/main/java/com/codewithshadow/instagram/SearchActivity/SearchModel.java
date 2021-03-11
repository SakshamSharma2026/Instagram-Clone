package com.codewithshadow.instagram.SearchActivity;

public class SearchModel {
    private String name;
    private String username;

    public SearchModel(String name, String username, String userid, String imgurl) {
        this.name = name;
        this.username = username;
        this.userid = userid;
        this.imgurl = imgurl;
    }

    public SearchModel()
    {}


    private String userid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    private String imgurl;

}

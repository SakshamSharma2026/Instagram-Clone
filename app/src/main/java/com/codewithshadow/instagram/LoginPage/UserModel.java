package com.codewithshadow.instagram.LoginPage;

public class UserModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }





    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    private String name;

    public UserModel()
    {}


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    private String userid;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    public UserModel(String name, String userid, String status, String username, String token, String imgurl) {
        this.name = name;
        this.userid = userid;
        this.status = status;
        this.username = username;
        this.token = token;
        this.imgurl = imgurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
    private String token;



    private String imgurl;
}

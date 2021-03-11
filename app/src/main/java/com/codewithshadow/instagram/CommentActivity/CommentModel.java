package com.codewithshadow.instagram.CommentActivity;

public class CommentModel {
    private String comment;
    private String username;
    private String imgurl;

    public CommentModel(String comment, String username, String imgurl, String time, String postimg, String key) {
        this.comment = comment;
        this.username = username;
        this.imgurl = imgurl;
        this.time = time;
        this.postimg = postimg;
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;



    public String getPostimg() {
        return postimg;
    }

    public void setPostimg(String postimg) {
        this.postimg = postimg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String postimg;


    public CommentModel()
    {}


    private String key;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }


}

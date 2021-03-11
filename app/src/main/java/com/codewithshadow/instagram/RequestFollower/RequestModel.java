package com.codewithshadow.instagram.RequestFollower;

public class RequestModel {
    private String sender;
    private String receiver;
    private String username;
    private String name;
    private String imgurl;

    public RequestModel()
    {}

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public RequestModel(String sender, String receiver, String username, String name, String imgurl) {
        this.sender = sender;
        this.receiver = receiver;
        this.username = username;
        this.name = name;
        this.imgurl = imgurl;
    }
}

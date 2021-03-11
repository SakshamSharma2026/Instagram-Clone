package com.codewithshadow.instagram.SendMessages.MessageUser;

public class MessageUserModel {
    private String sender;
    private String username;

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

    private String imgurl;

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

    private String receiver;

    public String getMsg() {
        return msg;
    }

    public MessageUserModel( String receiver, String msg,String username,String imgurl) {
        this.receiver = receiver;
        this.msg = msg;
        this.username = username;
        this.imgurl = imgurl;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }



    private String msg;
}

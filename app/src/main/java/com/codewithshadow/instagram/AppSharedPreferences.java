package com.codewithshadow.instagram;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {
    private SharedPreferences sharedPreference;

    private static final String flag="logout";

    private  SharedPreferences.Editor editor;


    private static final String username="username";
    private  static  final  String imgUrl ="imgUrl";
    private  static  final  String status ="status";
    private  static  final  String followersCount ="followersCount";
    private  static  final  String followingsCount ="FollowingsCount";
    private  static  final  String notificationImgUrl ="notificationImgUrl";

    public AppSharedPreferences(Context context) {
        String Pref_Name = "Login_ID";
        sharedPreference = context.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE);
        editor = sharedPreference.edit();
        editor.apply();
    }
    public  void setUsername(String username)
    {
        editor.putString(this.username,username);
        editor.apply();
    }
    public String getUserName() {
        return sharedPreference.getString(username,null);
    }

    public  void setImgUrl(String imgUrl)
    {
        editor.putString(this.imgUrl,imgUrl);
        editor.apply();
    }
    public String getImgUrl() {
        return sharedPreference.getString(imgUrl,null);
    }

    public  void setNotificationImgUrl(String notificationImgUrl)
    {
        editor.putString(this.notificationImgUrl,notificationImgUrl);
        editor.apply();
    }
    public String getNotificationImgUrl() {
        return sharedPreference.getString(notificationImgUrl,null);
    }

    public  void setStatus(String status)
    {
        editor.putString(this.status,status);
        editor.apply();
    }
    public String getStatus() {
        return sharedPreference.getString(status,null);
    }



    public void setFollowersCount(String followersCount){
        editor.putString(this.followersCount,followersCount);
        editor.apply();
    }

    public String getFollowersCount(){
        return sharedPreference.getString(followersCount,null);
    }


    public void setFollowingsCount(String followingsCount){
        editor.putString(this.followingsCount,followingsCount);
        editor.apply();
    }

    public String getFollowingsCount(){
        return sharedPreference.getString(followingsCount,null);
    }
}

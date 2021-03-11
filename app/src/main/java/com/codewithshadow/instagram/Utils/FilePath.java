package com.codewithshadow.instagram.Utils;

import android.os.Environment;

public class FilePath {

    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();
    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/Camera";
    public String FIREBASE_IMAGE_STORAGE = ROOT_DIR + "/WhatsApp/Media/WhatsApp_Images";


}

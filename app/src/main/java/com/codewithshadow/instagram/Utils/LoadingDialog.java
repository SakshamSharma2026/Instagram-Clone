package com.codewithshadow.instagram.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.codewithshadow.instagram.R;

public class LoadingDialog {
    Activity activity;
    AlertDialog dialog;
    TextView textView;

    public LoadingDialog(Activity myactivity)
    {
        activity=myactivity;
    }
    public void startloadingDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater inflater=activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progressdialog,null));
        builder.setCancelable(false);
        dialog=builder.create();
        dialog.show();
    }
    public void dismissDialog()
    {
        dialog.dismiss();
    }

    public  void textDiaglog(String message)
    {
        textView = dialog.findViewById(R.id.loading_text);
        textView.setText(message);
    }
}

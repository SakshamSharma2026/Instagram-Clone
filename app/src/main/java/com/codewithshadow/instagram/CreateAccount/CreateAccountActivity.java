package com.codewithshadow.instagram.CreateAccount;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Utils.JavaMailApi;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.List;
import java.util.Random;

public class CreateAccountActivity extends AppCompatActivity {
    EditText email,password;
    FrameLayout btnNext;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference ref;
    ProgressBar progressBar;
    Snackbar snackbar;
    Runnable mrunnable;
    Handler mhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");
        progressBar = findViewById(R.id.button_progress);
        email =findViewById(R.id.item_input_email);
        password = findViewById(R.id.item_input_password);
        btnNext =  findViewById(R.id.btn_next);
        btnNext.setEnabled(false);
        btnNext.setBackgroundColor(getResources().getColor(R.color.blue2));

                         //----------------------------------RunTime Permissions----------------------------------//
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET).withListener(
                new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        //Normal Functionl if user allow permission
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }
        ).check();


        email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

                if (s.toString().equals("")) {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundColor(getResources().getColor(R.color.blue2));

                } else if (!s.toString().isEmpty()){
                    btnNext.setBackgroundColor(getResources().getColor(R.color.blue));
                    btnNext.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }
        });
        PushDownAnim.setPushDownAnimTo(btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String e1 = email.getText().toString().trim();

                if(TextUtils.isEmpty(e1))
                {
                    email.setError("Enter Email");
                    email.requestFocus();
                }
                else
                {
                    createAccount(e1);
                }
                
                
            }
        });

    }

    private void createAccount(String e1) {
        progressBar.setVisibility(View.VISIBLE);
        btnNext.setEnabled(false);
        btnNext.setBackgroundColor(getResources().getColor(R.color.lightblue));
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            mrunnable = new Runnable() {
                @Override
                public void run() {
                    btnNext.setEnabled(false);
                    progressBar.setVisibility(View.GONE);
                    Random rnd = new Random();
                    int number = rnd.nextInt(999999);
                    String uniqueKey = String.format("%06d",number);
                    String emailsender = e1;
                    String message = String.valueOf(Html.fromHtml(uniqueKey  + " is your instagram code." ));
                    String subject = "Confirmation Code";
                    JavaMailApi javaMailApi = new JavaMailApi(CreateAccountActivity.this,emailsender,subject,message);
                    javaMailApi.execute();
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(CreateAccountActivity.this, ConfirmationCodeActivity.class);
                    intent.putExtra("email",e1);
                    intent.putExtra("uniquekey",uniqueKey);
                    startActivity(intent);
                    finish();
                }
            };
            mhandler=new Handler();
            mhandler.postDelayed(mrunnable,5000);
            connected = true;
        }
        else {
            View view =  findViewById(R.id.layout);
            snackbar = Snackbar.make(view,"No internet connection.",Snackbar.LENGTH_INDEFINITE);
            snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
            snackbar.setBackgroundTint(getResources().getColor(R.color.black));
            snackbar.setTextColor(Color.WHITE);
            snackbar.setDuration(5000);
            snackbar.show();
            connected = false;
        }





    }

}



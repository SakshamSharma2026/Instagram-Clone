package com.codewithshadow.instagram.CreateAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codewithshadow.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.HashMap;
import java.util.Map;

public class Name_UsernameActivity extends AppCompatActivity {
    EditText username,name,password;
    FrameLayout btnNext;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference ref;
    String email;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name__username);
        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        name =findViewById(R.id.item_input_name);
        username = findViewById(R.id.item_input_username);
        password = findViewById(R.id.item_input_password);
        progressBar = findViewById(R.id.button_progress);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        btnNext =  findViewById(R.id.btn_next);
        btnNext.setEnabled(false);
        btnNext.setBackgroundColor(getResources().getColor(R.color.lightblue));
        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

                if (s.toString().equals("")) {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundColor(Color.GRAY);

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
                final String n1 = name.getText().toString().trim();
                final String u1 = username.getText().toString().trim();
                final String p1 = password.getText().toString().trim();

                if(TextUtils.isEmpty(n1))
                {
                    name.setError("Enter Name");
                    name.requestFocus();
                }
                else if(TextUtils.isEmpty(u1))
                {
                    username.setError("Enter Username");
                    username.requestFocus();
                }
                else if(TextUtils.isEmpty(p1))
                {
                    password.setError("Enter Password");
                    password.requestFocus();
                }
                else
                {
                    addAccountDetails(n1,u1,p1,email);
                }


            }
        });

    }

    private void addAccountDetails(String n1, String u1,String p1,String e1) {
        progressBar.setVisibility(View.VISIBLE);
        btnNext.setEnabled(false);
        btnNext.setBackgroundColor(getResources().getColor(R.color.lightblue));


        auth.createUserWithEmailAndPassword(e1,p1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Map<String,Object> map = new HashMap<>();
                            map.put("email",e1);
                            ref.child(auth.getUid()).child("Info").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(Name_UsernameActivity.this,UploadProfilePicActivity.class);
                                    intent.putExtra("name",n1);
                                    intent.putExtra("username",u1);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Name_UsernameActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                btnNext.setEnabled(true);
                btnNext.setBackgroundColor(getResources().getColor(R.color.blue));
            }
        });


    }
}
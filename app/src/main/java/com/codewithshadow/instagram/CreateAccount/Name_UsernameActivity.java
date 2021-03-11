package com.codewithshadow.instagram.CreateAccount;

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

import com.codewithshadow.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thekhaeng.pushdownanim.PushDownAnim;

public class Name_UsernameActivity extends AppCompatActivity {
    EditText username,name;
    FrameLayout btnNext;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference ref;

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
        btnNext =  findViewById(R.id.btn_next);
        btnNext.setEnabled(false);
        btnNext.setBackgroundColor(Color.GRAY);
        username.addTextChangedListener(new TextWatcher() {

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
                else
                {
                    addAccountDetails(n1,u1);
                }


            }
        });

    }

    private void addAccountDetails(String n1, String u1) {
        Intent intent = new Intent(Name_UsernameActivity.this, UploadProfilePicActivity.class);
        intent.putExtra("name",n1);
        intent.putExtra("username",u1);
        startActivity(intent);
        finish();


//        Map<String, Object> map = new HashMap<>();
//        map.put("name",n1);
//        map.put("username",u1);
//        ref.child(user.getUid()).child("Info").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//            }
//        });
    }
}
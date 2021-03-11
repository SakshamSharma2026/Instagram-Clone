package com.codewithshadow.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codewithshadow.instagram.LoginPage.LoginScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DrawerActivity extends AppCompatActivity {
    TextView username;
    AppSharedPreferences appSharedPreferences;
    LinearLayout LogoutLayout;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        appSharedPreferences = new AppSharedPreferences(this);
        username = findViewById(R.id.username);
        username.setText(appSharedPreferences.getUserName());
        LogoutLayout = findViewById(R.id.logout_layout);
        LogoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("Users").child(user.getUid()).child("Info").child("token").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(DrawerActivity.this,"Logged Out ",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(DrawerActivity.this, LoginScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
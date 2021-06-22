package com.codewithshadow.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codewithshadow.instagram.Fragments.HomeFragment;
import com.codewithshadow.instagram.Fragments.NotificationFragment;
import com.codewithshadow.instagram.Fragments.ProfileFragment;
import com.codewithshadow.instagram.Fragments.SearchFragment;
import com.codewithshadow.instagram.LoginPage.UserModel;
import com.codewithshadow.instagram.PostImages.PostActivity;
import com.codewithshadow.instagram.Utils.AppSharedPreferences;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;
    AppSharedPreferences appSharedPreferences;
    DatabaseReference userref;
    FirebaseUser user;
    UserModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        appSharedPreferences = new AppSharedPreferences(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        bottomNavigationView = findViewById(R.id.bottom_navigationbar);
        bottomNavigationView.setItemIconTintList(null);


        userref.child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                model = snapshot.getValue(UserModel.class);
                appSharedPreferences.setUsername(model.getUsername());
                appSharedPreferences.setImgUrl(model.getImgurl());
                Glide.with(getApplicationContext()).asBitmap()
                        .load(model.getImgurl()).apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                Drawable profileImage = new BitmapDrawable(resource);
                                bottomNavigationView.getMenu().getItem(4).setIcon(profileImage);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }

                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationSelectedListenter);
        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new HomeFragment()).commit();


    }




    private BottomNavigationView.OnNavigationItemSelectedListener navigationSelectedListenter = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navhome:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.navsearch:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.navuplod:
                    selectedFragment = null;
                    startActivity(new Intent(Dashboard.this, PostActivity.class));
                    break;
                case R.id.navlike:
                    selectedFragment = new NotificationFragment();
                    break;
                case R.id.navprofile:
//                    SharedPreferences sharedPreferences;
//                    SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
//                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    editor.apply();
                    selectedFragment = new ProfileFragment();
                    break;

            }

            if (selectedFragment!=null)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,selectedFragment).commit();
            }
            return true;
        }
    };

    @Override
    public void onBackPressed() {

        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigationbar);
        if (mBottomNavigationView.getSelectedItemId() == R.id.navhome)
        {
            super.onBackPressed();
            finish();
        }
        else
        {
            mBottomNavigationView.setSelectedItemId(R.id.navhome);
        }
    }




}
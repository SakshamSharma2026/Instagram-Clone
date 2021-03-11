package com.codewithshadow.instagram.FullScreenImages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.codewithshadow.instagram.PostImages.PostAdapter;
import com.codewithshadow.instagram.PostImages.PostModel;
import com.codewithshadow.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FullScreenImageActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<PostModel> list;
    LinearLayoutManager manager ;
    PostAdapter adapter;
    DatabaseReference ref;
    FirebaseUser user;
    String userid;
    ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        recyclerView = findViewById(R.id.fullscreen_view);
        user  = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");


//        ref.child("Users").child(user.getUid()).child("Info").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snap) {
//                username = snap.child("username").getValue(String.class);
//                userimg = snap.child("imgurl").getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



        ref.child("AllPosts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                PostModel model = null;
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    model = snap.child("Info").getValue(PostModel.class);
                    if (model.getPublisherid().equals(userid))
                    {
                        list.add(model);
                    }
                    Collections.reverse(list);

                }
                adapter = new PostAdapter(FullScreenImageActivity.this,list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
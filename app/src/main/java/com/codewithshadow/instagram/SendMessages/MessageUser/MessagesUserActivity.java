package com.codewithshadow.instagram.SendMessages.MessageUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.codewithshadow.instagram.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessagesUserActivity extends AppCompatActivity {
    DatabaseReference ref;
    FirebaseUser user;
    List<MessageUserModel> list;
    MessageUserAdaper adapter;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    private List<String> followingList;
    private List<String> followList;
    ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_user);
        recyclerView = findViewById(R.id.messageuser_recycler);

        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");
        list = new ArrayList<>();
        manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        checkFollowers();

    }

    private void checkFollowers() {
        followList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user.getUid()).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    followList.add(snapshot1.getKey());
                }
                checkFollowing();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(user.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                    followingList.add(snapshot1.getKey());
                    Collections.reverse(followingList);
                }


                Read_User_Messages(user.getUid());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void Read_User_Messages(String myid) {

        for(String id : followingList)
        {
            ref.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snap) {
                    list.clear();
                    MessageUserModel model = null;
                    String receiver = snap.getKey();
                    String username = snap.child("Info").child("username").getValue(String.class);
                    String imgurl = snap.child("Info").child("imgurl").getValue(String.class);
                    String msg = snap.child("Msg").child("msg").getValue(String.class);

                    model = new MessageUserModel(receiver, msg, username, imgurl);
                    list.add(model);

                    adapter = new MessageUserAdaper(MessagesUserActivity.this, list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        }
    }

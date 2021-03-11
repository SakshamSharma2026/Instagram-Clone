package com.codewithshadow.instagram.FollowUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.HighLightStory.HighlightAdapter;
import com.codewithshadow.instagram.PostImages.PostAdapter;
import com.codewithshadow.instagram.PostImages.PostModel;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.SendMessages.SendMessageActivity;
import com.codewithshadow.instagram.Story.StoryActivity;
import com.codewithshadow.instagram.Story.StoryModel;
import com.codewithshadow.instagram.newAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FollowUserActivity extends AppCompatActivity {
    ImageView profileimg;
    DatabaseReference reference,storyref;
    FirebaseUser user;
    LinearLayout follow_layout,following_layout,user_layout;
    TextView fullname,bio,website,idusername,textpost,username,textfollowers,textfollowing;
    String stringusername,stringfullname,stringimgurl,stringuseid;
    ValueEventListener mDblistener;
    BottomSheetDialog bottomSheetDialog;
    RelativeLayout btnextrafeatures;
    RelativeLayout privatelayout;
    RecyclerView recyclerView,highlight_recycler;
    newAdapter adapter;
    RelativeLayout btn_message;
    RelativeLayout follow_btn;
    TextView textView;
    LinearLayoutManager manager2;
    HighlightAdapter storyAdapter;
    List<StoryModel> storyModelList;
    List<PostModel> list;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_user);
        Intent intent = getIntent();
        stringfullname = intent.getStringExtra("fullname");
        stringusername = intent.getStringExtra("username");
        stringimgurl = intent.getStringExtra("imgurl");
        stringuseid= intent.getStringExtra("userid");
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        profileimg = findViewById(R.id.button_image);
        fullname = findViewById(R.id.item_text_1);
        bio = findViewById(R.id.item_text_3);
        website = findViewById(R.id.item_text_4);
        idusername = findViewById(R.id.idusername);
        textpost = findViewById(R.id.item_1_value);
        username = findViewById(R.id.username);
        btn_message = findViewById(R.id.messageitem_button_1_click_parent);
        follow_btn = findViewById(R.id.item_button_1_click_parent);
        textView = findViewById(R.id.item_button_1_text);
        follow_layout = findViewById(R.id.followlayout);
        following_layout = findViewById(R.id.followinglayout);
        privatelayout = findViewById(R.id.privatelayout);
        user_layout = findViewById(R.id.userlayout);
        recyclerView = findViewById(R.id.grid_view);
        highlight_recycler = findViewById(R.id.highlights_view);
        btnextrafeatures = findViewById(R.id.followingitem_button_1_click_parent);
        textfollowing = findViewById(R.id.item_3_value);
        textfollowers = findViewById(R.id.item_2_value);

        Glide.with(getApplicationContext()).load(stringimgurl).into(profileimg);
        fullname.setText(stringfullname);
        username.setText(stringusername);


        mDblistener = reference.child("Users").child(stringuseid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String stringbio = snapshot.child("Info").child("bio").getValue(String.class);
                bio.setText(stringbio);
                int retrivepost = (int)snapshot.child("Posts").getChildrenCount();
                textpost.setText(retrivepost+"");
                int retrivefollowers = (int)snapshot.child("Followers").getChildrenCount();
                textfollowers.setText(retrivefollowers+"");
                int retrivefollowing = (int)snapshot.child("Following").getChildrenCount();
                textfollowing.setText(retrivefollowing+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(FollowUserActivity.this, SendMessageActivity.class);
                intent1.putExtra("imgurl",stringimgurl);
                intent1.putExtra("username",stringusername);
                intent1.putExtra("userid",stringuseid);
                startActivity(intent1);
            }
        });


        checkFollower();



        reference.child("Users").child(stringuseid).child("Request").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(user.getUid()).exists())
                {
                    follow_btn.setBackground(getResources().getDrawable(R.drawable.uigitdev_elements_input_field_border));
                    textView.setText("Requested");
                    textView.setTextColor(Color.BLACK);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                follow_btn.setBackground(getResources().getDrawable(R.drawable.uigitdev_elements_input_field_border));
                textView.setText("Requested");
                textView.setTextColor(Color.BLACK);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(user.getUid(),"Requested");

                reference.child("Users").child(stringuseid).child("Request").child(user.getUid()).setValue("Requested").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
            }
        });



        btnextrafeatures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog=new BottomSheetDialog(FollowUserActivity.this,R.style.BottomSheetDialogStyle);
                View bottomsheetview = LayoutInflater.from(FollowUserActivity.this).inflate(R.layout.followingextra,(LinearLayout)findViewById(R.id.rll));
                bottomSheetDialog.setContentView(bottomsheetview);
                bottomsheetview.setBackgroundResource(R.drawable.curvedbottomsheet);
                RelativeLayout btn_unfollow = bottomsheetview.findViewById(R.id.btn_unfollow);
                btn_unfollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Unfollowuser(stringuseid);
                    }
                });
                try {
                    bottomSheetDialog.show();
                }
                catch (WindowManager.BadTokenException e) {
                    //use a log message
                }
            }
        });
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(manager);


        reference.child("AllPosts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                PostModel model = null;

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    model = dataSnapshot.child("Info").getValue(PostModel.class);
                    if (model.getPublisherid().equals(stringuseid))
                    {
                        list.add(model);
                    }
                }

                adapter=new newAdapter(FollowUserActivity.this,list);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        storyref = FirebaseDatabase.getInstance().getReference().child("Story");

        storyModelList = new ArrayList<>();
        manager2 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        highlight_recycler.setHasFixedSize(true);
        highlight_recycler.setLayoutManager(manager2);

        readStory();


    }

    private void checkFollower() {

        reference.child("Users").child(user.getUid()).child("Following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snap) {
                if (snap.child(stringuseid).exists())
                {
                    following_layout.setVisibility(View.VISIBLE);
                    follow_layout.setVisibility(View.GONE);
                    privatelayout.setVisibility(View.GONE);
                    user_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    follow_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Unfollowuser(String stringuseid) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        databaseReference.child("Following").child(stringuseid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                bottomSheetDialog.dismiss();
                following_layout.setVisibility(View.GONE);
                follow_layout.setVisibility(View.VISIBLE);
                privatelayout.setVisibility(View.VISIBLE);
                user_layout.setVisibility(View.GONE);
            }
        });
    }


    private void readStory()
    {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(stringuseid).child("HighLights");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyModelList.clear();

                StoryModel storyModel = null;
                for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                    storyModel = snapshot2.getValue(StoryModel.class);
                    storyModelList.add(storyModel);
                }

                storyAdapter = new HighlightAdapter(FollowUserActivity.this,storyModelList);
                highlight_recycler.setAdapter(storyAdapter);
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
            bottomSheetDialog= null;
        }
    }
}
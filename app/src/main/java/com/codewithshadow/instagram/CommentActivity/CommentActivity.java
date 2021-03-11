package com.codewithshadow.instagram.CommentActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codewithshadow.instagram.AppSharedPreferences;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Utils.UniversalImageLoderClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {
    DatabaseReference databaseReference,commentref;
    FirebaseUser user;
    CircleImageView img;
    RecyclerView recyclerView;
    String postkey,userkey,username,postimg;
    TextView postcomment;
    EditText commentText;
    List<CommentModel> list;
    CommentAdapter adapter;
    LinearLayoutManager manager;
    AppSharedPreferences appSharedPreferences;
    ImageView backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        appSharedPreferences = new AppSharedPreferences(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        img = findViewById(R.id.button_image);
        recyclerView = findViewById(R.id.commentsrecycler);
        postcomment = findViewById(R.id.postcomment);
        commentText = findViewById(R.id.textcomment);
        list = new ArrayList<>();
        manager = new LinearLayoutManager(this);
        adapter = new CommentAdapter(this,list);
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        Intent intent = getIntent();
        userkey = intent.getStringExtra("userkey");
        postkey = intent.getStringExtra("postkey");
        postimg = intent.getStringExtra("postimg");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postkey);
        commentref = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postkey);

        commentref.child("Comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                CommentModel model = null;
                for(DataSnapshot snap: snapshot.getChildren())
                {
                        model = snap.getValue(CommentModel.class);
                        list.add(model);
                        recyclerView.setAdapter(adapter);


                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        UniversalImageLoderClass.setImage(appSharedPreferences.getImgUrl(),img,null);

        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals(""))
                {
                    postcomment.setTextColor(getResources().getColor(R.color.blue));
                    postcomment.setEnabled(false);

                }
                else
                {
                    postcomment.setTextColor(getResources().getColor(R.color.gradient_start_color));
                    postcomment.setEnabled(true);


                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        postcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String c1 = commentText.getText().toString().trim();
                if (TextUtils.isEmpty(c1))
                {
                    commentText.setError("Write comment");
                    commentText.requestFocus();
                }
                else
                {
                        UploadComment(c1);
                    }

            }
        });




    }

    private void UploadComment(String c1) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = new Date();
        Map<String, Object> map = new HashMap<>();
        String key = databaseReference.child("Comments").push().getKey();
        map.put("comment",c1);
        map.put("username",appSharedPreferences.getUserName());
        map.put("imgurl",appSharedPreferences.getImgUrl());
        map.put("postimg",postimg);
        map.put("time",sdf.format(date));
        map.put("key",key);
        databaseReference.child("Comments").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                commentText.getText().clear();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }
}
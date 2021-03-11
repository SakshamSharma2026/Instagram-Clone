package com.codewithshadow.instagram.HighLightStory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Story.StoryActivity;
import com.codewithshadow.instagram.Story.StoryModel;
import com.codewithshadow.instagram.Utils.UniversalImageLoderClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class OpenHighLightActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter = 0;
    long presstime = 0L;
    long limit = 500L;
    StoriesProgressView storiesProgressView;
    ImageView story_photo;
    TextView story_username;
    ProgressBar progressBar;
    FirebaseUser user;
    CircleImageView imageView;

    ImageView deletebtn;
    TextView seentext;
    List<String> images;
    List<String> storyids;
    String userid;
    RelativeLayout rrll;



    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    presstime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - presstime;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_high_light);
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        storiesProgressView  = findViewById(R.id.stories);

        user = FirebaseAuth.getInstance().getCurrentUser();
        imageView  = findViewById(R.id.img);
        story_photo = findViewById(R.id.storyimage);
        story_username = findViewById(R.id.username);
        progressBar = findViewById(R.id.progress_bar);
        deletebtn = findViewById(R.id.deletestory);
        seentext = findViewById(R.id.seentext);
        rrll = findViewById(R.id.rrll);
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");

        if (userid.equals(user.getUid()))
        {
            rrll.setVisibility(View.VISIBLE);
        }



        userInfo();
        getStories(userid);

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);


        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);


        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(userid).child("HighLights").child(storyids.get(counter));

                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(OpenHighLightActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onNext() {
        UniversalImageLoderClass.setImage(images.get(++counter),story_photo,progressBar);

//        Glide.with(getApplicationContext()).load(images.get(++counter)).into(story_photo);

    }

    @Override
    public void onPrev() {
        if ((counter-1)<0)
            return;
        UniversalImageLoderClass.setImage(images.get(--counter),story_photo,progressBar);


//        Glide.with(getApplicationContext()).load(images.get(--counter)).into(story_photo);

    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }


    private void userInfo()
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid)
                .child("Info");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);
                String img = snapshot.child("imgurl").getValue(String.class);
                UniversalImageLoderClass.setImage(img,imageView,null);

//                Glide.with(getApplicationContext()).load(img).into(imageView);
                story_username.setText(username);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStories(String userid)
    {
        images = new ArrayList<>();
        storyids = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("HighLights");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                images.clear();
                storyids.clear();
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    StoryModel model = snap.getValue(StoryModel.class);
                        images.add(model.getStoryimg());
                        storyids.add(model.getStoryid());

                }

                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(OpenHighLightActivity.this);
                storiesProgressView.startStories(counter);


                UniversalImageLoderClass.setImage(images.get(counter),story_photo,progressBar);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
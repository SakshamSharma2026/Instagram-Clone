package com.codewithshadow.instagram.Story;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.FollowUser.FollowUserActivity;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Utils.UniversalImageLoderClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tapadoo.alerter.Alerter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    int counter = 0;
    long presstime = 0L;
    long limit = 500L;
    StoriesProgressView storiesProgressView;
    ImageView story_photo,btnhighlight;
    TextView story_username,timetxt;
    ProgressBar progressBar;
    FirebaseUser user;


    RelativeLayout rrll;
    ImageView deletebtn;
    TextView seentext;
    BottomSheetDialog bottomSheetDialog;

    List<String>images;
    List<String> storyids;

    String userid;
    CircleImageView imageView,highlight_image;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;


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
        setContentView(R.layout.activity_story);
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
        rrll = findViewById(R.id.rrll);
        timetxt = findViewById(R.id.time);
        deletebtn = findViewById(R.id.deletestory);
        seentext = findViewById(R.id.seentext);
        btnhighlight = findViewById(R.id.highlightstory);

        btnhighlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheetDialog=new BottomSheetDialog(StoryActivity.this,R.style.BottomSheetDialogStyle);
                View bottomsheetview = LayoutInflater.from(StoryActivity.this).inflate(R.layout.highlight_bottomsheet,(RelativeLayout)findViewById(R.id.rll));
                bottomSheetDialog.setContentView(bottomsheetview);
                highlight_image = bottomsheetview.findViewById(R.id.button_image);
                TextView btnAdd = bottomsheetview.findViewById(R.id.btnAdd);
                EditText btnEdit = bottomsheetview.findViewById(R.id.addtext);
                UniversalImageLoderClass.setImage(images.get(counter),highlight_image,progressBar);

                bottomsheetview.setBackgroundResource(R.drawable.curvedbottomsheet);
                try {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                    bottomSheetDialog.show();
                    storiesProgressView.pause();
                }
                catch (WindowManager.BadTokenException e) {
                    //use a log message
                }

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alerter.create(StoryActivity.this)
                        .setTitle("Added to " + btnEdit.getText().toString().trim())
                                .setBackgroundColorRes(R.color.white)
                                .setTitleAppearance(R.style.AlertTextAppearance_Tittle)
                                .setIconColorFilter(0) // Optional - Removes white tint
                                .setIcon(R.drawable.usericon)
                                .show();
                        String myid  = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(myid).child("HighLights").child(storyids.get(counter));

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("storyimg",images.get(counter));
                        hashMap.put("storyid",storyids.get(counter));
                        hashMap.put("userId",user.getUid());
                        hashMap.put("text",btnEdit.getText().toString().trim());
                        databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                bottomSheetDialog.dismiss();
                                storiesProgressView.resume();
                            }
                        });
                    }
                });
            }
        });


        rrll.setVisibility(View.GONE);

        userid = getIntent().getStringExtra("userid");
        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
        {
            rrll.setVisibility(View.VISIBLE);
        }


        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Story")
                        .child(userid).child(storyids.get(counter));

                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(StoryActivity.this,"Deleted",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });

        userInfo(userid);
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
    }

    @Override
    public void onNext() {
        UniversalImageLoderClass.setImage(images.get(++counter),story_photo,progressBar);

        addView(storyids.get(counter));
        seenNumber(storyids.get(counter));
//        Glide.with(getApplicationContext()).load(images.get(++counter)).into(story_photo);

    }

    @Override
    public void onPrev() {
        if ((counter-1)<0)
            return;
        UniversalImageLoderClass.setImage(images.get(--counter),story_photo,progressBar);
        seenNumber(storyids.get(counter));

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

    private void getStories(String userid)
    {
        images = new ArrayList<>();
        storyids = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Story").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                images.clear();
                storyids.clear();
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    StoryModel model = snap.getValue(StoryModel.class);
                    long timecurrent = System.currentTimeMillis();

                    if (timecurrent > model.getTimestart() && timecurrent < model.getTimeend())
                    {
                        images.add(model.getStoryimg());
                        storyids.add(model.getStoryid());
                        covertTimeToText(model.getTimeupload(),timetxt);
                    }
                }

                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);


                UniversalImageLoderClass.setImage(images.get(counter),story_photo,null);

                addView(storyids.get(counter));
                seenNumber(storyids.get(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void userInfo(String userid)
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


    private void addView(String storyid)
    {
        if (!userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            FirebaseDatabase.getInstance().getReference().child("Story").child(userid).child(storyid)
                    .child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
        }
    }

    private void seenNumber(String storyid)
    {
        DatabaseReference rr = FirebaseDatabase.getInstance().getReference().child("Story").child(userid).child(storyid)
                .child("views");
        rr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                seentext.setText("Seen by "+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public String covertTimeToText(String dataDate,TextView timetxt) {

        String convTime = null;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            if (second < 60) {
                convTime = second + "s " ;
            } else if (minute < 60) {
                convTime = minute + "m ";
            } else if (hour < 24) {
                convTime = hour + "h ";
            }
            timetxt.setText( convTime);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convTime;
    }
}
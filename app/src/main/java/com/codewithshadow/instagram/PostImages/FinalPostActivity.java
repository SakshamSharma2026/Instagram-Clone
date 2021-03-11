package com.codewithshadow.instagram.PostImages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.AppSharedPreferences;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Utils.FilePath;
import com.codewithshadow.instagram.Utils.ImageManager;
import com.codewithshadow.instagram.Dashboard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FinalPostActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    DatabaseReference ref,alluserref;
    FirebaseUser user;
    ImageView continuee,img,backbtn;
    String userkey,userid,userimg,finalimageUrl;
    String postimgurl;
    EditText caption,location;
    AppSharedPreferences appSharedPreferences;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_post);
        appSharedPreferences = new AppSharedPreferences(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");
        alluserref = FirebaseDatabase.getInstance().getReference().child("AllPosts");
        mStorageRef = FirebaseStorage.getInstance().getReference("MainImage");
        img = findViewById(R.id.img);
        caption = findViewById(R.id.textcaption);
        location = findViewById(R.id.textlocation);
        continuee = findViewById(R.id.continuee);
        Intent intent = getIntent();
        postimgurl = intent.getStringExtra("mSelectedImage");
        backbtn = findViewById(R.id.close);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Glide.with(getApplicationContext()).load(postimgurl).into(img);

        continuee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String c1 = caption.getText().toString();
                final String l1 = location.getText().toString();

                if (TextUtils.isEmpty(c1))
                {
                    caption.setError("Enter Caption");
                    caption.requestFocus();
                }
                else if (TextUtils.isEmpty(l1))
                {
                    location.setError("Enter Location");
                    location.requestFocus();
                }

                else
                {
                    Toast.makeText(FinalPostActivity.this,"Post Uploading... ",Toast.LENGTH_LONG).show();
                    uploadData(c1,l1,postimgurl,FirebaseAuth.getInstance().getCurrentUser());
                }

            }
        });
    }



    private void uploadData(final String c1,final String l1,String resultUri, final FirebaseUser currectuser) {
        if (resultUri != null) {
            FilePath filePath = new FilePath();
            final StorageReference reference = mStorageRef.child(filePath.FIREBASE_IMAGE_STORAGE + "/" + user.getUid() + "/photo" + System.currentTimeMillis() );

            Bitmap bmp = ImageManager.getBitmap(resultUri);
            byte[] bytes = ImageManager.getBytesFromBitmapImage(bmp,100);

            reference.putBytes(bytes)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    finalimageUrl=uri.toString();
                                    String imageUrl=uri.toString();
                                    String key = ref.child("Posts").push().getKey();
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                                    Date date = new Date();
                                    Map<String, Object> map = new HashMap<>();
                                    map.put(key,"true");
//                                    db.collection("Users").document(user.getUid()).collection("Posts").document(key).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                        }
//                                    });


//                                    ref.child(user.getUid()).child("Posts").child(key).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                        }
//                                    });

                                    Map<String, Object> map2 = new HashMap<>();
                                    map2.put("postimg",imageUrl);
                                    map2.put("imgurl",appSharedPreferences.getImgUrl());
                                    map2.put("username",appSharedPreferences.getUserName());
                                    map2.put("caption",c1);
                                    map2.put("location",l1);
                                    map2.put("postkey",key);
                                    map2.put("publisherid",user.getUid());
                                    map2.put("time",sdf.format(date));
//
//                                    db.collection("AllPosts").document(key).set(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            Intent intent = new Intent(FinalPostActivity.this, Dashboard.class);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
//                                            startActivity(intent);
//                                        }
//                                    });

                                    alluserref.child(key).child("Info").setValue(map2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(FinalPostActivity.this, Dashboard.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                                            startActivity(intent);
                                        }
                                    });

                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FinalPostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }
}
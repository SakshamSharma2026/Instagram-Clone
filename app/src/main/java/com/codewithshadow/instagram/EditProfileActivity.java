package com.codewithshadow.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.SendMessages.SendMessageActivity;
import com.codewithshadow.instagram.Utils.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    ImageView profileimg;
    DatabaseReference ref;
    FirebaseUser user;
    EditText fullname,username,bio,website;
    ImageView done,close;
    TextView btn_change_profile_pic;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    LoadingDialog loadingDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        profileimg = findViewById(R.id.button_image);
        fullname = findViewById(R.id.editname);
        username = findViewById(R.id.editusername);
        website = findViewById(R.id.editwebsite);
        bio = findViewById(R.id.editbio);
        done = findViewById(R.id.done);
        close = findViewById(R.id.close);
        loadingDialog = new LoadingDialog(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("MainImage").child(user.getUid());
        btn_change_profile_pic = findViewById(R.id.btn_change_profile_pic);

        btn_change_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImg();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String n1 = fullname.getText().toString().trim();
                final String u1 = username.getText().toString().trim();
                final String w1 = website.getText().toString().trim();
                final String b1 = bio.getText().toString();
                UploadData(n1,u1,w1,b1);


            }
        });

        RetreivingData();


    }

    private void ChooseImg() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null ) {
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setAspectRatio(4,5)
//                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                uploadFile();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(EditProfileActivity.this,"" + error,Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadFile() {
        loadingDialog.startloadingDialog();
        loadingDialog.textDiaglog("Uploading Profile Picture ");
        if (mImageUri != null) {
            Bitmap bitmap= null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),mImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();



            final StorageReference reference = mStorageRef.child("Files/"+System.currentTimeMillis() );

            reference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    loadingDialog.dismissDialog();
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("imgurl",uri);

                                    ref.child("Info").updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismissDialog();
                            Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void UploadData(String n1, String u1, String w1, String b1) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",n1);
        map.put("username",u1);
        map.put("website",w1);
        map.put("bio",b1);

        ref.child("Info").updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
            }
        });
    }

    private void RetreivingData() {
        ref.child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String img = snapshot.child("imgurl").getValue(String.class);
                String name = snapshot.child("name").getValue(String.class);
                String stringusername = snapshot.child("username").getValue(String.class);
                String stringbio = snapshot.child("bio").getValue(String.class);
                String stringwebsite = snapshot.child("website").getValue(String.class);

                Glide.with(getApplicationContext()).load(img).into(profileimg);
                fullname.setText(name);
                username.setText(stringusername);
                bio.setText(stringbio);
                website.setText(stringwebsite);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
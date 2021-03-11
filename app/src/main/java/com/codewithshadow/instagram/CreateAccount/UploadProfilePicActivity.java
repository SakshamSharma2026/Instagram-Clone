package com.codewithshadow.instagram.CreateAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Utils.LoadingDialog;
import com.codewithshadow.instagram.Dashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.HashMap;
import java.util.Map;

public class UploadProfilePicActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri resultUri;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference ref;
    FrameLayout btnAddPhoto;
    ImageView profilePicture;
    String stringname,stringusername;
    TextView text;
    String finalimageUrl;
    LoadingDialog loadingDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);
        btnAddPhoto = findViewById(R.id.btn_addphoto);
        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference().child("Users");
        profilePicture = findViewById(R.id.profile_pic);
        text = findViewById(R.id.text);
        mStorageRef = FirebaseStorage.getInstance().getReference("MainImage").child(user.getUid());
        loadingDialog = new LoadingDialog(this);

        PushDownAnim.setPushDownAnimTo(btnAddPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profilePicture.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.photocamera).getConstantState()))
                {
                    text.setText("Continue");
                    openFileChooser();
                }
                else
                {
                    uploadFile();
                }

            }
        });

        Intent intent = getIntent();
        stringname = intent.getStringExtra("name");
        stringusername = intent.getStringExtra("username");

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null ) {
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
                Glide.with(this).load(mImageUri) .apply(RequestOptions.circleCropTransform())
                        .into(profilePicture);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(UploadProfilePicActivity.this,"" + error,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile() {
        if (mImageUri != null) {
            loadingDialog.startloadingDialog();
            final StorageReference reference = mStorageRef.child("Files/"+System.currentTimeMillis() );

            reference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (task.isComplete())
                                            {
                                                String token = task.getResult();
                                                finalimageUrl=uri.toString();
                                                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                                String imageUrl=uri.toString();
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("name",stringname);
                                                map.put("username",stringusername);
                                                map.put("imgurl",imageUrl);
                                                map.put("userid",user.getUid());
                                                map.put("token", token);
//                                                map.put("status",true);
//                                                db.collection("Users").document(auth.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        loadingDialog.dismissDialog();
//                                                        Intent intent = new Intent(UploadProfilePicActivity.this, Dashboard.class);
//                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
//                                                        startActivity(intent);
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        loadingDialog.dismissDialog();
//                                                        Toast.makeText(UploadProfilePicActivity.this,"Error !",Toast.LENGTH_SHORT).show();
//                                                    }
//                                                });
                                                ref.child(auth.getUid()).child("Info").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        loadingDialog.dismissDialog();
                                                        Intent intent = new Intent(UploadProfilePicActivity.this, Dashboard.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    UserProfileChangeRequest profileUpdate=new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();
                                    user.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                            Toast.makeText(UploadProfilePicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

}
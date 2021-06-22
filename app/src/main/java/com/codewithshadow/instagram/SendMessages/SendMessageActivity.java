package com.codewithshadow.instagram.SendMessages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.Utils.AppSharedPreferences;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.SendNotification.NotiModel.NotificationReq;
import com.codewithshadow.instagram.SendNotification.NotiModel.NotificationResponce;
import com.codewithshadow.instagram.SendNotification.NotificationRequest;
import com.codewithshadow.instagram.SendNotification.RetrofitClient;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

import static com.codewithshadow.instagram.SendNotification.Constants.BASE_URL;

public class SendMessageActivity extends AppCompatActivity {
    CircleImageView imageView;
    TextView username;
    String stringusername,stringuserid,stringimgurl;
    EditText typemessage;
    ImageView btn_SendMessage,backbtn;
    DatabaseReference ref;
    FirebaseUser user;
    List<SendMessageModel> list;
    SendMessageAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    AppSharedPreferences appSharedPreferences;
    ValueEventListener seenListener;
    ImageView choose_img;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    LoadingDialog loadingDialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        appSharedPreferences = new AppSharedPreferences(this);
        loadingDialog = new LoadingDialog(this);
        imageView = findViewById(R.id.button_image);
        username = findViewById(R.id.username);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        Intent intent = getIntent();
        stringusername = intent.getStringExtra("username");
        stringimgurl  = intent.getStringExtra("imgurl");
        stringuserid = intent.getStringExtra("userid");
        typemessage = findViewById(R.id.item_input);
        btn_SendMessage = findViewById(R.id.item_send_icon);
        recyclerView = findViewById(R.id.chatrecycler);
        list = new ArrayList<>();
        manager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        choose_img = findViewById(R.id.item_emoji_icon);
        choose_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseImg();
            }
        });

        Read_Message(user.getUid(),stringuserid,stringimgurl);




        btn_SendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String t1 = typemessage.getText().toString();

                if(TextUtils.isEmpty(t1))
                {
                    typemessage.setError("Enter Message");
                    typemessage.requestFocus();
                }
                else

                {
                    Send_Message(t1);
                    typemessage.getText().clear();
                }


            }
        });

        username.setText(stringusername);

        Glide.with(this).load(stringimgurl).into(imageView);

        Seen_Message(stringuserid);

    }

    private void ChooseImg() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void Seen_Message(String uid)
    {
        seenListener = ref.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    SendMessageModel sendMessageModel = dataSnapshot.getValue(SendMessageModel.class);
                    if (sendMessageModel.getReceiver().equals(user.getUid()) && sendMessageModel.getSender().equals(uid))
                    {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Send_Message(String t1) {
        String key = ref.child("Chats").push().getKey();
        Map<String,Object> map = new HashMap<>();
        map.put("msg",t1);
        map.put("sender",user.getUid());
        map.put("key",key);
        map.put("type","text");
        map.put("isseen",false);
        map.put("receiver",stringuserid);

        ref.child("Chats").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sentByRest(stringuserid,stringusername,t1);
            }
        });
    }


    private void Read_Message(String myid,String userid,String imageurl)
    {
        ref.child("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot snap : snapshot.getChildren())
                {
                    SendMessageModel model = snap.getValue(SendMessageModel.class);

                    if (model.getReceiver().equals(myid) && model.getSender().equals(userid) || model.getReceiver().equals(userid) && model.getSender().equals(myid))
                    {
                        list.add(model);
                    }

                }
                adapter = new SendMessageAdapter(SendMessageActivity.this,list,imageurl);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void sentByRest(String publisherId,String likedusername,String msg) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId).child("Info")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        NotificationReq req = new NotificationReq(dataSnapshot.child("token").getValue(String.class),
                                new NotificationReq.Notification(appSharedPreferences.getUserName(),
                                        appSharedPreferences.getUserName() + ": " + msg));

                        RetrofitClient.getRetrofit(BASE_URL)
                                .create(NotificationRequest.class)
                                .sent(req)
                                .enqueue(new Callback<NotificationResponce>() {
                                    @Override
                                    public void onResponse(Call<NotificationResponce> call, retrofit2.Response<NotificationResponce> response) {
                                        if (response.code() == 200) {
                                        }
//                                        progress_bar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onFailure(Call<NotificationResponce> call, Throwable t) {
//                                        progress_bar.setVisibility(View.GONE);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        progress_bar.setVisibility(View.GONE);
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null ) {
            mImageUri = data.getData();
            uploadFile();
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SendMessageActivity.this,"" + error,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFile() {
        loadingDialog.startloadingDialog();
        loadingDialog.textDiaglog("Sending image");
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

            final StorageReference reference = mStorageRef.child("ChatImages/"+ user.getUid() +"post_"+System.currentTimeMillis() );

            reference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    loadingDialog.dismissDialog();
                                    String key = ref.child("Chats").push().getKey();
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("msg","Sent a photo");
                                    map.put("sender",user.getUid());
                                    map.put("key",key);
                                    map.put("msg_img",uri.toString());
                                    map.put("type","image");
                                    map.put("isseen",false);
                                    map.put("receiver",stringuserid);
                                    ref.child("Chats").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                            Toast.makeText(SendMessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
//        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        status("offline");
//        ref.child("Chats").removeEventListener(seenListener);
    }
}
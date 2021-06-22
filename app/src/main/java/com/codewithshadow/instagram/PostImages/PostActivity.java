package com.codewithshadow.instagram.PostImages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.Utils.GridImageAdapter;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Utils.FilePath;
import com.codewithshadow.instagram.Utils.FileSearch;
import com.codewithshadow.instagram.Utils.SquareImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    Button btn_upload_img;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri resultUri;
    String userid,userimg,mSelectImage;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    DatabaseReference ref,alluserref;
    FirebaseUser user;
    ImageView continuee,backbtn;
    SquareImageView img;
    GridView gridView;
    private ArrayList<String> directories;
    private static final int NUM_GRID_COLUMNS = 3;
    private String mappends = "file:/";

    Spinner directorySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        user = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        alluserref = FirebaseDatabase.getInstance().getReference().child("AllPosts");
        mStorageRef = FirebaseStorage.getInstance().getReference("MainImage");
        continuee = findViewById(R.id.continuee);
        gridView = findViewById(R.id.GV_Images);
        img = findViewById(R.id.galleryimg);
        directories = new ArrayList<>();
        directorySpinner = findViewById(R.id.directoryspinner);
        backbtn = findViewById(R.id.close);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //----------------------------------RunTime Permissions----------------------------------//
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET).withListener(
                new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        //Normal Functionl if user allow permission
                        init();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }
        ).check();


//        btn_upload_img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openFileChooser();
//            }
//        });

        ref.child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userid = snapshot.child("username").getValue(String.class);
                userimg = snapshot.child("imgurl").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null ) {
            mImageUri = data.getData();
            CropImage.activity(mImageUri)
                    .setAspectRatio(1,1)
//                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Glide.with(getApplicationContext()).load(resultUri).into(img);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(PostActivity.this,"" + error,Toast.LENGTH_SHORT).show();
            }
        }
    }


private void init()
{
    FilePath filePaths = new FilePath();
    if (FileSearch.getDirectoryPaths(filePaths.PICTURES) !=null) {
        directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
    }
    directories.add(filePaths.CAMERA);
    try {

//        directories.add(filePaths.WhatsApp);
//        directories.add(filePaths.SS);


    }catch (Exception e)
    {
        e.printStackTrace();
    }


    ArrayList<String> directoryNames = new ArrayList<>();
    for (int i=0;i<directories.size();i++)
    {
        int index = directories.get(i).lastIndexOf("/");
        String string = directories.get(i).substring(index).replace("/","");
        directoryNames.add(string);
    }


    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.customspinnertext,directories);
    adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
    directorySpinner.setAdapter(adapter);
    directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            setupGridView(directories.get(i));

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });

}

private void setupGridView(String selectedDirectory)
{
    final ArrayList<String> imgUrl = FileSearch.getFilePaths(selectedDirectory);
    int gridWidth = getResources().getDisplayMetrics().widthPixels;
    int imageWidth = gridWidth/NUM_GRID_COLUMNS;
    gridView.setColumnWidth(imageWidth);
    GridImageAdapter gridImageAdapter = new GridImageAdapter(PostActivity.this,R.layout.layout_grid_imageview,mappends,imgUrl);
    gridView.setAdapter(gridImageAdapter);
    setImage(imgUrl.get(0),img,mappends);
    mSelectImage = imgUrl.get(0);

    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            setImage(imgUrl.get(i),img,mappends);
            mSelectImage = imgUrl.get(i);
        }
    });

    continuee.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(PostActivity.this,FinalPostActivity.class);
            intent.putExtra("mSelectedImage",mSelectImage);
            startActivity(intent);
        }
    });

}


private void setImage(String imgurl , ImageView imageView , String appends)
{
    ImageLoader imageLoader = ImageLoader.getInstance();
    imageLoader.displayImage(appends + imgurl, imageView, new ImageLoadingListener() {
        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    });
}



    }

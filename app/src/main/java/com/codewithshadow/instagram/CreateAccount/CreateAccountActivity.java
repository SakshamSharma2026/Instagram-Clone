package com.codewithshadow.instagram.CreateAccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codewithshadow.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    EditText email,password;
    FrameLayout btnNext;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference ref;
    ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Users");
        progressBar = findViewById(R.id.button_progress);
        email =findViewById(R.id.item_input_email);
        password = findViewById(R.id.item_input_password);
        btnNext =  findViewById(R.id.btn_next);
        btnNext.setEnabled(false);
        btnNext.setBackgroundColor(getResources().getColor(R.color.blue2));

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
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }
        ).check();


        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

                if (s.toString().equals("")) {
                    btnNext.setEnabled(false);
                    btnNext.setBackgroundColor(Color.GRAY);

                } else if (!s.toString().isEmpty()){
                    btnNext.setBackgroundColor(getResources().getColor(R.color.blue));
                    btnNext.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }
        });
        PushDownAnim.setPushDownAnimTo(btnNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String e1 = email.getText().toString().trim();
                final String p1 = password.getText().toString().trim();
                
                if(TextUtils.isEmpty(e1))
                {
                    email.setError("Enter Email");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(p1))
                {
                    password.setError("Enter Password");
                    password.requestFocus();
                }
                else
                {
                    createAccount(e1,p1);
                }
                
                
            }
        });

    }

    private void createAccount(String e1, String p1) {
        btnNext.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(e1,p1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Map<String,Object> map = new HashMap<>();
                    map.put("email",e1);
//                    db.collection("Users").document(auth.getUid()).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            progressBar.setVisibility(View.GONE);
//                            Intent intent = new Intent(CreateAccountActivity.this,Name_UsernameActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(CreateAccountActivity.this,"Error !",Toast.LENGTH_SHORT).show();
//                        }
//                    });
                    ref.child(auth.getUid()).child("Info").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(CreateAccountActivity.this,Name_UsernameActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }
}
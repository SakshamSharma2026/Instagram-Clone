package com.codewithshadow.instagram.LoginPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codewithshadow.instagram.CreateAccount.CreateAccountActivity;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Dashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.HashMap;
import java.util.Map;

public class LoginScreen extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference ref;
    TextView btnCreateAccount;
    EditText email,password;
    FrameLayout btnLogin;
    ProgressBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
//        btn_fblogin = findViewById(R.id.btn_fblogin);
//        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
         user = FirebaseAuth.getInstance().getCurrentUser();
         ref = FirebaseDatabase.getInstance().getReference().child("Users");
         btnCreateAccount = findViewById(R.id.btn_create_account);
         bar = findViewById(R.id.button_progress);
        email =findViewById(R.id.item_input_email);
        password = findViewById(R.id.item_input_password);
        btnLogin =  findViewById(R.id.btn_next);
        btnLogin.setEnabled(false);
        btnLogin.setBackgroundColor(getResources().getColor(R.color.blue2));
         btnCreateAccount.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(LoginScreen.this, CreateAccountActivity.class);
                 startActivity(intent);
             }
         });






        password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

                if (s.toString().equals("")) {
                    btnLogin.setEnabled(false);
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.blue2));

                } else if (!s.toString().isEmpty()){
                    btnLogin.setBackgroundColor(getResources().getColor(R.color.blue));
                    btnLogin.setEnabled(true);
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
        PushDownAnim.setPushDownAnimTo(btnLogin).setOnClickListener(new View.OnClickListener() {
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
                    SignIn(e1,p1);
                }


            }
        });




//        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();

//        client = GoogleSignIn.getClient(this, gso);

        //-----------------Facebook Button-----------------//


//        PushDownAnim.setPushDownAnimTo(btn_fblogin).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                GoogleLogin();
//
//
////                LoginManager.getInstance().logInWithReadPermissions(LoginScreen.this, Arrays.asList("email", "public_profile"));
////                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
////                            @Override
////                            public void onSuccess(LoginResult loginResult) {
////                                Log.d("Success", "facebook:onSuccess:" + loginResult);
////
////                                handleFacebookAccessToken(loginResult.getAccessToken());
////                                btn_fblogin.setEnabled(false);
////                            }
////
////                            @Override
////                            public void onCancel() {
////                                Log.d("Cancel", "facebook:onCancel");
////                                // ...
////                            }
////
////                            @Override
////                            public void onError(FacebookException error) {
////                                // ...
////                                Log.d("Cancel", error.toString());
////
////                            }
////                        }
////                );
//            }
//        });
    }

    private void SignIn(String e1, String p1) {
        bar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(e1,p1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            String token = task.getResult().getToken();
                            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                            Map<String, Object> map = new HashMap<>();
                            map.put("token", token);
                            ref.child(user.getUid()).child("Info").updateChildren(map);

                        }
                    });
                    bar.setVisibility(View.GONE);
                    Intent intent = new Intent(LoginScreen.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                bar.setVisibility(View.GONE);
                Toast.makeText(LoginScreen.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void GoogleLogin() {
//        Intent intent = client.getSignInIntent();
//        startActivityForResult(intent, Rc_Sign_in);
//    }


//    private void handleFacebookAccessToken(AccessToken token) {
//
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user=mAuth.getCurrentUser();
//                            String fbname = user.getDisplayName();
//                            String fbemail = user.getEmail();
//                            String fbimgUrl = user.getPhotoUrl().toString();
//                            fbimgUrl = fbimgUrl + "?type=large&redirect=true&width=600&height=600";
//                            UserModel uu = new UserModel(fbname,fbemail,fbimgUrl,user.getUid());
//                            ref.child(user.getUid()).child("Info").setValue(uu).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
//                                        @Override
//                                        public void onSuccess(InstanceIdResult instanceIdResult) {
//                                            String token = instanceIdResult.getToken();
//                                            Map<String, Object> map = new HashMap<>();
//                                            map.put("token", token);
//                                            ref.child(user.getUid()).child("Info").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//
//                                                }
//                                            });
//                                        }
//                                    });
//                                }
//                            });
////                            updateUI();
//
//                            // Sign in success, update UI with the signed-in user's information
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(LoginScreen.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//
//                        }
//                        // ...
//                    }
//                });
//    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
//        if (requestCode == Rc_Sign_in) {
//            Toast.makeText(LoginScreen.this, "Authentication is in progress...", Toast.LENGTH_LONG).show();
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
////                bar.setVisibility(View.VISIBLE);
//                // Google Sign In was successful, authenticate with Firebase
//                GoogleSignInAccount account = task.getResult(ApiException.class);
//                String email = account.getEmail();
//                String user_name = account.getDisplayName();
//                String imageUrl =account.getPhotoUrl().toString();
//                imageUrl = imageUrl.substring(0, imageUrl.length() - 5) + "s400-c";
//
//                //Url Change HoSakta hai
//
//                FirebaseGoogleAuth(account.getIdToken(),user_name,email,imageUrl);
//            } catch (ApiException e) {
//                Toast.makeText(LoginScreen.this, "Signin Error", Toast.LENGTH_LONG).show();
//                // Google Sign In failed, update UI appropriately
//                Log.w("Error", "Google sign in failed", e);
//            }
//        }
//
//    }


//    private void FirebaseGoogleAuth(String idToken, final String user_name, final String email, final String imageUrl) {
//        AuthCredential authCredential= GoogleAuthProvider.getCredential(idToken,null);
//        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful())
//                {
//                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
//                    final FirebaseUser user = mAuth.getCurrentUser();
//
//                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.hasChild(user.getUid())) {
//                                Intent intent1=new Intent(LoginScreen.this, dashboard.class);
//                                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                                        String token = task.getResult().getToken();
//                                        Map<String, Object> map = new HashMap<>();
//                                        map.put("token", token);
//                                        databaseReference.child(user.getUid()).child("Info").updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                            }
//                                        });
//                                    }
//                                });
//                                startActivity(intent1);
//                                finish();
////                                MDToast mdToast=MDToast.makeText(GetStarted.this," Welcome back: " + user.getDisplayName(),MDToast.LENGTH_LONG,MDToast.TYPE_INFO);
//////                                mdToast.setIcon(R.drawable.ioio);
////                                mdToast.show();
//                                return;
//                            }
//                            else {
//                                UserModel uu = new UserModel(user_name,email,imageUrl,user.getUid());
//                                databaseReference.child(user.getUid()).child("Info").setValue(uu).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//
//                                    }
//                                });
//                                Intent intent1=new Intent(LoginScreen.this, dashboard.class);
//                                startActivity(intent1);
//                                finish();
////                                MDToast mdToast=MDToast.makeText(GetStarted.this,"Authentication Successful Welcome: " + user.getDisplayName(),MDToast.LENGTH_LONG,MDToast.TYPE_SUCCESS);
////                                mdToast.show();
////                                Inappnotification();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
////                    finish();
//                }
//                else {
//                    // If sign in fails, display a message to the user.
////                    MDToast mdToast = MDToast.makeText(getApplicationContext(), "Signed In Failed", MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
////                    mdToast.show();
//                }
//            }
//        });
//    }



//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        mCallbackManager.onActivityResult(requestCode,resultCode,data);
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void updateUI() {
        Intent intent=new Intent(LoginScreen.this, Dashboard.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            updateUI();
        }
    }

}
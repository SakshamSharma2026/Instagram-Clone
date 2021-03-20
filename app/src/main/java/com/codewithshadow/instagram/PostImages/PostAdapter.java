package com.codewithshadow.instagram.PostImages;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.codewithshadow.instagram.AppSharedPreferences;
import com.codewithshadow.instagram.CommentActivity.CommentActivity;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.SendNotification.NotiModel.NotificationReq;
import com.codewithshadow.instagram.SendNotification.NotiModel.NotificationResponce;
import com.codewithshadow.instagram.SendNotification.NotificationRequest;
import com.codewithshadow.instagram.SendNotification.RetrofitClient;
import com.codewithshadow.instagram.Utils.SquareImageView;
import com.codewithshadow.instagram.Utils.UniversalImageLoderClass;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.zolad.zoominimageview.ZoomInImageView;
import com.zolad.zoominimageview.ZoomInImageViewAttacher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

import static com.codewithshadow.instagram.SendNotification.Constants.BASE_URL;
import static com.google.android.material.animation.AnimationUtils.DECELERATE_INTERPOLATOR;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    private Context aCtx;
    private List<PostModel> list;

    public PostAdapter(Context aCtx, List<PostModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;
    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.card_post,parent, false);
        return new PostAdapter.MyViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        AppSharedPreferences appSharedPreferences = new AppSharedPreferences(aCtx);
        PostModel model = list.get(position);
        GestureDetector gestureDetector;

        final Drawable drawable = holder.heart.getDrawable();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db =FirebaseFirestore.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AllPosts");

        String userkey = user.getUid();
        String postkey = model.getPostkey();
        String publisherId = model.getPublisherid();
        holder.whiteheart.setVisibility(View.VISIBLE);
        holder.redheart.setVisibility(View.GONE);
        gestureDetector = new GestureDetector(aCtx, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }


            @Override
            public boolean onDoubleTap(MotionEvent e) {
                AnimatorSet animatorSet = new AnimatorSet();
                holder.redheart.setScaleX(0.1f);
                holder.whiteheart.setScaleY(0.1f);
                ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(holder.redheart, "scaleY", 0.1f,1f);
                objectAnimatorY.setDuration(300);
                objectAnimatorY.setInterpolator(DECELERATE_INTERPOLATOR);
                ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(holder.redheart, "scaleX", 0.1f,1f);
                objectAnimatorX.setDuration(300);
                objectAnimatorX.setInterpolator(DECELERATE_INTERPOLATOR);
                holder.redheart.setVisibility(View.VISIBLE);
                holder.whiteheart.setVisibility(View.GONE);
                animatorSet.playTogether(objectAnimatorY,objectAnimatorX);
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        nrLikes(postkey,userkey,publisherId,model.getUsername(),model.getImgurl(),model.getPostimg(),appSharedPreferences);
                        super.onAnimationEnd(animation);
                    }
                });

                if (drawable instanceof AnimatedVectorDrawableCompat)
                {
                    AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
                    holder.heart.setVisibility(View.VISIBLE);
                    avd.start();
                }
                else if (drawable instanceof  AnimatedVectorDrawable)
                {
                    holder.heart.setVisibility(View.VISIBLE);
                    AnimatedVectorDrawable avd2 = (AnimatedVectorDrawable) drawable;
                    avd2.start();

                }

                return true;
            }
        }
    );








        holder.postimg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        CollectionReference collectionReference = db.collection("AllPosts").document(postkey).collection("Likes");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentSnapshot documentSnapshot : value)
                {
                    if (documentSnapshot.getId().equals(userkey))
                    {
                        holder.redheart.setVisibility(View.VISIBLE);
                        holder.whiteheart.setVisibility(View.GONE);
                        holder.redheart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                holder.redheart.setVisibility(View.GONE);
                                holder.whiteheart.setVisibility(View.VISIBLE);
                                removeLikes(postkey,userkey);

                            }
                        });
                    }
                    else
                    {
                        holder.whiteheart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AnimatorSet animatorSet = new AnimatorSet();
                                holder.redheart.setScaleX(0.3f);
                                holder.whiteheart.setScaleY(0.3f);
                                ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(holder.redheart, "scaleY", 0.3f,3f);
                                objectAnimatorY.setDuration(5000);
                                objectAnimatorY.setInterpolator(DECELERATE_INTERPOLATOR);
                                ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(holder.redheart, "scaleX", 0.3f,3f);
                                objectAnimatorX.setDuration(5000);
                                objectAnimatorX.setInterpolator(DECELERATE_INTERPOLATOR);
                                holder.redheart.setVisibility(View.VISIBLE);
                                holder.whiteheart.setVisibility(View.GONE);
                                animatorSet.playTogether(objectAnimatorY,objectAnimatorX);
                                animatorSet.start();
                                animatorSet.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        nrLikes(postkey,userkey,publisherId,model.getUsername(),model.getImgurl(),model.getPostimg(),appSharedPreferences);
                                        super.onAnimationEnd(animation);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postkey).child("Likes").child(userkey).exists())
                {
                    holder.redheart.setVisibility(View.VISIBLE);
                    holder.whiteheart.setVisibility(View.GONE);
                    holder.redheart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.redheart.setVisibility(View.GONE);
                            holder.whiteheart.setVisibility(View.VISIBLE);
                            removeLikes(postkey,userkey);

                        }
                    });
                }
                else
                {
                    holder.whiteheart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            holder.redheart.setScaleX(0.3f);
                            holder.whiteheart.setScaleY(0.3f);
                            ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(holder.redheart, "scaleY", 0.3f,3f);
                            objectAnimatorY.setDuration(5000);
                            objectAnimatorY.setInterpolator(DECELERATE_INTERPOLATOR);
                            ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(holder.redheart, "scaleX", 0.3f,3f);
                            objectAnimatorX.setDuration(5000);
                            objectAnimatorX.setInterpolator(DECELERATE_INTERPOLATOR);
                            holder.redheart.setVisibility(View.VISIBLE);
                            holder.whiteheart.setVisibility(View.GONE);
                            animatorSet.playTogether(objectAnimatorY,objectAnimatorX);
                            animatorSet.start();
                            animatorSet.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    nrLikes(postkey,userkey,publisherId,model.getUsername(),model.getImgurl(),model.getPostimg(),appSharedPreferences);
                                    super.onAnimationEnd(animation);
                                }
                            });
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        isLikes(holder.likescount,holder.commentcount,postkey);


        holder.username.setText(model.getUsername());
        holder.username2.setText(model.getUsername());
        holder.caption.setText(model.getCaption());
        holder.uploaddate.setText(model.getTime());
        holder.location.setText(model.getLocation());


        if (aCtx == null) {
            return;
        }
            RequestOptions myOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
    Glide.with(aCtx)
            .load(list.get(position).getImgurl())
            .thumbnail(0.05f)
            .apply(myOptions)
            .into(holder.userImage);

    Glide.with(aCtx)
            .load(list.get(position).getPostimg())
            .thumbnail(0.05f)
            .apply(myOptions)
            .into(holder.postimg);




        holder.commentcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aCtx, CommentActivity.class);
                intent.putExtra("userkey",userkey);
                intent.putExtra("postkey",postkey);
                intent.putExtra("postimg",model.getPostimg());
                intent.putExtra("username",model.getUsername());
                intent.putExtra("userimg",model.getImgurl());
                aCtx.startActivity(intent);

            }
        });

        holder.commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aCtx, CommentActivity.class);
                intent.putExtra("userkey",userkey);
                intent.putExtra("postkey",postkey);
                intent.putExtra("postimg",model.getPostimg());
                intent.putExtra("username",model.getUsername());
                intent.putExtra("userimg",model.getImgurl());
                aCtx.startActivity(intent);

            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username,username2,caption,likescount,commentcount,uploaddate,location;
        ImageView heart,whiteheart,redheart,commentImage,postimg;
        CircleImageView userImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            username2 = itemView.findViewById(R.id.username2);
            caption = itemView.findViewById(R.id.text_caption);
            userImage = itemView.findViewById(R.id.button_image);
            postimg = itemView.findViewById(R.id.postimg);
            heart = itemView.findViewById(R.id.likeheart);
            redheart = itemView.findViewById(R.id.image_heart_red);
            whiteheart = itemView.findViewById(R.id.image_heart);
            likescount = itemView.findViewById(R.id.likescount);
            commentcount = itemView.findViewById(R.id.commentscount);
            uploaddate = itemView.findViewById(R.id.uploaddate);
            commentImage = itemView.findViewById(R.id.speech_bubble);
            location = itemView.findViewById(R.id.location);

        }
    }

    private void isLikes(TextView textView ,TextView commentcount,String postkey)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("AllPosts")
                .child(postkey);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                textView.setText(snapshot.child("Likes").getChildrenCount() + " likes");
                commentcount.setText("View all " + snapshot.child("Comments").getChildrenCount() + " comments");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    
    private void nrLikes(String postkey,String userkey,String publisherId,String username,String img,String postimg, AppSharedPreferences appSharedPreferences)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postkey).child("Likes");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postkey).child("Info");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = new Date();
        Map<String, Object> map = new HashMap();
        map.put("postimg",postimg);
        map.put("username",appSharedPreferences.getUserName());
        map.put("imgurl",appSharedPreferences.getImgUrl());
        map.put("time",sdf.format(date));

        databaseReference.child(user.getUid()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {

                            if (!snap.child("publisherid").getValue(String.class).equals(user.getUid()))
                            {
                                sentByRest(publisherId,appSharedPreferences.getUserName());
                            }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });



        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        userReference.child(userkey).child("Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String likedusername = snapshot.child("username").getValue(String.class);
                String likedimg = snapshot.child("imgurl").getValue(String.class);

                Map<String, Object> map2 = new HashMap();
                map2.put("postimg",postimg);
                map2.put("username",likedusername);
                map2.put("img",likedimg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void removeLikes(String postkey,String userkey)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postkey).child("Likes");
        databaseReference.child(userkey).removeValue();
    }




    private void sentByRest(String publisherId,String likedusername) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId).child("Info")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        NotificationReq req = new NotificationReq(dataSnapshot.child("token").getValue(String.class),
                                new NotificationReq.Notification(likedusername, likedusername + " liked your photo.")
                        );


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



}






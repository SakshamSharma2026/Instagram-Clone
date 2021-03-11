package com.codewithshadow.instagram.FullScreenImages;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.CommentActivity.CommentActivity;
import com.codewithshadow.instagram.PostImages.PostAdapter;
import com.codewithshadow.instagram.PostImages.PostModel;
import com.codewithshadow.instagram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullScreenAdapter extends RecyclerView.Adapter<FullScreenAdapter.MyViewHolder> {
    private Context aCtx;
    private List<FullScreenModel> list;

    public FullScreenAdapter(Context aCtx, List<FullScreenModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;

    }

    @NonNull
    @Override
    public FullScreenAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.card_post,parent, false);
        return new FullScreenAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FullScreenAdapter.MyViewHolder holder, int position) {
        FullScreenModel model = list.get(position);
        final boolean[] showingFirst = {true};

        final Drawable drawable = holder.heart.getDrawable();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AllPosts");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userkey = user.getUid();
//        String postkey = model.getPostkey();

//        isLikes(holder.likescount,holder.commentcount,postkey,userkey);


//        holder.postimg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                nrLikes(postkey,userkey,model.getUsername(),model.getImgurl(),model.getPostimg());
//
//                holder.smallheart.setImageResource(R.drawable.redheart);
//                if (drawable instanceof AnimatedVectorDrawableCompat)
//                {
//                    AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) drawable;
//                    holder.heart.setVisibility(View.VISIBLE);
//                    avd.start();
//                }
//                else if (drawable instanceof AnimatedVectorDrawable)
//                {
//                    holder.heart.setVisibility(View.VISIBLE);
//                    AnimatedVectorDrawable avd2 = (AnimatedVectorDrawable) drawable;
//                    avd2.start();
//
//                }
//            }
//        });



//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snap) {
//
//                if (snap.child(postkey).child("Likes").child(userkey).exists())
//                {
//                    Log.d("click1","click1");
//                    holder.smallheart.setImageResource(R.drawable.redheart);
//                    holder.smallheart.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (showingFirst[0]==true)
//                            {
//                                removeLikes(model.getPostkey(),model.getUserkey());
//                                showingFirst[0] = false;
//                            }
//                            else
//                            {
//                                nrLikes(postkey,userkey,model.getUsername(),model.getImgurl(),model.getPostimg());
//                                showingFirst[0] = true;
//                            }
//                        }
//                    });
//                }
//                else
//                {
//                    Log.d("click2","click2");
//
//                    holder.smallheart.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            holder.smallheart.setImageResource(R.drawable.redheart);
//                            nrLikes(postkey,userkey,model.getUsername(),model.getImgurl(),model.getPostimg());
//                        }
//                    });
//                }
//
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



        holder.username.setText(model.getUsername());
        holder.username2.setText(model.getUsername());

        if (aCtx == null) {
            return;
        }
        Glide.with(aCtx).load(model.getImgurl()).into(holder.userImage);
        Glide.with(aCtx).load(model.getPostimg()).into(holder.postimg);

//        holder.commentcount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(aCtx, CommentActivity.class);
//                intent.putExtra("userkey",userkey);
//                intent.putExtra("postkey",postkey);
//                intent.putExtra("username",model.getUsername());
//                intent.putExtra("userimg",model.getImgurl());
//                aCtx.startActivity(intent);
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username,username2,caption,likescount,commentcount;
        ImageView userImage,postimg,heart,smallheart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            username2 = itemView.findViewById(R.id.username2);
            caption = itemView.findViewById(R.id.text_caption);
            userImage = itemView.findViewById(R.id.button_image);
            postimg = itemView.findViewById(R.id.postimg);
            heart = itemView.findViewById(R.id.likeheart);
            smallheart = itemView.findViewById(R.id.image_heart_red);
            likescount = itemView.findViewById(R.id.likescount);
            commentcount = itemView.findViewById(R.id.commentscount);

//        cardll = itemView.findViewById(R.id.cardll);

        }
    }

//    private void isLikes(TextView textView ,TextView commentcount,String postkey,String userkey)
//    {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
//                .child("AllPosts")
//                .child(postkey);
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                textView.setText(snapshot.child("Likes").getChildrenCount() + "");
//                commentcount.setText("View all " + snapshot.child("Comments").getChildrenCount() + " comments.");
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void nrLikes(String postkey,String userkey,String username,String img,String postimg)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postkey)
                .child("Likes");
        Map<String, Object> map = new HashMap();
        map.put("postimg",postimg);
        map.put("username",username);
        map.put("img",img);
        databaseReference.child(userkey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("data " ,"data " + postkey + " " + userkey);
            }
        });
    }

    private void removeLikes(String postkey,String userkey)
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("AllPosts").child(postkey)
                .child("Likes");
        databaseReference.child(userkey).removeValue();
    }
}







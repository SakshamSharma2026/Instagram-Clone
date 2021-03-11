package com.codewithshadow.instagram;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.codewithshadow.instagram.FollowUser.FollowUserActivity;
import com.codewithshadow.instagram.FullScreenImages.FullScreenImageActivity;
import com.codewithshadow.instagram.PostImages.PostModel;
import com.codewithshadow.instagram.SearchActivity.SearchModel;
import com.codewithshadow.instagram.Utils.SquareImageView;
import com.codewithshadow.instagram.Utils.UniversalImageLoderClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class newAdapter extends RecyclerView.Adapter<newAdapter.MyViewHolder> {
    private Context aCtx;
    private List<PostModel> list;

    public newAdapter(Context aCtx, List<PostModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;

    }

    @NonNull
    @Override
    public newAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.photosviewpager, parent, false);
        return new newAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final newAdapter.MyViewHolder holder, final int position) {



        UniversalImageLoderClass.setImage(list.get(position).getPostimg(),holder.userImage,null);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aCtx, FullScreenImageActivity.class);
                intent.putExtra("userid",list.get(position).getPublisherid());
                aCtx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SquareImageView userImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.photsimg);


        }

        @Override
        public void onClick(View view) {

        }
    }
}




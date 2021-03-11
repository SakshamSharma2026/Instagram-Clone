package com.codewithshadow.instagram.FollowUser;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.codewithshadow.instagram.FullScreenImages.FullScreenImageActivity;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Utils.SquareImageView;
import com.codewithshadow.instagram.newAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private Context aCtx;
    private List<String> list;

    public ImageAdapter(Context aCtx, List<String> list)
    {
        this.aCtx=aCtx;
        this.list=list;

    }

    @NonNull
    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.photosviewpager, parent, false);
        return new ImageAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ImageAdapter.MyViewHolder holder, final int position) {
        RequestOptions myOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(aCtx)
                .load(list.get(position))
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(myOptions)
                .into(holder.userImage);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aCtx, FullScreenImageActivity.class);
                intent.putExtra("userid",user.getUid());
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



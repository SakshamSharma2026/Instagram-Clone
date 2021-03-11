package com.codewithshadow.instagram;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.FollowUser.FollowUserActivity;
import com.codewithshadow.instagram.LoginPage.UserModel;
import com.codewithshadow.instagram.SearchActivity.SearchAdapter;
import com.codewithshadow.instagram.SearchActivity.SearchModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserRecomAdapter extends RecyclerView.Adapter<UserRecomAdapter.MyViewHolder> {
    private Context aCtx;
    private List<UserModel> list;

    public UserRecomAdapter(Context aCtx, List<UserModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;

    }

    @NonNull
    @Override
    public UserRecomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.uigitdev_elements_instagram_recommend_item, parent, false);
        return new UserRecomAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRecomAdapter.MyViewHolder holder, int position) {
        UserModel model = list.get(position);
        holder.fullname.setText(model.getName());
        holder.username.setText(model.getUsername());
        if (aCtx == null) {
            return;
        }
        Glide.with(aCtx).load(model.getImgurl()).circleCrop().into(holder.userImage);

        holder.followbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aCtx, FollowUserActivity.class);
                intent.putExtra("fullname",model.getName());
                intent.putExtra("username",model.getUsername());
                intent.putExtra("imgurl",model.getImgurl());
                intent.putExtra("userid",model.getUserid());
                aCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, fullname;
        ImageView userImage;
        RelativeLayout followbtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.item_text);
            userImage = itemView.findViewById(R.id.item_image);
            fullname = itemView.findViewById(R.id.item_label);
            followbtn = itemView.findViewById(R.id.item_follow_button_click_parent);
        }
    }
}





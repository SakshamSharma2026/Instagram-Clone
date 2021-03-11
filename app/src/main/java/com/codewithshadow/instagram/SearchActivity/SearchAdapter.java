package com.codewithshadow.instagram.SearchActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.FollowUser.FollowUserActivity;
import com.codewithshadow.instagram.LoginPage.UserModel;
import com.codewithshadow.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
private Context aCtx;
private List<UserModel> list;

    public SearchAdapter(Context aCtx, List<UserModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.card_searchuser, parent, false);
        return new SearchAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel model = list.get(position);

        holder.fullname.setText(model.getName());
        holder.username.setText(model.getUsername());
        if (aCtx == null) {
            return;
        }
        Glide.with(aCtx).load(model.getImgurl()).circleCrop().into(holder.userImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
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

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.item_text);
        userImage = itemView.findViewById(R.id.item_image);
        fullname = itemView.findViewById(R.id.item_label);

    }
}
}





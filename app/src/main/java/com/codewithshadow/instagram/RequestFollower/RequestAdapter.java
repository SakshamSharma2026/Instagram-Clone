package com.codewithshadow.instagram.RequestFollower;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.FollowUser.FollowUserActivity;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.SearchActivity.SearchAdapter;
import com.codewithshadow.instagram.SearchActivity.SearchModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {
    private Context aCtx;
    private List<RequestModel> list;

    public RequestAdapter(Context aCtx, List<RequestModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;

    }

    @NonNull
    @Override
    public RequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.uigitdev_elements_instagram_followers_item, parent, false);
        return new RequestAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.MyViewHolder holder, int position) {
        RequestModel model = list.get(position);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        holder.fullname.setText(model.getName());
        holder.username.setText(model.getUsername());
        if (aCtx == null) {
            return;
        }
        Glide.with(aCtx).load(model.getImgurl()).circleCrop().into(holder.userImage);

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
                holder.itemView.setVisibility(View.GONE);
                Map<String, Object> map = new HashMap<>();
                map.put(model.getSender(),"true");
//                db.collection("Users").document(user.getUid()).collection("Followers").document(model.getSender()).set(map);


                Map<String, Object> map2 = new HashMap<>();
                map2.put(user.getUid(),"true");
//                db.collection("Users").document(model.getSender()).collection("Following").document(user.getUid()).set(map2);





                databaseReference.child(user.getUid()).child("Request").child(model.getSender()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Map<String, Object> map = new HashMap<>();
                        map.put(model.getSender(),"true");
                        databaseReference.child(user.getUid()).child("Followers").setValue(map);


                        Map<String, Object> map2 = new HashMap<>();
                        map2.put(user.getUid(),"true");
                        databaseReference.child(model.getSender()).child("Following").setValue(map2);
                    }
                });





            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
                holder.itemView.setVisibility(View.GONE);
                databaseReference.child(user.getUid()).child("Request").child(model.getSender()).removeValue();

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
        RelativeLayout btn_follow,btn_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.item_text);
            userImage = itemView.findViewById(R.id.item_image);
            fullname = itemView.findViewById(R.id.item_label);
            btn_follow = itemView.findViewById(R.id.item_follow_button_click_parent);
            btn_delete = itemView.findViewById(R.id.item_delete_button_click_parent);
//        userImage = itemView.findViewById(R.id.userimg);
//        fullname = itemView.findViewById(R.id.fullname);
//        cardll = itemView.findViewById(R.id.cardll);

        }
    }
}






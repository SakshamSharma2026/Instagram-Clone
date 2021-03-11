package com.codewithshadow.instagram.CommentActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.SearchActivity.SearchAdapter;
import com.codewithshadow.instagram.SearchActivity.SearchModel;
import com.codewithshadow.instagram.Utils.UniversalImageLoderClass;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private Context aCtx;
    private String key;
    private List<CommentModel> list;

    public CommentAdapter(Context aCtx, List<CommentModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;

    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.card_comment, parent, false);
        return new CommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {
        CommentModel model = list.get(position);
        holder.comment.setText(model.getComment());
        holder.username.setText(model.getUsername() + " ");
        if (aCtx == null) {
            return;
        }

        UniversalImageLoderClass.setImage(model.getImgurl(),holder.userImage,null);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, comment;
        CircleImageView userImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            userImage = itemView.findViewById(R.id.button_image);
            comment = itemView.findViewById(R.id.comment);
//        cardll = itemView.findViewById(R.id.cardll);

        }
    }
}






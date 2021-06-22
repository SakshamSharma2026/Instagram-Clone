package com.codewithshadow.instagram.HighLightStory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codewithshadow.instagram.Utils.AppSharedPreferences;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.Story.StoryModel;
import com.codewithshadow.instagram.Utils.UniversalImageLoderClass;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HighlightAdapter extends RecyclerView.Adapter<HighlightAdapter.MyViewHolder> {
    private Context aCtx;
    private List<StoryModel> list;
    AppSharedPreferences appSharedPreferences;

    public HighlightAdapter(Context aCtx, List<StoryModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;

    }

    @NonNull
    @Override
    public HighlightAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(aCtx).inflate(R.layout.card_story, parent, false);
            return new HighlightAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HighlightAdapter.MyViewHolder holder, int position) {
        StoryModel model = list.get(position);
        appSharedPreferences = new AppSharedPreferences(aCtx);
        UniversalImageLoderClass.setImage(model.getStoryimg(),holder.story_photo,null);
        holder.story_username.setText(model.getMsg());



       holder.story_photo_seen_layout.setBackgroundColor(aCtx.getResources().getColor(R.color.gray2));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(aCtx,OpenHighLightActivity.class);
                    intent.putExtra("userid",model.getUserId());
                    aCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView story_username,addstory_text;
        CircleImageView story_photo;
        RelativeLayout story_photo_seen_layout,backLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            story_username = itemView.findViewById(R.id.story_username);
            story_photo = itemView.findViewById(R.id.button_image);
            story_photo_seen_layout = itemView.findViewById(R.id.button_click_parent);


        }
    }
}




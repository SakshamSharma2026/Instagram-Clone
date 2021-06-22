package com.codewithshadow.instagram.NotificationFragmentData;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.Utils.AppSharedPreferences;
import com.codewithshadow.instagram.CommentActivity.CommentModel;
import com.codewithshadow.instagram.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationFragCommentAdapter extends RecyclerView.Adapter<NotificationFragCommentAdapter.MyViewHolder> {
    private Context aCtx;
    private List<CommentModel> list;


    public NotificationFragCommentAdapter(Context aCtx, List<CommentModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;


    }

    @NonNull
    @Override
    public NotificationFragCommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(aCtx).inflate(R.layout.uigitdev_elements_instagram_comment_notification_item, parent, false);
        return new NotificationFragCommentAdapter.MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull NotificationFragCommentAdapter.MyViewHolder holder, int position) {
        CommentModel model = list.get(position);
        AppSharedPreferences appSharedPreferences = new AppSharedPreferences(aCtx);

        if (aCtx == null) {
            return;
        }



        Glide.with(aCtx).load(model.getImgurl()).into(holder.userImage);
        Glide.with(aCtx).load(model.getPostimg()).into(holder.postimg);
        covertTimeToText(model.getTime(),holder,model,appSharedPreferences);







    }



    @Override
    public int getItemCount() {
        return list.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView userImage,postimg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.item_text);
            userImage = itemView.findViewById(R.id.item_picture);
            postimg = itemView.findViewById(R.id.item_content_photo);
//        cardll = itemView.findViewById(R.id.cardll);

        }
    }


    public String covertTimeToText(String dataDate , MyViewHolder holder, CommentModel model, AppSharedPreferences appSharedPreferences) {

        String convTime = null;
        String suffix = "ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = second + " secs " + suffix;
            } else if (minute < 60) {
                convTime = minute + " mins "+suffix;
            } else if (hour < 24) {
                convTime = hour + " hrs "+suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " Years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " Months " + suffix;
                } else {
                    convTime = (day / 7) + " Week " + suffix;
                }
            } else if (day < 7) {
                convTime = day+" Days "+suffix;
            }
            String text2 = model.getUsername() +  " commented: " + "@" + appSharedPreferences.getUserName() + " "+ model.getComment() + " "+ convTime;
            SpannableString ss2 = new SpannableString(text2);
            StyleSpan styleSpan2 = new StyleSpan(Typeface.BOLD);
            AbsoluteSizeSpan word_size = new AbsoluteSizeSpan(14,true);
            ss2.setSpan(styleSpan2, 0, model.getUsername().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss2.setSpan(word_size, 0, model.getUsername().length(),  Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.username.setText(ss2);


        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", e.getMessage());
        }

        return convTime;
    }
}


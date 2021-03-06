package com.codewithshadow.instagram.NotificationFragmentData;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private Context aCtx;
    private List<NotificationModel> list;


    public NotificationAdapter(Context aCtx, List<NotificationModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;


    }

    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(aCtx).inflate(R.layout.uigitdev_elements_instagram_like_notification_item, parent, false);
            return new NotificationAdapter.MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, int position) {
        NotificationModel model = list.get(position);

        if (aCtx == null) {
            return;
        }


        Glide.with(aCtx).load(model.getImgurl()).into(holder.userImage);
        Glide.with(aCtx).load(model.getPostimg()).into(holder.postimg);
        covertTimeToText(model.getTime(),holder.username,model.getUsername());






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


    public String covertTimeToText(String dataDate,TextView usernameTextView,String username) {

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
            String text2 = username +  " liked your photo . " + "\n" + convTime;
            SpannableString ss2 = new SpannableString(text2);
            StyleSpan styleSpan2 = new StyleSpan(Typeface.BOLD);
//            AbsoluteSizeSpan word_size = new AbsoluteSizeSpan(14,true);

            ss2.setSpan(styleSpan2, 0, username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            usernameTextView.setText(ss2);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convTime;
    }
}






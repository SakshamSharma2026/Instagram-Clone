package com.codewithshadow.instagram.SendMessages.MessageUser;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codewithshadow.instagram.R;
import com.codewithshadow.instagram.SendMessages.SendMessageActivity;
import com.codewithshadow.instagram.SendMessages.SendMessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageUserAdaper extends RecyclerView.Adapter<MessageUserAdaper.MyViewHolder> {
    private Context aCtx;
    private List<MessageUserModel> list;
    String theLastMessage;

    public MessageUserAdaper(Context aCtx, List<MessageUserModel> list)
    {
        this.aCtx=aCtx;
        this.list=list;

    }

    @NonNull
    @Override
    public MessageUserAdaper.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(aCtx).inflate(R.layout.card_useritem, parent, false);
        return new MessageUserAdaper.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageUserAdaper.MyViewHolder holder, int position) {
        MessageUserModel model = list.get(position);
        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference().child("Users");
        holder.username.setText(model.getUsername());
        Glide.with(aCtx).load(model.getImgurl()).into(holder.userImage);
        lastMessage(model.getReceiver(),holder.msg);

        ref.child(model.getReceiver()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(aCtx, SendMessageActivity.class);
                intent.putExtra("imgurl",model.getImgurl());
                intent.putExtra("username",model.getUsername());
                intent.putExtra("userid",model.getReceiver());

                aCtx.startActivity(intent);
            }
        });






    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView username, msg;
        ImageView userImage;
        RelativeLayout direct_message;
        CardView badge;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.item_text);
            userImage = itemView.findViewById(R.id.item_image);
            msg = itemView.findViewById(R.id.item_label);
            direct_message = itemView.findViewById(R.id.direct_message_layout);
            badge = itemView.findViewById(R.id.item_badge_card_parent);

        }
    }

    private void lastMessage(String userid , TextView last_msg)
    {
        theLastMessage = "default";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren())
                {
                    SendMessageModel model = snap.getValue(SendMessageModel.class);

                    if (model.getReceiver().equals(user.getUid()) && model.getSender().equals(userid) || model.getReceiver().equals(userid) && model.getSender().equals(user.getUid()))
                    {
                        theLastMessage = model.getMsg();
                    }

                }
                switch (theLastMessage)
                {
                    case "default":
                        last_msg.setText("Active now");
                        break;
                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }
                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}







package com.kgec.socailmediaapp3;

import android.graphics.Color;
import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages>userMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersDatabaseRef;

    public MessageAdapter(List<Messages>userMessageList){

        this.userMessageList=userMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_of_users,parent,false);

        mAuth=FirebaseAuth.getInstance();

        MessageViewHolder messageViewHolder=new MessageViewHolder(view);
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        String messageSenderId=mAuth.getCurrentUser().getUid();

        Messages messages=userMessageList.get(position);
        String fromUserId=messages.getUid();
        String fromMessageType=messages.getType();

        UsersDatabaseRef= FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);

        UsersDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    String image=dataSnapshot.child("ProfileImage").getValue().toString();
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(holder.receiver_image);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (fromMessageType.equals("text")){

            holder.receiver_message_text.setVisibility(View.INVISIBLE);
            holder.receiver_image.setVisibility(View.INVISIBLE);

            if (fromUserId.equals(messageSenderId)){

                holder.sender_message_text.setBackgroundResource(R.drawable.sender_message_layout);
                holder.sender_message_text.setTextColor(Color.WHITE);
                holder.sender_message_text.setGravity(Gravity.LEFT);
                holder.sender_message_text.setText(messages.getMessage());

            }else {

                // Receiver purpose

                holder.sender_message_text.setVisibility(View.INVISIBLE);
                holder.receiver_message_text.setVisibility(View.VISIBLE);
                holder.receiver_image.setVisibility(View.VISIBLE);


                holder.receiver_message_text.setBackgroundResource(R.drawable.receiver_message_layout);
                holder.receiver_message_text.setTextColor(Color.BLACK);
                holder.receiver_message_text.setGravity(Gravity.RIGHT);
                holder.receiver_message_text.setText(messages.getMessage());



            }
        }


    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView sender_message_text,receiver_message_text;
        CircleImageView receiver_image;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            sender_message_text=itemView.findViewById(R.id.sender_messsage_text);
            receiver_message_text=itemView.findViewById(R.id.receiver_message_text);
            receiver_image=itemView.findViewById(R.id.chat_profile_image);
        }
    }
}

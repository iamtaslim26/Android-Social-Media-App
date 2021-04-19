package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private Toolbar chat_toolbar;
    private EditText chat_text;
    private ImageButton send_btn;
    private RecyclerView chat_view;
    private String receiver_user_id,message_receiver_name,sender_user_id,saveCurrentDate,saveCurrentTime;
    private CircleImageView receiver_image;
    private TextView receiver_name;
    private DatabaseReference RootRef,UsersRef;
    private FirebaseAuth mAuth;
    private final List<Messages>messagesList=new ArrayList<>();
    private MessageAdapter messageAdapter;
    private TextView userlast_seen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        receiver_user_id=getIntent().getExtras().get("visit_user_id").toString();
        message_receiver_name=getIntent().getExtras().get("visit_fullname").toString();

        Initialize();


        chat_view=findViewById(R.id.private_messages_list_of_users);
        chat_view.setLayoutManager(new LinearLayoutManager(this));
        chat_view.setHasFixedSize(true);

        mAuth= FirebaseAuth.getInstance();
        sender_user_id= mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");



        DisplayReceiverInfo();


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendMessage();
            }
        });

        FetchMessages();


    }

    private void FetchMessages() {

        RootRef.child("Messages").child(sender_user_id)
                .child(receiver_user_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()){

                            Messages messages=dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage() {


        String input_text=chat_text.getText().toString();

        if (TextUtils.isEmpty(input_text)){

            Toast.makeText(this, "Write your message first. . . .", Toast.LENGTH_LONG).show();
        }
        else {
            updateUserStatus("Online");


            String message_sender_Ref="Messages/"+sender_user_id+"/"+receiver_user_id;
            String message_receiver_Ref="Messages/"+receiver_user_id+"/"+sender_user_id;

            DatabaseReference users_message_key=RootRef.child("Messages").child(sender_user_id)
                    .child(receiver_user_id)
                    .push();

            String unique_message_id=users_message_key.getKey();

            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd-yyyy");
            saveCurrentDate=dateFormat.format(calForDate.getTime());

            Calendar calForTime= Calendar.getInstance();
            SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:a");
            saveCurrentTime=timeFormat.format(calForTime.getTime());

            // Now save this to Realtime Database

            HashMap<String,Object>messageTextBody=new HashMap<>();
            messageTextBody.put("message",input_text);
            messageTextBody.put("date",saveCurrentDate);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("uid",sender_user_id);
            messageTextBody.put("type","text");

            HashMap<String ,Object>messageBodyDetails=new HashMap<>();
            messageBodyDetails.put(message_sender_Ref+"/"+unique_message_id,messageTextBody);
            messageBodyDetails.put(message_receiver_Ref+"/"+unique_message_id,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        Toast.makeText(ChatActivity.this, "Message Sent succesfully. . . .", Toast.LENGTH_SHORT).show();
                        chat_text.setText("");
                    }
                    else {

                        String e=task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "Failed.......        "+e, Toast.LENGTH_SHORT).show();
                        chat_text.setText("");
                    }



                }
            });




        }
    }

    private void DisplayReceiverInfo() {

        receiver_name.setText(message_receiver_name);
        RootRef.child("Users").child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    String receiver_profile_image=dataSnapshot.child("ProfileImage").getValue().toString();
                    String type=dataSnapshot.child("userstate").child("type").getValue().toString();
                    String last_date=dataSnapshot.child("userstate").child("date").getValue().toString();
                    String last_time=dataSnapshot.child("userstate").child("time").getValue().toString();
                    
                    if (type.equals("Online")){

                        userlast_seen.setText("Online");
                    }
                    else {

                        userlast_seen.setText("Last seen: "+last_time+"  "+last_date);
                    }
                    Picasso.get().load(receiver_profile_image).placeholder(R.drawable.profile).into(receiver_image);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateUserStatus(String state){

        String savecurrentDate,savecurrentTime;

        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd,yyyy");
        savecurrentDate=dateFormat.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm: a");
        savecurrentTime=timeFormat.format(calForTime.getTime());

        HashMap<String,Object>map=new HashMap<>();
        map.put("date",savecurrentDate);
        map.put("time",savecurrentTime);
        map.put("type",state);

        UsersRef.child(receiver_user_id)
                .child("userstate")
                .updateChildren(map);

    }

    private void Initialize() {

        chat_toolbar=findViewById(R.id.chat_toolbar);
        setSupportActionBar(chat_toolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view=layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);


        receiver_name=findViewById(R.id.custom_profile_name);
        receiver_image=findViewById(R.id.custom_profile_image);
        userlast_seen=findViewById(R.id.custom_user_last_seen);
        chat_text=findViewById(R.id.input_message);
        send_btn=findViewById(R.id.send_message_btn);

        messageAdapter=new MessageAdapter(messagesList);
        chat_view=findViewById(R.id.private_messages_list_of_users);
        chat_view.setLayoutManager(new LinearLayoutManager(this));
        chat_view.setHasFixedSize(true);
        chat_view.setAdapter(messageAdapter);


    }
}

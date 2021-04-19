package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView friends_list;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private DatabaseReference UsersRef,FriendsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mToolbar=findViewById(R.id.friends_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Friends");


        friends_list=findViewById(R.id.friends_recycler_view);
        friends_list.setLayoutManager(new LinearLayoutManager(this));
        friends_list.setHasFixedSize(true);


        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsRef=FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_id);





    }
    public void updateUserStatus(String state){

        String savecurrentDate,savecurrentTime;

        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("MM-dd,yyyy");
        savecurrentDate=dateFormat.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm: a");
        savecurrentTime=timeFormat.format(calForTime.getTime());

        HashMap<String,Object> map=new HashMap<>();
        map.put("date",savecurrentDate);
        map.put("time",savecurrentTime);
        map.put("type",state);

        UsersRef.child(current_user_id)
                .child("userstate")
                .updateChildren(map);

    }



    @Override
    protected void onStart() {
        super.onStart();

        updateUserStatus("Online");

        FirebaseRecyclerOptions<Friends>options=new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(FriendsRef,Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder>adapter=new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Friends model) {
                // First take an id ..as the database we created

                String userIds=getRef(position).getKey();

                UsersRef.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            final String username=dataSnapshot.child("username").getValue().toString();
                            final String fullname=dataSnapshot.child("fullname").getValue().toString();
                            final String profileImage=dataSnapshot.child("ProfileImage").getValue().toString();
                            String status=dataSnapshot.child("status").getValue().toString();

                            holder.fullName.setText(fullname);
                            holder.userName.setText(username);
                            holder.user_status.setText(status);
                            Picasso.get().load(profileImage).placeholder(R.drawable.profile).into(holder.profile_image);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder=new AlertDialog.Builder(FriendsActivity.this);
                                    builder.setTitle("Choose one");



                                    CharSequence[]options=new CharSequence[]{

                                            "Visit "+fullname+"'s profile",
                                            "Send message to "+fullname
                                    };

                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which==0){

                                                Intent profileIntent=new Intent(getApplicationContext(),PersonProfileActivity.class);
                                                profileIntent.putExtra("visit_user_id",userIds);
                                                startActivity(profileIntent);


                                            }
                                            else if (which==1){

                                                Intent chatIntent=new Intent(getApplicationContext(),ChatActivity.class);
                                                chatIntent.putExtra("visit_user_id",userIds);
                                                chatIntent.putExtra("visit_fullname",fullname);
                                                startActivity(chatIntent);

                                            }

                                        }
                                    });
                                    builder.show();
                                }
                            });



                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.all_user_display_layout,parent,false);
                FriendsViewHolder friendsViewHolder=new FriendsViewHolder(view);
                return friendsViewHolder;
            }
        };

        friends_list.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_image;
        TextView userName,fullName,user_status;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image=itemView.findViewById(R.id.user_profile_display_image);
            userName=itemView.findViewById(R.id.user_profile_display_name);
            fullName=itemView.findViewById(R.id.user_fullname);
            user_status=itemView.findViewById(R.id.user_status);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();

        if(id==android.R.id.home){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        updateUserStatus("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        updateUserStatus("Offline");
    }
}
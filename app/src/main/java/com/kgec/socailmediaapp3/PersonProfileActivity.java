package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView user_name,user_profilename,user_status,user_relationship,user_gender,user_country,user_dob;
    private Button send_request_btn,cancel_request_btn;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,FriendRequestsRef,FriendsRef;
    private String reciever_user_id,CURRENT_STATE,current_user_id,savecurrentDate;

    // The current user id is the Sender user id


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        Initialize();

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        reciever_user_id=getIntent().getExtras().get("visit_user_id").toString();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendRequestsRef=FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        FriendsRef=FirebaseDatabase.getInstance().getReference().child("Friends");

        UsersRef.child(reciever_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    String USERNAME=dataSnapshot.child("username").getValue().toString();
                    String FULLNAME=dataSnapshot.child("fullname").getValue().toString();
                    String DOB=dataSnapshot.child("dob").getValue().toString();
                    String GENDER=dataSnapshot.child("gender").getValue().toString();
                    String RELATIONSHIP=dataSnapshot.child("relationshipStatus").getValue().toString();
                    String STATUS=dataSnapshot.child("status").getValue().toString();
                    String COUNTRYNAME=dataSnapshot.child("countryName").getValue().toString();
                    String PROFILEIMAGE=dataSnapshot.child("ProfileImage").getValue().toString();

                    user_name.setText("User Name:- "+USERNAME);
                    user_profilename.setText("Name:- "+FULLNAME);
                    user_country.setText("Country:- "+COUNTRYNAME);
                    user_status.setText("Bio:- "+STATUS);
                    user_gender.setText("Gender:- "+GENDER);
                    user_relationship.setText("Relationship Status:- "+RELATIONSHIP);
                    user_dob.setText("Date of Birth:- "+DOB);

                    Picasso.get().load(PROFILEIMAGE).placeholder(R.drawable.profile).into(profile_image);



                    MaintainButtonText();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!current_user_id.equals(reciever_user_id)){

                send_request_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        send_request_btn.setEnabled(false);

                        if(CURRENT_STATE.equals("not_friend")){

                            SendFriendRequest();
                        }
                        else if (CURRENT_STATE.equals("request_sent")){

                            DeleteFriendRequest();
                        }
                        else if (CURRENT_STATE.equals("request_received")){

                            AcceptFriendRequest();
                        }
                        else if (CURRENT_STATE.equals("friends")){

                            UnfriendAnExistingFriend();
                        }
                    }
                });
        }


        cancel_request_btn.setVisibility(View.GONE);
        cancel_request_btn.setEnabled(false);


    }

    private void UnfriendAnExistingFriend() {
        FriendsRef.child(current_user_id)
                .child(reciever_user_id)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    FriendsRef.child(reciever_user_id)
                            .child(current_user_id)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                CURRENT_STATE="not_friend";
                                send_request_btn.setText("Send Friend Request");
                                send_request_btn.setEnabled(true);
                                cancel_request_btn.setVisibility(View.GONE);
                            }

                        }
                    });
                }

            }
        });

    }

    private void AcceptFriendRequest() {

     Calendar calendar=Calendar.getInstance();
     SimpleDateFormat dateFormat=new SimpleDateFormat("MMM-dd,yyyy");
     savecurrentDate=dateFormat.format(calendar.getTime());

     FriendsRef.child(current_user_id).child(reciever_user_id)
             .child("date")
             .setValue(savecurrentDate)
             .addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {

                     if (task.isSuccessful()){

                         FriendsRef.child(reciever_user_id)
                                 .child(current_user_id)
                                 .child("date")
                                 .setValue(savecurrentDate)
                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {




                                         // NOW WE HAVE TO DELETE THE FRIENDREQUEST AS THEY BECOME FRIENDS NOW

                                         FriendRequestsRef.child(current_user_id)
                                                 .child(reciever_user_id)
                                                 .removeValue()
                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<Void> task) {
                                                         FriendRequestsRef.child(reciever_user_id)
                                                                 .child(current_user_id)
                                                                 .removeValue()
                                                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                     @Override
                                                                     public void onComplete(@NonNull Task<Void> task) {

                                                                         CURRENT_STATE="friends";
                                                                         send_request_btn.setText("Unfriend The Person");
                                                                         send_request_btn.setEnabled(true);

                                                                         cancel_request_btn.setVisibility(View.GONE);


                                                                     }
                                                                 });

                                                     }
                                                 });

                                     }
                                 });
                     }

                 }
             });
    }

    private void DeleteFriendRequest() {

        FriendRequestsRef.child(current_user_id)
                .child(reciever_user_id)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            FriendRequestsRef.child(reciever_user_id)
                                    .child(current_user_id)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                CURRENT_STATE="not_friend";
                                                send_request_btn.setText("Send Friend Request");
                                                send_request_btn.setEnabled(true);

                                                cancel_request_btn.setVisibility(View.GONE);
                                            }

                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintainButtonText() {

        FriendRequestsRef.child(current_user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(reciever_user_id)){

                            String request_type=dataSnapshot.child(reciever_user_id).child("request_type").getValue().toString();

                            if (request_type.equals("sent")){

                                CURRENT_STATE="request_sent";
                                send_request_btn.setEnabled(true);
                                send_request_btn.setText("Cancel Friend Request");

                                cancel_request_btn.setVisibility(View.GONE);
                            }
                            else if (request_type.equals("received")){

                                CURRENT_STATE="request_received";
                                send_request_btn.setText("Accept Friend request");
                                cancel_request_btn.setVisibility(View.VISIBLE);
                                cancel_request_btn.setEnabled(true);
                                cancel_request_btn.setText("Cancel Friend Request");

                                cancel_request_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        DeleteFriendRequest();
                                    }
                                });

                            }
                        }else {

                            FriendsRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(reciever_user_id)){

                                        CURRENT_STATE="friends";
                                        send_request_btn.setText("Unfriend The Person");
                                        cancel_request_btn.setVisibility(View.GONE);

                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void SendFriendRequest() {

        FriendRequestsRef.child(current_user_id)
                .child(reciever_user_id)
                .child("request_type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            FriendRequestsRef.child(reciever_user_id)
                                    .child(current_user_id)
                                    .child("request_type")
                                    .setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                send_request_btn.setEnabled(true);

                                                send_request_btn.setText("Cancel Friend Request");
                                                CURRENT_STATE="request_sent";

                                                cancel_request_btn.setVisibility(View.GONE);


                                            }

                                        }
                                    });

                        }

                    }
                });
    }

    private void Initialize() {

        profile_image=findViewById(R.id.person_proifle_activity_imageview);
        user_name=findViewById(R.id.person_profile_activity_username);
        user_country=findViewById(R.id.person_profile_activity_country);
        user_dob=findViewById(R.id.person_profile_activity_dateofbirth);
        user_gender=findViewById(R.id.person_profile_activity_gender);
        user_status=findViewById(R.id.person_profile_activity_status);
        user_profilename=findViewById(R.id.person_proifle_activity_profile_name);
        user_relationship=findViewById(R.id.person_profile_activity_relatioshipstatus);
        send_request_btn=findViewById(R.id.person_profile_activity_send_request);
        cancel_request_btn=findViewById(R.id.person_profile_activity_cancel_request);

        CURRENT_STATE="not_friend";
    }
}
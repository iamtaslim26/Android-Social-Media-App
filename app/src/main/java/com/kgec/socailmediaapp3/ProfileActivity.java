package com.kgec.socailmediaapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView profile_image;
    private TextView user_name,user_profilename,user_status,user_relationship,user_gender,user_country,user_dob;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private DatabaseReference ProfileRef,FriendsRef,PostRef;
    private Button no_of_post_button,no_of_friends_button;
    private  int friends_count=0,posts_count=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Initialize();

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        ProfileRef= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        FriendsRef= FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_id);
        PostRef= FirebaseDatabase.getInstance().getReference().child("Posts");

        no_of_friends_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),FriendsActivity.class));
            }
        });

        no_of_post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),MyPostActivity.class));
            }
        });


        ProfileRef.addValueEventListener(new ValueEventListener() {
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





                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    friends_count=(int)dataSnapshot.getChildrenCount();
                    no_of_friends_button.setText(Integer.toString(friends_count)+" Friends");


                }
                else {

                    no_of_friends_button.setText("0 Friend");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        PostRef.orderByChild("uid").startAt(current_user_id)
                .endAt(current_user_id+"\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            posts_count= (int) dataSnapshot.getChildrenCount();
                            no_of_post_button.setText(Integer.toString(posts_count)+" Posts");
                        }
                        else {

                            no_of_post_button.setText("0 Post");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




    }

    private void Initialize() {


        profile_image=findViewById(R.id.profile_activity_imageView);
        user_name=findViewById(R.id.profile_activity_username);
        user_country=findViewById(R.id.profile_activity_country);
        user_dob=findViewById(R.id.profile_activity_dateofbirth);
        user_gender=findViewById(R.id.profile_activity_gender);
        user_status=findViewById(R.id.profile_activity_status);
        user_profilename=findViewById(R.id.profile_activity_profile_name);
        user_relationship=findViewById(R.id.profile_activity_relationship);
        no_of_friends_button=findViewById(R.id.no_of_Friends_btn);
        no_of_post_button=findViewById(R.id.no_of_post_button);

    }
}
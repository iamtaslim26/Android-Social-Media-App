package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView post_list;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,LikesRef;
    private CircleImageView profile_image;
    private TextView user_full_name;
    private String currentUserId;
    private ImageButton add_new_post_btn;
    Boolean LikeChecker=false;




    private DatabaseReference PostRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       mAuth=FirebaseAuth.getInstance();
       UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
       currentUserId=mAuth.getCurrentUser().getUid();

        navigationView=findViewById(R.id.navigation_view);
        drawerLayout=findViewById(R.id.drawer_layout);
        View navView=navigationView.inflateHeaderView(R.layout.navigation_header);



        View headerView = navigationView.getHeaderView(0);
        user_full_name=headerView.findViewById(R.id.nav_user_full_name);
        profile_image=headerView.findViewById(R.id.profile_image);


      add_new_post_btn=findViewById(R.id.add_new_post_button);


        mToolbar=findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        actionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);


        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        post_list=findViewById(R.id.all_user_post_list);


        mAuth=FirebaseAuth.getInstance();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        post_list.setHasFixedSize(true);
        post_list.setLayoutManager(linearLayoutManager);





        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                UserMenuSelcetor(item);
                return false;
            }
        });


        add_new_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),AddPostActivity.class));
            }
        });

        UsersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


              if (dataSnapshot.exists()){
                  if (dataSnapshot.hasChild("ProfileImage")){
                      String image=dataSnapshot.child("ProfileImage").getValue().toString();
                     Picasso.get().load(image).into(profile_image);



                  }
                  if (dataSnapshot.hasChild("username")){
                      String fullname1=dataSnapshot.child("username").getValue().toString();
                      user_full_name.setText(fullname1);
                  }



              }
              else {

                  Toast.makeText(MainActivity.this, "Please Add picture", Toast.LENGTH_SHORT).show();
              }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curreUser=mAuth.getCurrentUser();
        if (curreUser==null){

            SendUserToLoginActivity();
        }
        else {

            CheckUserExistance();
        }


        FirebaseRecyclerOptions<Post>options=new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(PostRef.orderByChild("counter"),Post.class)
                .build();


        FirebaseRecyclerAdapter<Post,PostViewHolder>adapter=new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull Post model) {

                String postkey=getRef(position).getKey();

                holder.username.setText(model.getFullname());
                holder.Description.setText(model.getDescription());
                holder.Date.setText(model.getDate());
                holder.Time.setText(model.getTime());
                Picasso.get().load(model.getProfileImage()).into(holder.imageView);
                Picasso.get().load(model.getPostImage()).into(holder.post_image_View);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent clickpost_intent=new Intent(getApplicationContext(),ClickPostActivity.class);
                        clickpost_intent.putExtra("Postkey",postkey);
                        startActivity(clickpost_intent);
                    }
                });



                holder.likes_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LikeChecker=true;
                        LikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (LikeChecker=true){

                                    if (dataSnapshot.child(postkey).hasChild(currentUserId)){

                                        LikesRef.child(postkey).child(currentUserId).removeValue();
                                        LikeChecker=false;

                                    }
                                    else{

                                        LikesRef.child(postkey).child(currentUserId).setValue(true);
                                        LikeChecker=false;

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                holder.setLikeButtonstatus(postkey);


                holder.comment_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent comments_intent=new Intent(getApplicationContext(),CommentsActivity.class);
                        comments_intent.putExtra("Postkey",postkey);
                        startActivity(comments_intent);

                    }
                });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout,parent,false);
                PostViewHolder postViewHolder=new PostViewHolder(view);
                return postViewHolder;
            }
        };

        post_list.setAdapter(adapter);
        adapter.startListening();

        updateUserStatus("Online");
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageView;
        TextView username,Date,Time,Description;
        ImageView post_image_View;
        ImageButton likes_btn,comment_btn;
        TextView Display_no_of_likes;

        int LikesCount;
        String currentUserId;
        DatabaseReference LikesRef;


        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.post_profile_image);
            username=itemView.findViewById(R.id.post_user_name);
            Date=itemView.findViewById(R.id.post_date);
            Time=itemView.findViewById(R.id.post_Time);
            Description=itemView.findViewById(R.id.post_description);
            post_image_View=itemView.findViewById(R.id.post_image);
            likes_btn=itemView.findViewById(R.id.like_post_button);
            comment_btn=itemView.findViewById(R.id.comment_button);
            Display_no_of_likes=itemView.findViewById(R.id.display_no_of_likes);

            LikesRef=FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLikeButtonstatus(final String postkey) {

            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postkey).hasChild(currentUserId)){

                        LikesCount= (int) dataSnapshot.child(postkey).getChildrenCount();
                        likes_btn.setImageResource(R.drawable.like);
                        Display_no_of_likes.setText(Integer.toString(LikesCount)+" Likes");
                    }
                    else {
                        LikesCount= (int) dataSnapshot.child(postkey).getChildrenCount();
                        likes_btn.setImageResource(R.drawable.dislike);
                        Display_no_of_likes.setText(Integer.toString(LikesCount)+" Likes");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }


    private void CheckUserExistance() {

        final String current_user_id=mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(current_user_id)){

                    SendUserToSetUpActivity();
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

        UsersRef.child(currentUserId)
                .child("userstate")
                .updateChildren(map);

    }



    private void SendUserToSetUpActivity() {
        Intent intent=new Intent(getApplicationContext(),SetUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void SendUserToLoginActivity() {

        Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelcetor(MenuItem item) {

        switch(item.getItemId()){

            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_profile:
                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                startActivity(new Intent(getApplicationContext(),FriendsActivity.class));
                Toast.makeText(this, "Friends", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Find_friends:
                startActivity(new Intent(getApplicationContext(),FindFriendsActivity.class));
                Toast.makeText(this, "Find Friends", Toast.LENGTH_SHORT).show();
                break;

//            case R.id.nav_message:
//                Toast.makeText(this, "Message", Toast.LENGTH_SHORT).show();
//                break;

            case R.id.nav_settings:
                startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_setup:
                startActivity(new Intent(getApplicationContext(),SetUpActivity.class));
                Toast.makeText(this, "SetUp", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                updateUserStatus("Offline");
                mAuth.signOut();
               SendUserToLoginActivity();
                break;
            case R.id.nav_add_new_post:
                startActivity(new Intent(getApplicationContext(),AddPostActivity.class));
                Toast.makeText(this, "Add new Post", Toast.LENGTH_SHORT).show();
                break;


        }
    }
}
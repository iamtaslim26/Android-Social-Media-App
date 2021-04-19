package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView myposts_list;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference PostRef,LikesRef,UsersRef;

    Boolean LikeChecker=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        mToolbar=findViewById(R.id.myposts_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Posts");

        myposts_list=findViewById(R.id.myposts_lists);
        myposts_list.setLayoutManager(new LinearLayoutManager(this));
        myposts_list.setHasFixedSize(true);


        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        PostRef= FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Post>options=new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(PostRef.orderByChild("uid").startAt(currentUserId).endAt(currentUserId+"\uf8ff"),Post.class)
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
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout,parent,false);
                PostViewHolder postViewHolder=new PostViewHolder(view);
                return postViewHolder;

            }
        };
        myposts_list.setAdapter(adapter);
        adapter.startListening();
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

}
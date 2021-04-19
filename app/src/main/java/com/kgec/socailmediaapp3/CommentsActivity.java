package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView comments_list;
    private EditText write_comments;
    private ImageButton comments_btn;

    private String Postkey,current_user_id,save_current_date,save_current_time;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,PostRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        comments_btn=findViewById(R.id.post_comment_button);
        comments_list=findViewById(R.id.comments_list);
        write_comments=findViewById(R.id.Write_comments);

        Postkey=getIntent().getExtras().get("Postkey").toString();

        comments_list.setLayoutManager(new LinearLayoutManager(this));
        comments_list.setHasFixedSize(true);

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef=FirebaseDatabase.getInstance().getReference().child("Posts").child(Postkey).child("Comments");


        comments_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            String username=dataSnapshot.child("username").getValue().toString();

                            ValidateUserInfo(username);
                            write_comments.setText("");

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comments>options=new FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(PostRef,Comments.class)
                .build();

        FirebaseRecyclerAdapter<Comments,CommentsViewHolder>adapter=new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model) {

                holder.date.setText(model.getDate());
                holder.time.setText(model.getTime());
                holder.commnets.setText(model.getComment());
                holder.UserName.setText(model.getUsername());

            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout,parent,false);
                CommentsViewHolder commentsViewHolder=new CommentsViewHolder(view);
                return commentsViewHolder;
            }
        };

        comments_list.setAdapter(adapter);
        adapter.startListening();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        TextView UserName,date,time,commnets;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);

            UserName=itemView.findViewById(R.id.username_comment);
            date=itemView.findViewById(R.id.comment_Date);
            time=itemView.findViewById(R.id.comment_Time);
            commnets=itemView.findViewById(R.id.comment_text_display);

        }
    }

    private void ValidateUserInfo(String username) {
        String comments=write_comments.getText().toString();

        if (TextUtils.isEmpty(comments)){

            Toast.makeText(this, "Please write your comments. . .. ", Toast.LENGTH_SHORT).show();
        }
        else {

            StoreCommentsintoFirebase(comments,username);
        }

    }

    private void StoreCommentsintoFirebase(String comments,String username) {

        Calendar calFordate=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM,yyyy");
        save_current_date=dateFormat.format(calFordate.getTime());

        Calendar calFortime=Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
        save_current_time=timeFormat.format(calFordate.getTime());

        HashMap<String,Object>comments_map=new HashMap<>();

        comments_map.put("Comment",comments);
        comments_map.put("Date",save_current_date);
        comments_map.put("Time",save_current_time);
        comments_map.put("uid",current_user_id);
        comments_map.put("username",username);

       final String Randomkey=current_user_id+" "+save_current_date+" "+save_current_time;

        PostRef.child(Randomkey).updateChildren(comments_map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(CommentsActivity.this, "Sucessfull. . . .", Toast.LENGTH_SHORT).show();
                }
                else {

                    String e=task.getException().getMessage();
                    Toast.makeText(CommentsActivity.this, "Failed.. . .       "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });





    }
}
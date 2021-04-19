package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
    private EditText search_input;
    private ImageButton search_btn;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private  String current_user_id,search;
    private RecyclerView friends_list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mToolbar=findViewById(R.id.FindFriends_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");



        search_input=findViewById(R.id.Search_friends);
        search_btn=findViewById(R.id.search_button);


        friends_list=findViewById(R.id.FindFriends_list);
        friends_list.setLayoutManager(new LinearLayoutManager(this));
        friends_list.setHasFixedSize(true);


        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");


        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 search=search_input.getText().toString();
                onStart();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<FindFriends>options=new FirebaseRecyclerOptions.Builder<FindFriends>()
                .setQuery(UsersRef.orderByChild("fullname").startAt(search),FindFriends.class)
                .build();


        FirebaseRecyclerAdapter<FindFriends,FindFriendsViewHolder>adapter=new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull FindFriends model) {

                holder.fullName.setText(model.getFullname());
                holder.user_status.setText(model.getStatus());
                holder.userName.setText(model.getUsername());
                Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profile).into(holder.profile_image);



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String visit_user_id=getRef(position).getKey();

                        if (visit_user_id.equals(current_user_id)){

                            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));

                        }
                        else {

                            Intent profileIntent=new Intent(getApplicationContext(),PersonProfileActivity.class);
                            profileIntent.putExtra("visit_user_id",visit_user_id);
                            startActivity(profileIntent);
                        }



                    }
                });


            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_user_display_layout,parent,false);
                FindFriendsViewHolder findFriendsViewHolder=new FindFriendsViewHolder(view);
                return findFriendsViewHolder;
            }
        };
        friends_list.setAdapter(adapter);
        adapter.startListening();


    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_image;
        TextView userName,fullName,user_status;

        public FindFriendsViewHolder(@NonNull View itemView) {
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
}
package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageView post_image;
    private TextView post_description;
    private Button edit_post_btn,delete_post_btn;
    private DatabaseReference ClickPostRef;
    private String postkey;
    private FirebaseAuth mAuth;
    private String current_user_id,description,postImage,post_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mToolbar=findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Post");

        postkey=getIntent().getExtras().get("Postkey").toString();


        post_image=findViewById(R.id.edit_post_image);
        post_description=findViewById(R.id.write_something);
        edit_post_btn=findViewById(R.id.edit_post_button);
        delete_post_btn=findViewById(R.id.delete_post_button);

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        ClickPostRef= FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey);

        edit_post_btn.setVisibility(View.GONE);
        delete_post_btn.setVisibility(View.GONE);

        delete_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeletePost();
            }
        });


        ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                     description=dataSnapshot.child("description").getValue().toString();
                     postImage=dataSnapshot.child("PostImage").getValue().toString();
                     post_user_id=dataSnapshot.child("uid").getValue().toString();

                    post_description.setText(description);
                    Picasso.get().load(postImage).into(post_image);

                    if (current_user_id.equals(post_user_id)){

                        edit_post_btn.setVisibility(View.VISIBLE);
                        delete_post_btn.setVisibility(View.VISIBLE);
                    }

                    edit_post_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            EditPost(description);
                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void EditPost(String description) {

        AlertDialog.Builder builder=new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post: ");
        final EditText inputFiled=new EditText(ClickPostActivity.this);
        inputFiled.setText(description);
        builder.setView(inputFiled);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ClickPostRef.child("description").setValue(inputFiled.getText().toString());
                SendUserToMainActivity();
                Toast.makeText(ClickPostActivity.this, "Post Updated Succesfully. . . ", Toast.LENGTH_SHORT).show();


            }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        Dialog dialog=builder.create();
        dialog.show();
    }



    private void DeletePost() {

        ClickPostRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    SendUserToMainActivity();
                    Toast.makeText(ClickPostActivity.this, "Deleted. . . ", Toast.LENGTH_SHORT).show();
                }
                else {


                    String message=task.getException().getMessage();
                    Toast.makeText(ClickPostActivity.this, "Failed... .     "+message, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void SendUserToMainActivity() {

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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


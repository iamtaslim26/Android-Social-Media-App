package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView Select_post_image;
    private EditText post_Description;
    private Button update_post_btn;

    final static int gallery_pick=1;
    private StorageReference PostImageReferrence;
    private String description,savecurrentDate,saveCurrentTime,PostRandomName;
    String downloadUrl,current_user_id;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,PostRef;
    private ProgressDialog loadingbar;

    long countpost=0;


    Uri  ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mToolbar=findViewById(R.id.add_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");


        Select_post_image=findViewById(R.id.select_image_in_post_activity);
        post_Description=findViewById(R.id.write_post_about_picture);
        update_post_btn=findViewById(R.id.post_activity_update_button);

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();


        PostImageReferrence=FirebaseStorage.getInstance().getReference();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef=FirebaseDatabase.getInstance().getReference().child("Posts");

        loadingbar=new ProgressDialog(this);



        Select_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,gallery_pick);
            }
        });


        Select_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenGallery();
            }
        });

        update_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidatePostInfo();
            }
        });



    }

    private void ValidatePostInfo() {

         description=post_Description.getText().toString();

        if(ImageUri==null){

            Toast.makeText(this, "Please Add a Image", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(description)){

            Toast.makeText(this, "PLease Write the description. . .. . ", Toast.LENGTH_LONG).show();
        }
        else {

            loadingbar.setMessage("Uploading. . . ");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            StoringImageToFirebaseStorage();
        }
    }


    private void StoringImageToFirebaseStorage() {

        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
       savecurrentDate= dateFormat.format(calForDate.getTime());

        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH : mm: ss");
       saveCurrentTime= timeFormat.format(calForTime.getTime());

        PostRandomName=savecurrentDate+" & "+saveCurrentTime;




        StorageReference filepath=PostImageReferrence.child("Post Images").child(ImageUri.getLastPathSegment()+PostRandomName+".jpg");
        filepath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){

                    downloadUrl=task.getResult().getDownloadUrl().toString();

                    Toast.makeText(AddPostActivity.this, "Image is saved into Storage.. . ", Toast.LENGTH_SHORT).show();

                    SavingPostInfromationToDatabase();
                    loadingbar.dismiss();
                }
                else {

                    String e=task.getException().getMessage();
                    Log.e("Hello", "onComplete:  Hello" );
                    Toast.makeText(AddPostActivity.this, "Error,.....   "+e, Toast.LENGTH_LONG).show();
                    loadingbar.dismiss();
                }

            }
        });


    }

    private void SavingPostInfromationToDatabase() {

        PostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    countpost=dataSnapshot.getChildrenCount();
                }else {

                    countpost=0;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String fullname=dataSnapshot.child("fullname").getValue().toString();
                    String profileImage=dataSnapshot.child("ProfileImage").getValue().toString();

                    HashMap<String,Object>postmap=new HashMap<>();
                    postmap.put("fullname",fullname);
                    postmap.put("uid",current_user_id);
                    postmap.put("date",savecurrentDate);
                    postmap.put("time",saveCurrentTime);
                    postmap.put("description",description);
                    postmap.put("PostImage",downloadUrl);
                    postmap.put("ProfileImage",profileImage);
                    postmap.put("PostDescriptionKey",PostRandomName);
                    postmap.put("counter",countpost);


                    PostRef.child(current_user_id+" "+PostRandomName).updateChildren(postmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                SendUserToMainActivity();
                                Toast.makeText(AddPostActivity.this, "Upload Succesfull.....", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                            else {

                                String e=task.getException().getMessage();

                                Toast.makeText(AddPostActivity.this, "Failed......       "+e, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToMainActivity() {

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void OpenGallery() {

        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,gallery_pick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallery_pick && resultCode==RESULT_OK &&data!=null){

            ImageUri=data.getData();
            Select_post_image.setImageURI(ImageUri);
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
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView imageview;
    private EditText settings_bio,username,fullname,country,dob,relatioship_status,gender;
    private Button update_btn;
    private FirebaseAuth mAuth;
    final static int gallery_pick=1;
    private DatabaseReference SettingsRef;
    private StorageReference UsersProfileImageRef;
    private String current_user_id;
    private ProgressDialog loadingbar;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar=findViewById(R.id.settings_Toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Account Settings");

        Initialize();

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        SettingsRef= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        UsersProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Image");

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateUserInfo();
            }
        });

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        SettingsRef.addValueEventListener(new ValueEventListener() {
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

                    Picasso.get().load(PROFILEIMAGE).placeholder(R.drawable.profile_icon).into(imageview);
                    settings_bio.setText(STATUS);
                    country.setText(COUNTRYNAME);
                    relatioship_status.setText(RELATIONSHIP);
                    gender.setText(GENDER);
                    fullname.setText(FULLNAME);
                    username.setText(USERNAME);
                    dob.setText(DOB);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ValidateUserInfo() {

        String biography=settings_bio.getText().toString();
        String userName=username.getText().toString();
        String Fullname=fullname.getText().toString();
        String DateofBirth=dob.getText().toString();
        String countryName=country.getText().toString();
        String relationshipStatus=relatioship_status.getText().toString();
        String Gender=gender.getText().toString();

        if (TextUtils.isEmpty(biography)){

            Toast.makeText(this, "Please write the bio. . . ", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(userName)){

            Toast.makeText(this, "Please write username. . . ", Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.isEmpty(Fullname)){

            Toast.makeText(this, "Please write Fullname. . . ", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(DateofBirth)){

            Toast.makeText(this, "Please write DOB. . . ", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(countryName)){

            Toast.makeText(this, "Please write countryname. . . ", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(relationshipStatus)){

            Toast.makeText(this, "Please write Relationship Status. . . ", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(Gender)){

            Toast.makeText(this, "Please write Gender. . . ", Toast.LENGTH_LONG).show();
        }
        else {

            UpdateAccountInfo(biography,userName,countryName,DateofBirth,Fullname,Gender,relationshipStatus);
        }
    }

    private void UpdateAccountInfo(String biography, String userName, String countryName, String DateofBirth, String Fullname, String Gender, String relationshipStatus) {

        HashMap<String,Object>settingsmap=new HashMap<>();
        settingsmap.put("countryName",countryName);
        settingsmap.put("status",biography);
        settingsmap.put("fullname",Fullname);
        settingsmap.put("username",userName);
        settingsmap.put("relationshipStatus",relationshipStatus);
        settingsmap.put("dob",DateofBirth);
        settingsmap.put("gender",Gender);

        SettingsRef.updateChildren(settingsmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    Toast.makeText(SettingsActivity.this, "Upload Succesful. . .. ", Toast.LENGTH_SHORT).show();
                }else {

                    String e=task.getException().getMessage();
                    Toast.makeText(SettingsActivity.this, "Failed. . . .  .         "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });
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

        if(requestCode==gallery_pick && resultCode==RESULT_OK && data!=null){

           Uri ImageUri=data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK){


                Uri resultUri=result.getUri();

                StorageReference filepath=UsersProfileImageRef.child(current_user_id+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(SettingsActivity.this, "Image is added in Storage. . . ", Toast.LENGTH_SHORT).show();
                            final String downloadUrl=task.getResult().getDownloadUrl().toString();

                            SettingsRef.child("ProfileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        Toast.makeText(SettingsActivity.this, "Image is saved in DataBase. .. ", Toast.LENGTH_SHORT).show();

                                    }else {

                                        String e =task.getException().getMessage();
                                        Toast.makeText(SettingsActivity.this, "Failed. ..     "+e, Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                        }else {
                            String e =task.getException().getMessage();
                            Toast.makeText(SettingsActivity.this, "Failed. ..     "+e, Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }else {

                Toast.makeText(this, "Cropping Image Proccess is Failed. . . . ", Toast.LENGTH_SHORT).show();
            }
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

    private void Initialize() {

        imageview=findViewById(R.id.settings_image);
        settings_bio=findViewById(R.id.settings_bio);
        username=findViewById(R.id.setting_username);
        fullname=findViewById(R.id.settings_fullname);
        country=findViewById(R.id.settinsg_countryname);
        dob=findViewById(R.id.settings_date_of_birth);
        relatioship_status=findViewById(R.id.settings_relationship_status);
        gender=findViewById(R.id.settings_Gender);
        update_btn=findViewById(R.id.settings_update_btn);
    }
}
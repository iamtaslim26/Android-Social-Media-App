package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class SetUpActivity extends AppCompatActivity {

    private EditText user_name,country_name,full_name;
    private Button btn_save;
    private CircleImageView profile_image;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference UsersRef;
    private ProgressDialog loadingbar;
    private StorageReference UsersProfileImageRef;

    final static  int Gallery_Pick=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);

        user_name=findViewById(R.id.set_up_username1);
        country_name=findViewById(R.id.set_up_country_name1);
        full_name=findViewById(R.id.set_up_Full_Name1);
        profile_image=findViewById(R.id.set_up_image);
        btn_save=findViewById(R.id.set_up_save_button1);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UsersProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");

        loadingbar=new ProgressDialog(this);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveAccountSetUpInformation();
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
            }
        });



        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if (dataSnapshot.hasChild("ProfileImage")){

                        String image=dataSnapshot.child("ProfileImage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(profile_image);




                    }
                    else if (dataSnapshot.hasChild("fullname")){

                        String username=dataSnapshot.child("username").getValue().toString();
                        String fullname=dataSnapshot.child("fullname").getValue().toString();
                        String countryname=dataSnapshot.child("countryName").getValue().toString();


                        country_name.setText(countryname);
                        full_name.setText(fullname);

                        user_name.setText(username);

                    }



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null){

           Uri  ImageUri=data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);


        }

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if (resultCode==RESULT_OK){

//                loadingbar.setMessage("Authenticating. . . . ");
//                loadingbar.show();

                Uri resultUri=result.getUri();
                StorageReference filepath=UsersProfileImageRef.child(currentUserId+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(SetUpActivity.this, "Image is stored .is Firebase Storage. .. .", Toast.LENGTH_SHORT).show();

                           final String downloadUrl=task.getResult().getDownloadUrl().toString();


                            UsersRef.child("ProfileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        startActivity(new Intent(getApplicationContext(),SetUpActivity.class));
                                        Toast.makeText(SetUpActivity.this, "Image is saved in Firebase Database. ..  .", Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }
                                    else {
                                        String e =task.getException().getMessage();
                                        Toast.makeText(SetUpActivity.this, "Error. .. ..   "+e, Toast.LENGTH_SHORT).show();
                                        loadingbar.dismiss();
                                    }


                                }

                            });
                        }
                        else {
                                String e=task.getException().getMessage();
                            Toast.makeText(SetUpActivity.this, "Failed. . ..  "+e, Toast.LENGTH_LONG).show();
                        }

                    }
                });


            }else {

                Toast.makeText(this, "Image is not cropped ...Failed", Toast.LENGTH_LONG).show();
            }

        }

    }

    private void SaveAccountSetUpInformation() {

        String username=user_name.getText().toString();
        String fullname=full_name.getText().toString();
        String countryName=country_name.getText().toString();

        if(TextUtils.isEmpty(username)){

            Toast.makeText(this, "Please Enter the UserName. . . ", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname)){

            Toast.makeText(this, "Please Enter the fullname. . . . ", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(countryName)){

            Toast.makeText(this, "Please Enter the country Name. .. ", Toast.LENGTH_SHORT).show();
        }
        else {

            loadingbar.setMessage("Authenticating. . .");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            HashMap<String,Object>usermap=new HashMap<>();
            usermap.put("username",username);
            usermap.put("fullname",fullname);
            usermap.put("countryName",countryName);
            usermap.put("dob","none");
            usermap.put("gender","none");
            usermap.put("status","Hello!I'm using Social MediaApp");
            usermap.put("relationshipStatus","none");

            UsersRef.updateChildren(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ((task.isSuccessful())){

                        SendUserToMainActivity();
                        Toast.makeText(SetUpActivity.this, "Data is saved.....", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                    else {

                        String e=task.getException().getMessage();
                        Toast.makeText(SetUpActivity.this, "Failed        "+e, Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                }
            });

        }
    }

    private void SendUserToMainActivity() {

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
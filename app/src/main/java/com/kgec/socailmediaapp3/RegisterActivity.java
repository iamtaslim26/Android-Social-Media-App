package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEmail,mPassword,mConfirm_password;
    private Button create_account_btn;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail=findViewById(R.id.register_email_id);
        mPassword=findViewById(R.id.register_password);
        mConfirm_password=findViewById(R.id.register_confirm_password);
        create_account_btn=findViewById(R.id.register_account_btn);

        mAuth= FirebaseAuth.getInstance();

        loadingbar=new ProgressDialog(this);




        create_account_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateAccount();
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=mAuth.getCurrentUser();

        if (firebaseUser!=null){

            SendUserToMainActivity();
        }
    }

    private void CreateAccount() {



        String email=mEmail.getText().toString();
        String password=mPassword.getText().toString();
        String c_password=mConfirm_password.getText().toString();

        if(TextUtils.isEmpty(email)){

            Toast.makeText(this, "Please Enter the email.. . . . ", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)){

            Toast.makeText(this, "Please Enter the Password.. . . . ", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(c_password)){

            Toast.makeText(this, "Please Enter the Password.. . . . ", Toast.LENGTH_LONG).show();
        }
//        else if(password!=c_password){
//
//            Toast.makeText(this, "Please Enter the correct password.. . . . ", Toast.LENGTH_LONG).show();
//        }
        else if(password.length()<6){

            Toast.makeText(this, "Password must be more than 6 digits. . .", Toast.LENGTH_LONG).show();
        }
        else {

            loadingbar.setTitle("Creating Account");
            loadingbar.setMessage("Authenticating. . . ");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        SendUserToMainActivity();
                      // SendUserToSetUpActivity();

                        Toast.makeText(RegisterActivity.this, "Account created. .. . ", Toast.LENGTH_SHORT).show();

                        loadingbar.dismiss();
                    }
                    else {
                        String e=task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Failed. . . "+e, Toast.LENGTH_SHORT).show();

                        loadingbar.dismiss();
                    }

                }
            });
        }

    }

    private void SendUserToMainActivity() {


        Intent intent1=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent1);
        finish();
    }

    private void SendUserToSetUpActivity() {

        Intent intent=new Intent(getApplicationContext(),SetUpActivity.class);
        startActivity(intent);
        finish();
    }
}
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail, mPassword;
    private Button mLogin_btn;
    private TextView register_link;
    private ProgressDialog loadingbar;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private TextView forgot_password_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail=findViewById(R.id.login_email_id);
        mPassword=findViewById(R.id.login_password);
        mLogin_btn=findViewById(R.id.login_account_btn);
        register_link=findViewById(R.id.login_text);
        forgot_password_link=findViewById(R.id.forgot_password_link);

        loadingbar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToRegisterActivity();
            }
        });

        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowUserToLogin();
            }
        });

        forgot_password_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),ForgotPasswordActivity.class));
            }
        });
    }

    private void AllowUserToLogin() {

        String email=mEmail.getText().toString();
        String password=mPassword.getText().toString();

        if(TextUtils.isEmpty(email)){

            Toast.makeText(this, "Please Write your email. .. . ", Toast.LENGTH_LONG).show();
        }
        else  if(TextUtils.isEmpty(password)){

            Toast.makeText(this, "Enter your password. .. . ", Toast.LENGTH_LONG).show();
        }
        else {

            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("Authenticating. . . ");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        SendUserToMainActivity();
                        loadingbar.dismiss();
                        Toast.makeText(LoginActivity.this, "Logged in succesfully. . . . ", Toast.LENGTH_SHORT).show();
                    }
                    else {

                        String message=task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Failed. ..     "+message, Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){

            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void SendUserToRegisterActivity() {

        Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intent);


    }
}
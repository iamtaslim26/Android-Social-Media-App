package com.kgec.socailmediaapp3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText reset_email_id;
    private Button reset_password_button;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        reset_email_id=findViewById(R.id.reset_email_id);
        reset_password_button=findViewById(R.id.reset_password);
        mAuth=FirebaseAuth.getInstance();

        reset_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=reset_email_id.getText().toString();

                if(TextUtils.isEmpty(email)){

                    Toast.makeText(ForgotPasswordActivity.this, "Please Enter the email. . .. ", Toast.LENGTH_SHORT).show();
                }
                else {

                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(ForgotPasswordActivity.this, "Check your email. .. . ", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            }
                            else {

                                String e=task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this, "Failed. . ..      "+e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
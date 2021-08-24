package com.example.chau_admin.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chau_admin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;



import  com.example.chau_admin.utils.*;

public class Sign_in extends AppCompatActivity {


    private EditText email,pass;
    private Button sign_in,register;
    private ProgressBar progressBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        progressBar =(ProgressBar) findViewById(R.id.progressbar2);
        email =(EditText) findViewById(R.id.email);
        pass =(EditText) findViewById(R.id.password);
        sign_in =(Button) findViewById(R.id.sign_in);
        register =(Button) findViewById(R.id.register);



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Reg.class));
            }
        });


        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()){
                    progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    progressBar.setVisibility(View.GONE);
                                } else
                                    new util().message("Sign in failed  Email & Password not match", getApplicationContext());
                            }
                        });
            }else
                    new util().message("Pls Enter a valid field",getApplicationContext());
            }
        });
    }
}
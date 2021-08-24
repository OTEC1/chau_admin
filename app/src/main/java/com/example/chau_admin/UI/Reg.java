package com.example.chau_admin.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.chau_admin.R;
import com.example.chau_admin.models.admin_user;
import com.example.chau_admin.utils.util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Reg extends AppCompatActivity {

    private  EditText email,pass;
    private Button btn;
    private ProgressBar progressBar;

    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        firebaseFirestore = FirebaseFirestore.getInstance();

        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        btn = (Button) findViewById(R.id.reg_btn);
        progressBar = (ProgressBar) findViewById(R.id.progressbar1);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty()){
                progressBar.setVisibility(View.VISIBLE);
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    admin_user admin = new admin_user();
                                    admin.setEmail(email.getText().toString());
                                    admin.setUser_id(email.getText().toString().substring(0, email.getText().toString().indexOf("@")));
                                    DocumentReference collectionReference = firebaseFirestore.collection("admins")
                                            .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
                                    collectionReference.set(admin)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        new util().message("Registered Successfully", getApplicationContext());
                                                        startActivity(new Intent(getApplicationContext(), Sign_in.class));
                                                        progressBar.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }else
                new util().message("Pls enter a valid Email and password", getApplicationContext());









            }
        });







    }
}
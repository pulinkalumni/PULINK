package com.example.pulink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp_Page extends AppCompatActivity {

    Button signupbtn;
    String docid;
    EditText email_edittext,password_edittext,conf_password_edittext,name_edittext;

    TextView logintext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        signupbtn=findViewById(R.id.signupbtn);
        logintext=findViewById(R.id.logintext);
        email_edittext=findViewById(R.id.email_edittext);
        password_edittext=findViewById(R.id.password_edittext);
        conf_password_edittext=findViewById(R.id.conf_password_edittext);
        name_edittext=findViewById(R.id.name_edittext);





        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=email_edittext.getText().toString();
                String password=password_edittext.getText().toString();
                String conf_password=conf_password_edittext.getText().toString();
                String name=name_edittext.getText().toString();
                boolean isValidate=validateData(email,password,conf_password,name);
                if(!isValidate)return;
                createaccountInFirebase(email,password,name);


            }
        });

        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp_Page.this, Login_Page.class);
                startActivity(intent);
            }
        });

    }


    void createaccountInFirebase(String email, String password, String name) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp_Page.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // User created successfully, now store name in Firestore
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = FirebaseFirestore.getInstance()
                                    .collection("User")
                                    .document(userId);

                            // Set the name data
                            Map<String, Object> data = new HashMap<>();
                            data.put("name", name);

                            documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Name stored successfully
                                            firebaseAuth.getCurrentUser().sendEmailVerification();
                                            // Sign out user after registration
                                            firebaseAuth.signOut();
                                            // Start the dashboard activity
                                            Intent intent = new Intent(SignUp_Page.this, SignUpDashBoard.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to store name
                                            Toast.makeText(SignUp_Page.this, "Enter Name Please" , Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Registration failed
                            Toast.makeText(SignUp_Page.this,  task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    boolean validateData(String email,String password,String conf_password,String name){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_edittext.setError("Invalid Email");
            return false;
        }
        if (password.length()<6){
            password_edittext.setError("Password Must Be Greater Than 6 words");
            return false;
        }
        if(!password.equals(conf_password)){
            conf_password_edittext.setError("Password not Match");
            return false;
        }
        if(name.equals("")){
            name_edittext.setError("Enter Name ");
            return false;
        }
        return true;
    }




}
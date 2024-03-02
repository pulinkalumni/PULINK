package com.example.pulink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class Login_Page extends AppCompatActivity {

    Button loginbtn;
    EditText email_edittext,password_edittext;
    TextView signuptext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loginbtn=findViewById(R.id.loginbtn);
        signuptext=findViewById(R.id.signuptext);
        email_edittext=findViewById(R.id.email_edittext);
        password_edittext=findViewById(R.id.password_edittext);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=email_edittext.getText().toString();
                String password=password_edittext.getText().toString();


                boolean isValidate=validateData(email,password);

                if(!isValidate)return;

                loginaccountInFirebase(email,password);

            }
        });

        signuptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login_Page.this, SignUp_Page.class);
                startActivity(intent);
            }
        });
    }


    void loginaccountInFirebase(String email,String password){
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Login_Page.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        Intent intent=new Intent(Login_Page.this, Home_Page.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(Login_Page.this,"Email is Not verified !!! Please verify it",Toast.LENGTH_LONG).show();
                        firebaseAuth.getCurrentUser().sendEmailVerification();
                        firebaseAuth.signOut();
                    }
                }
                else{
                    Toast.makeText(Login_Page.this,task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();

                }
            }
        });

    }


    boolean validateData(String email,String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_edittext.setError("Invalid Email");
            return false;
        }

        if(password.length()<6){
            password_edittext.setError("Wrong password");
            return false;
        }
        return true;
    }
}
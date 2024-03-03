package com.example.pulink;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Login_Page extends AppCompatActivity {

    Button loginbtn;
    EditText email_edittext,password_edittext;
    TextView signuptext;
    ImageView google;
    FirebaseAuth auth;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    int RC_SIGN_IN=20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loginbtn=findViewById(R.id.loginbtn);
        signuptext=findViewById(R.id.signuptext);
        email_edittext=findViewById(R.id.email_edittext);
        password_edittext=findViewById(R.id.password_edittext);
        google=findViewById(R.id.google);
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("51839764002-runfu5jnlf1uqjpcjp9820f68f99qk8a.apps.googleusercontent.com").requestEmail().build();
        gsc= GoogleSignIn.getClient(this,gso);
        auth=FirebaseAuth.getInstance();



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


        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinusinggoogle();
            }
        });
    }


    void signinusinggoogle(){
        Intent signIntent=gsc.getSignInIntent();
        startActivityForResult(signIntent,RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount>task=GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseauth(account.getIdToken());

            } catch (Exception e) {
                // Log the localized message
                String errorMessage = e.getLocalizedMessage();
                Log.e("Localized Error", errorMessage);
                Toast.makeText(Login_Page.this, errorMessage, Toast.LENGTH_LONG).show();
            }

        }
    }


    public void firebaseauth(String idToken){
        AuthCredential credential= GoogleAuthProvider.getCredential(idToken,null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=auth.getCurrentUser();
                    // User created successfully, now store name in Firestore
                    String userId = user.getUid();
                    DocumentReference documentReference = FirebaseFirestore.getInstance()
                            .collection("User")
                            .document(userId);

                    // Set the name data
                    Map<String, Object> data = new HashMap<>();
                    data.put("name", user.getDisplayName());

                    documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Intent intent=new Intent(Login_Page.this, Home_Page.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to store name
                                    Toast.makeText(Login_Page.this, "Something Went Wrong" , Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

    }

    void loginaccountInFirebase(String email, String password){
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
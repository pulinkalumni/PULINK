package com.example.pulink;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp_Page extends AppCompatActivity {

    Button signupbtn;
    String docid;
    ImageView google;
    EditText email_edittext,password_edittext,conf_password_edittext,name_edittext;

    TextView logintext;


    // signup using google
    FirebaseAuth firebaseAuth ;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    int RC_SIGN_IN=20;

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
        google=findViewById(R.id.google);

        firebaseAuth = FirebaseAuth.getInstance();
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("51839764002-runfu5jnlf1uqjpcjp9820f68f99qk8a.apps.googleusercontent.com").requestEmail().build();
        gsc= GoogleSignIn.getClient(this,gso);









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
            }
            catch (Exception e){
                Toast.makeText(SignUp_Page.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

            }
        }
    }


    public void firebaseauth(String idToken){
        AuthCredential authCredential= GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user=firebaseAuth.getCurrentUser();
                    String userid=user.getUid();


                    DocumentReference documentReference=FirebaseFirestore.getInstance().collection("User").document(userid);

                    Map<String ,Object> hm=new HashMap<>();
                    hm.put("name",user.getDisplayName());

                    documentReference.set(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent=new Intent(SignUp_Page.this,Home_Page.class);
                            startActivity(intent);
                            finish();
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUp_Page.this,"Something went wrong",Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }

    void createaccountInFirebase(String email, String password, String name) {

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
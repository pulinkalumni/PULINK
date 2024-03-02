package com.example.pulink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen_Page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_page);


        new Handler().postDelayed(new Runnable() {
            final FirebaseUser curruser=FirebaseAuth.getInstance().getCurrentUser();

            @Override
            public void run() {
                if(curruser==null){
                    Intent intent=new Intent(SplashScreen_Page.this,MainActivity.class);
                    startActivity(intent);

                }
                else{
                    Intent intent=new Intent(SplashScreen_Page.this,Home_Page.class);
                    startActivity(intent);

                }
                finish();

            }
        },3000);
    }
}
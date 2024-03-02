package com.example.pulink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignUp_Page extends AppCompatActivity {

    Button signupbtn;
    TextView logintext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        signupbtn=findViewById(R.id.signupbtn);
        logintext=findViewById(R.id.logintext);

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUp_Page.this, SignUpDashBoard.class);
                startActivity(intent);
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


}
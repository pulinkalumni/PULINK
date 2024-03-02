package com.example.pulink;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpDashBoard extends AppCompatActivity {

    Button gotodashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_dash_board);
        gotodashboard=findViewById(R.id.gotodashboard);

        gotodashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpDashBoard.this,Home_Page.class);
                startActivity(intent);
            }
        });
    }
}
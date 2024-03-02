package com.example.pulink;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home_Page extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageView logout_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        bottomNavigationView=findViewById(R.id.bottom_navigation_view);
        logout_icon=findViewById(R.id.logout_icon);

        logout_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Home_Page.this,Login_Page.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.account){
                    loadFrag(new MyAccountPage(),false);
                }
                if(id==R.id.dashboard){
                    loadFrag(new DashBoard_Page_Frag(),false);
                }
                if(id==R.id.post){
                    loadFrag(new DashBoard_Page_Frag(),false);
                }
                return true;
            }
        });


    }


    public void loadFrag(Fragment fragment, boolean abc){
        FragmentManager fmt= getSupportFragmentManager();
        FragmentTransaction ft= fmt.beginTransaction();

        if(abc){
            ft.add(R.id.container1,fragment);
        }
        else{
            ft.replace(R.id.container1,fragment);

        }
        ft.commit();

    }
}
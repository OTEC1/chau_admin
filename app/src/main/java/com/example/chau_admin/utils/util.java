package com.example.chau_admin.utils;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chau_admin.R;
import com.example.chau_admin.UI.MainActivity;
import com.example.chau_admin.UI.Sign_in;
import com.example.chau_admin.UI.Uploader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class util {



    public static final int PICK_IMAGE = 5002;
    public static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 5001;


    public  void  message(String s, Context context){
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }



    //Different Activity Instance
        public AppCompatActivity Compact(AppCompatActivity view) {

            AppCompatActivity myActivity = null;

            if (view instanceof MainActivity)
                myActivity = (MainActivity) view;
            else
            if (view instanceof Uploader)
                myActivity = (Uploader) view;

            assert myActivity != null;
            myActivity.getSupportFragmentManager();

            return myActivity;
        }

    public void bot_nav(BottomNavigationView navigationView,AppCompatActivity view) {
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.homes:
                        Compact(view).startActivity(new Intent(view, Uploader.class));
                        break;

                    case R.id.sign_in:
                        Compact(view).startActivity(new Intent(view, Sign_in.class));
                        break;
                }
                    return false;
            }
        });

    }





}

package com.example.chau_admin.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.chau_admin.R;
import com.example.chau_admin.UI.MainActivity;
import com.example.chau_admin.UI.Sign_in;
import com.example.chau_admin.UI.Upload;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

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

            assert myActivity != null;
            myActivity.getSupportFragmentManager();

            return myActivity;
        }

    public void bot_nav(BottomNavigationView navigationView,AppCompatActivity view,Bundle b) {
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.posted:
                        if(FirebaseAuth.getInstance().getUid()!=null)
                            view.startActivity(new Intent(view.getApplicationContext(),MainActivity.class));
                        else
                            message("Pls Sign in", view.getApplicationContext());
                        break;

                    case R.id.homes:
                        if(FirebaseAuth.getInstance().getUid()!=null)
                          openFragment(new Upload(),b,view);
                        else
                            message("Pls Sign in", view.getApplicationContext());
                        break;


                    case R.id.sign_in:
                        Compact(view).startActivity(new Intent(view, Sign_in.class));
                        break;
                }
                    return false;
            }
        });

    }





    //IMG LOAD PROGRESS
    public void IMG(ImageView poster_value, String model, ProgressBar progressBar) {
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(poster_value.getContext()).load(model)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .apply(requestOptions).into(poster_value);
    }



    public void openFragment(Fragment post, Bundle b, AppCompatActivity activity) {
        FragmentTransaction fragmentManager = activity.getSupportFragmentManager().beginTransaction();
        post.setArguments(b);
        fragmentManager.replace(R.id.framelayout,post);
        fragmentManager.addToBackStack(null);
        fragmentManager.commit();

    }




}

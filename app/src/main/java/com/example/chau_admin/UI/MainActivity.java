package com.example.chau_admin.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.chau_admin.R;
import com.example.chau_admin.Running_Service.Keep_alive;
import com.example.chau_admin.Running_Service.PushReceiver;
import com.example.chau_admin.Running_Service.RegisterUser;
import com.example.chau_admin.utils.util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import me.pushy.sdk.Pushy;

import static com.example.chau_admin.utils.util.READ_STORAGE_PERMISSION_REQUEST_CODE;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView navigationView;
    private Intent intent;
    private Keep_alive keep_alive;
    private Bundle  b = new Bundle();
    private String TAG="MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        NOTIFICATION_LISTER();
        policy();
        checks();
        LOAD_UI();

        if(FirebaseAuth.getInstance().getUid()!=null)
           new util().openFragment(new Post(),b,this);
        else
            new util().message("Pls sign in",getApplicationContext());



    }



    private void LOAD_UI() {
        b.putString("a","");
        new util().bot_nav(navigationView,this,b);
    }


    //Step 1
    public void policy() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Log.d(TAG, " Called ! ");
        }
    }


    //Step 2
    protected void checks() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("OK");
        } else
            request_permission();
    }


    //Step 3
    private void request_permission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This Permission is needed for file sharing")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
                        }
                    }).setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
            } else
            ActivityCompat.requestPermissions(Objects.requireNonNull(this), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);

    }


    //Step 9
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
               new util().message("Permission Granted",this);
            else
                new util().message("Permission Denied",this);

        }

    }



    private void NOTIFICATION_LISTER() {
        keep_alive = new Keep_alive();
        intent = new Intent(this, keep_alive.getClass());
        if (!isServicerunning(keep_alive.getClass()))
            startService(intent);


        if(!Pushy.isRegistered(getApplicationContext()))
            new RegisterUser(this).execute();
        Pushy.listen(this);
    }



    private boolean isServicerunning(Class<? extends Keep_alive> aClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (aClass.getName().equals(serviceInfo.service.getClassName())) {
                Log.d(TAG, " Service Already Running");
                return true;
            }
            Log.d(TAG, " Service Not Running");
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent();
        intent.setAction("restartservice");
        intent.setClass(this, PushReceiver.class);
        intent.putExtra("O","1");
        this.sendBroadcast(intent);
        super.onDestroy();
    }

}
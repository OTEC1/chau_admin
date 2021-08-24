package com.example.chau_admin.Running_Service;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;

import me.pushy.sdk.Pushy;

public class RegisterUser extends AsyncTask<Void,Void,Object> {

    Activity activity;

    String TAG ="RegisterUser";

    public RegisterUser(Activity context) {
        this.activity = context;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        try {
            String deviceToken = Pushy.register(activity.getApplicationContext());
            Log.d(TAG,"Pushy Device token: "+deviceToken);

            new URL("https://com.example.chauvendor/regsiter/device?token="+deviceToken).openConnection();

            return  deviceToken;
        } catch (Exception e) {

            return e;
        }
    }


    @Override
    protected void onPostExecute(Object o) {
        String message;

        if (o instanceof Exception)
            Log.d(TAG, o.toString());
         else
            UPDATE_SERVER(o);
    }

        private void UPDATE_SERVER(Object o) {
        }
}

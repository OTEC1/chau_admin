package com.example.chau_admin.Running_Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import com.example.chau_admin.R;
import com.example.chau_admin.UI.MainActivity;

import me.pushy.sdk.Pushy;

import static com.example.chau_admin.utils.Constants.NOTIFICATION_TITLE;

public class PushReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String notificationText  = "Plain Text";

        if(intent.getStringExtra("message")!=null)
            notificationText = intent.getStringExtra("message");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notify)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(notificationText)
                .setLights(Color.RED, 1000, 1000)
                .setVibrate(new long[]{0, 400, 250, 400})
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));

                Pushy.setNotificationChannel(builder, context);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, builder.build());
                if(intent.getStringExtra("O").equals("1"))
                    notificationManager.cancelAll();
    }
}

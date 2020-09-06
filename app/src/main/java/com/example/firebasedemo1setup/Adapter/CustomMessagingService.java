package com.example.firebasedemo1setup.Adapter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.firebasedemo1setup.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class CustomMessagingService extends FirebaseMessagingService {

    NotificationManager notificationManager;
    Notification notification;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getNotification()!=null)
        {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            generateNotification(title,message);
        }
    }

    public void generateNotification(String title, String message)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(CustomMessagingService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT>=26)
        {
            String channelId = "com.example.firebasedemo1setup";
            String channelName = "FCM";

            NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            assert notificationManager!=null;
            notificationManager.createNotificationChannel(notificationChannel);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(CustomMessagingService.this,channelId);
            notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setColor(Color.BLUE)
                    .setAutoCancel(true)
                    .setTicker("App Update Available")
                    .setContentTitle(title)
                    .setContentText("App Update Available")
                    .setContentIntent(pendingIntent);

            notification = notificationBuilder.build();
        }
        else
        {
            Notification.Builder nb = new Notification.Builder(CustomMessagingService.this);
            nb.setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setTicker(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent);
            nb.build();

            notification = nb.getNotification();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
        }

        if(Build.VERSION.SDK_INT>=26)
        {
            startForeground(0,notification);
        }
        else
        {
            notificationManager.notify(0, notification);
        }
    }
}

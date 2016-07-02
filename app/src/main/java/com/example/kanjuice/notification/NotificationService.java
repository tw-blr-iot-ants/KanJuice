package com.example.kanjuice.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.example.kanjuice.activities.JuiceMenuActivity;

public class NotificationService implements NotificationHandler {


    private final Context context;

    public NotificationService(Context context) {

        this.context = context;
    }

    @Override
    public void sendNotification(Bundle data) {
        Intent intent = new Intent(context,JuiceMenuActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setContentIntent(pendingIntent)
                .setContentText(data.getString("message"))
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

}

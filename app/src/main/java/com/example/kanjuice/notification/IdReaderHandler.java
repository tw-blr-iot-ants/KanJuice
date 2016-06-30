package com.example.kanjuice.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

public class IdReaderHandler extends GcmListenerService implements NotificationHandler {
    private Context context;

    public IdReaderHandler(Context context) {

        this.context = context;
    }

    @Override
    public void sendNotification(Bundle data) {
        Intent intent = null;
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setContentIntent(pendingIntent)
                .setContentText("hello world!")
                .setAutoCancel(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

}

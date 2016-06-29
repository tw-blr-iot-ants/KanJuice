package com.example.kanjuice.gcm;

import android.app.Notification;
import android.os.Bundle;

import com.example.kanjuice.notification.NotificationHandler;
import com.example.kanjuice.notification.NotificationHandlerFactory;
import com.example.kanjuice.util.Logger;
import com.google.android.gms.gcm.GcmListenerService;


public class GCMListenerService extends GcmListenerService {
    private Logger logger;
    private NotificationHandlerFactory notificationHandlerFactory;

    public GCMListenerService() {
        logger = Logger.loggerFor(GCMListenerService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHandlerFactory = new NotificationHandlerFactory(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        logger.d("message is receiving from " + from);
        super.onMessageReceived(from, data);

        String notificationType = data.getString("notificationType");
        if (notificationType == null)
            return;
        NotificationHandler notificationHandler = notificationHandlerFactory.handleFor(String.valueOf(notificationType));
        logger.d("handling messese after receiving this");
        notificationHandler.handleMessage(data);
    }
}


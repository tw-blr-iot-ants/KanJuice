package com.example.kanjuice.gcm;

import com.google.android.gms.gcm.GcmListenerService;


import android.os.Bundle;

import com.example.kanjuice.notification.NotificationHandler;
import com.example.kanjuice.notification.NotificationHandlerFactory;
import com.example.kanjuice.util.Logger;


public class GCMReceiverService extends GcmListenerService {
    private Logger logger;
    private NotificationHandlerFactory notificationHandlerFactory;

    public GCMReceiverService() {
        logger = Logger.loggerFor(GCMReceiverService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logger.d("GCMReceiverService is created");
        notificationHandlerFactory = new NotificationHandlerFactory(this);
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        logger.d("message is receiving from GCM " + from);
        super.onMessageReceived(from, data);

        logger.d("notificationType data is :"+data);

        String notificationType = data.getString("notificationType");
//        if (notificationType == null)
//            return;
        NotificationHandler notificationHandler = notificationHandlerFactory.handleFor(String.valueOf(notificationType));
        logger.d("handling message after receiving this");

        notificationHandler.sendNotification(data);
    }
}


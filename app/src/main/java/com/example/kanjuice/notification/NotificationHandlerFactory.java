package com.example.kanjuice.notification;

import android.content.Context;

public class NotificationHandlerFactory {
    private Context context;

    public NotificationHandlerFactory(Context context) {
        this.context = context;
    }

    public NotificationHandler handleFor(String notificationType) {
       return new NotificationService(context);
    }
}

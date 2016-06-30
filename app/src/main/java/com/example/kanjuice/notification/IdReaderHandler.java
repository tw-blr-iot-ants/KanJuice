package com.example.kanjuice.notification;

import android.content.Context;
import android.os.Bundle;

public class IdReaderHandler implements NotificationHandler {
    private Context context;

    public IdReaderHandler(Context context) {

        this.context = context;
    }

    @Override
    public void handleMessage(Bundle data) {
           String getUserId = data.getString("userId");

    }
}

package com.example.kanjuice.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.example.kanjuice.BuildConfig;
import com.example.kanjuice.activities.UserInputActivity;
import com.example.kanjuice.util.Logger;
import com.google.android.gms.gcm.GcmListenerService;


public class GCMReceiverService extends GcmListenerService {
    public static final String ACTION_RECEIVE_EMP_ID = BuildConfig.APPLICATION_ID + ".RECEIVE_EMP_ID";
    private Logger logger;

    public GCMReceiverService() {
        logger = Logger.loggerFor(GCMReceiverService.class);
    }

    public static final String TAG = "BluetoothReaderService";

    public static final int MSG_DATA_RECEIVED = 102;

    @Override
    public void onCreate() {
        super.onCreate();
        logger.d("GCMReceiverService is created");
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        super.onMessageReceived(from, data);

        logger.d(String.format("Message received from %s: %s", from, message));

        Intent intent = new Intent(ACTION_RECEIVE_EMP_ID);
        intent.putExtra(UserInputActivity.EXTRA_CARD_NUMBER, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
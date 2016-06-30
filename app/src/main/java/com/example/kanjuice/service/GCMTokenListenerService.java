package com.example.kanjuice.service;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GCMTokenListenerService extends InstanceIDListenerService {

    private static final String TAG = "RegIntentService";

    //  [START refresh_token]

    @Override
    public void onTokenRefresh() {
        Intent service = new Intent(this, GCMRegistrationIntentService.class);
        startService(service);
    }

    //  [End refresh_token]
}

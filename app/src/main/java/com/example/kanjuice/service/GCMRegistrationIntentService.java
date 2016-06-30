package com.example.kanjuice.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.example.kanjuice.R;
import com.example.kanjuice.activities.SplashActivity;
import com.example.kanjuice.util.Logger;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class GCMRegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    public static final String RESITRATION_SUCESS = "resistrationSucess";
    public static final String RESITRATION_FAILD = "resistrationFailed";;
    Logger logger = Logger.loggerFor(GCMRegistrationIntentService.class);

    public GCMRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        registerToGCM();
    }

    public void registerToGCM() {
        Intent registrationComplete = new Intent(this, SplashActivity.class);
        String token = null;
        try {
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            logger.d("GCMIntentService token : " + token);
            //;notify to UI that registration complete
            registrationComplete = new Intent(RESITRATION_SUCESS);
            registrationComplete.putExtra("token", token);

        } catch (Exception e) {
            registrationComplete = new Intent(RESITRATION_FAILD);
            logger.e("GCMIntentService reatration failed " + token);
        }

        //send broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}

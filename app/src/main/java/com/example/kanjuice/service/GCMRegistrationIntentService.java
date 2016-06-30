package com.example.kanjuice.service;

import android.app.IntentService;
import android.content.Intent;

import com.example.kanjuice.R;
import com.example.kanjuice.util.Logger;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class GCMRegistrationIntentService extends IntentService{
    private static final String TAG = "RegIntentService";
    private static final String RESITRATION_SUCESS = "resistrationSucess";
    Logger logger = Logger.loggerFor(GCMRegistrationIntentService.class);

    public GCMRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    public void registerToGCM(){
        Intent registrationComplete = null;
        String token = null;
        try {
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            logger.d("GCMIntentService token : " + token);
            //;notify to UI that registration complete
            registrationComplete = new Intent(RESITRATION_SUCESS);
            registrationComplete.putExtra("token",token);

        }catch (Exception e){
            logger.e("GCMIntentService reatration failed "+token);
        }
    }


}

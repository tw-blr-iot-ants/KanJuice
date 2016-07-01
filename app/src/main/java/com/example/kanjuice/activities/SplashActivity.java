package com.example.kanjuice.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.example.kanjuice.R;
import com.example.kanjuice.service.GCMRegistrationIntentService;


public class SplashActivity extends Activity {


    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("AppSharedPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isFirstRun", true)) {
            setContentView(R.layout.activity_splash);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, RegionSelectionActivity.class));
                    finish();
                }
            }, 3000);
            sharedPreferences.edit().putBoolean("isFirstRun", false).commit();
        } else {
            startActivity(new Intent(SplashActivity.this, JuiceMenuActivity.class));
            finish();
        }

    }

}



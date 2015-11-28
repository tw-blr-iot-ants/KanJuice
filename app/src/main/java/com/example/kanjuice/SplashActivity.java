package com.example.kanjuice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.List;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("AppSharedPreferences", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("isFirstRun", true)) {
            setContentView(R.layout.activity_splash);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, RegionSelectionActivity.class));
                    finish();
                }
            }, 3000);
            sharedPreferences.edit().putBoolean("isFirstRun", false).commit();
        }
        else {
            startActivity(new Intent(SplashActivity.this, JuiceMenuActivity.class));
            finish();
        }

    }
}



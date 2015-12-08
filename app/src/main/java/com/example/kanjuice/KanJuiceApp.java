package com.example.kanjuice;


import android.app.Application;

import java.util.concurrent.Executors;

import retrofit.RestAdapter;

public class KanJuiceApp extends Application {

    private static final boolean DEBUG = false;
    private static final String KANJUICE_PROD_SERVER_URL = "http://10.132.127.212:8083";
    private static final String KANJUICE_DEV_SERVER_URL = "http://10.132.126.17:8083";
    private static final String KANJUICE_SERVER_URL = DEBUG ? KANJUICE_DEV_SERVER_URL : KANJUICE_PROD_SERVER_URL;
    private RestAdapter restAdapter;
    private JuiceServer juiceServer;

    @Override
    public void onCreate() {
        super.onCreate();

        setupRestAdapter();
    }

    private void setupRestAdapter() {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(KANJUICE_SERVER_URL)
                .setExecutors(Executors.newFixedThreadPool(3), Executors.newFixedThreadPool(1))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    public JuiceServer getJuiceServer() {
        if (juiceServer == null) {
            juiceServer = restAdapter.create(JuiceServer.class);
        }
        return juiceServer;
    }

}

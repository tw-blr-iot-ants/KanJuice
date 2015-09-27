package com.example.kanjuice;


import android.app.Application;

import java.util.concurrent.Executors;

import retrofit.RestAdapter;

public class KanJuiceApp extends Application {

    private static final String KANJUICE_SERVER_URL = "http://192.168.0.57:8083";
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

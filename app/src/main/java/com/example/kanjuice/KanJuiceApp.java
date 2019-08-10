package com.example.kanjuice;


import android.app.Application;
import android.util.Base64;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;


import static com.example.kanjuice.constants.KanjuiceUrls.*;

@ReportsCrashes(formUri = KANJUICE_SERVER_LOG_URL)
public class KanJuiceApp extends Application {
    private RestAdapter restAdapter;
    private JuiceServer juiceServer;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ACRA.init(this);
        setupRestAdapter();
    }

    private void setupRestAdapter() {
        String basicAuth = "admin:123abc123";
        final String basic = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(KANJUICE_SERVER_URL)
                .setExecutors(Executors.newFixedThreadPool(5), Executors.newFixedThreadPool(3))
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Basic " + basic);
                    }
                })
                .build();
    }

    public JuiceServer getJuiceServer() {
        if (juiceServer == null) {
            juiceServer = restAdapter.create(JuiceServer.class);
        }
        return juiceServer;
    }

}

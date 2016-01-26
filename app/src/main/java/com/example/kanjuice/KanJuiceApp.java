package com.example.kanjuice;


import android.app.Application;
import android.util.Base64;

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

@ReportsCrashes(formUri = "http://10.132.127.212:3000/logs/kanjuice")
public class KanJuiceApp extends Application {

    private static final boolean DEBUG = false;
    private static final String KANJUICE_PROD_SERVER_URL = "http://10.132.127.212:8083";
    private static final String KANJUICE_DEV_SERVER_URL = "http://192.168.0.53:8083";
    private static final String KANJUICE_SERVER_URL = DEBUG ? KANJUICE_DEV_SERVER_URL : KANJUICE_PROD_SERVER_URL;
    private static final String ENCRYPTION_KEY = "abcd1234";
    private RestAdapter restAdapter;
    private JuiceServer juiceServer;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        try {
            setupRestAdapter();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (ShortBufferException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private void setupRestAdapter() throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, ShortBufferException, BadPaddingException, IllegalBlockSizeException {

        String basicAuth = "admin:123abc123";
        byte[] input = basicAuth.getBytes("utf-8");

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] theDigest = md.digest(ENCRYPTION_KEY.getBytes("UTF-8"));
        SecretKeySpec skc = new SecretKeySpec(theDigest, "AES/ECB/PKCS5Padding");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skc);

        byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
        int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);

        final String basic = Base64.encodeToString(cipherText, Base64.DEFAULT);

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(KANJUICE_SERVER_URL)
                .setExecutors(Executors.newFixedThreadPool(5), Executors.newFixedThreadPool(3))
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", basic);
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

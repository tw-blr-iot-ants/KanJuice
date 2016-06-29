package com.example.kanjuice.util;

import android.util.Log;

public class Logger {

    private String LOG_TAG;

    private Logger(Class clazz) {
        this.LOG_TAG = clazz.getSimpleName();
    }

    public static Logger loggerFor(Class clazz) {
        return new Logger(clazz);
    }

    public void d(String message) {
        Log.d(LOG_TAG, message);
    }

    public void e(String message) {
        Log.e(LOG_TAG, message);
    }

    public void e(String message, Throwable throwable) {
        Log.e(LOG_TAG, message, throwable);
    }

    public void i(String message) {
        Log.i(LOG_TAG, message);
    }

    public void d(String message, Throwable throwable) {
        Log.d(LOG_TAG, message, throwable);
    }
}



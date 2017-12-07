package com.example.kanjuice.constants;

public final  class KanjuiceUrls {
    private static final boolean DEBUG = false;
    private static final String KANJUICE_PROD_SERVER_URL = "https://kanjuice.herokuapp.com";
    private static final String KANJUICE_DEV_SERVER_URL = "http://192.168.1.3:8083";

    private static final String KANJUICE_PROD_SERVER_LOG_URL = "https://kanjuice.herokuapp.com/api/log/";
    private static final String KANJUICE_DEV_SERVER_LOG_URL = "http://192.168.1.3:8083/api/log/";

    public static final String KANJUICE_SERVER_URL = DEBUG ? KANJUICE_DEV_SERVER_URL : KANJUICE_PROD_SERVER_URL;
    public static final String KANJUICE_SERVER_LOG_URL = DEBUG ? KANJUICE_DEV_SERVER_LOG_URL : KANJUICE_PROD_SERVER_LOG_URL;
}

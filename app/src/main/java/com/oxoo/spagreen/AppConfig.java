package com.oxoo.spagreen;

public class AppConfig {

    static {
        System.loadLibrary("api_config");
    }

    public static native String getApiServerUrl();
    public static native String getApiKey();
    public static native String getPurchaseCode();
    public static native String getYouTubeApiKey();

    public static final String API_SERVER_URL = getApiServerUrl();
    public static final String API_KEY = getApiKey();
    //copy your terms url from php admin dashboard & paste below
    public static final String TERMS_URL = "https://heplay.dghub.in/terms/";
    public static final String ENVATO_PURCHASE_CODE = getPurchaseCode();
    public static final String YOUTUBE_API_KEY = getYouTubeApiKey();

    //paypal payment status
    public static final boolean PAYPAL_ACCOUNT_LIVE = false;

    // download option for non subscribed user
    public static final boolean ENABLE_DOWNLOAD_TO_ALL = false;

    //enable RTL
    public static boolean ENABLE_RTL = true;

    //youtube video auto play
    public static boolean YOUTUBE_VIDEO_AUTO_PLAY = false;

    //enable external player
    public static final boolean ENABLE_EXTERNAL_PLAYER = false;

    //default theme
    public static boolean DEFAULT_DARK_THEME_ENABLE = true;

    // First, you have to configure firebase to enable facebook, phone and google login
    // facebook authentication
    public static final boolean ENABLE_FACEBOOK_LOGIN = false;

    //Phone authentication
    public static final boolean ENABLE_PHONE_LOGIN = false;

    //Google authentication
    public static final boolean ENABLE_GOOGLE_LOGIN = true;
}

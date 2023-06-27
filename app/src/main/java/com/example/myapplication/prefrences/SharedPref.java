package com.example.myapplication.prefrences;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    private static SharedPreferences mSharedPref;
    public static final String IS_LOGIN = "is_login";
    public static final String USER_NAME = "USER_NAME";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String IS_FORM_SUBMITTED = "IS_FORM_SUBMITTED";
    public static final String CODE = "CODE";
    public static final String USER_ID = "USER_ID";
    public static final String USER_TYPE = "USER_TYPE";
    public static final String TOKEN_REGISTER_TIME = "TOKEN_REGISTER_TIME";
    public static final String PAUSE_TIME = "PAUSE_TIME";
    public static final String PROFILE_IMAGE = "PROFILE_IMAGE";
    public static final String LANGUAGE_TYPE = "LANGUAGE_TYPE";
    public static final String PLAY_SONG_POSITION = "PLAY_SONG_POSITION";
    public static final String IMAGE_URL = "IMAGE_URL";
    private SharedPref() {
    }

    public static void init(Context context) {
        if (mSharedPref == null)
            mSharedPref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static String read(String key, String defValue) {
        return mSharedPref.getString(key, defValue);
    }

    public static void write(String key, String value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static boolean read(String key, boolean defValue) {
        return mSharedPref.getBoolean(key, defValue);
    }

    public static void write(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public static Integer read(String key, int defValue) {
        return mSharedPref.getInt(key, defValue);
    }

    public static void write(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.putInt(key, value).commit();
    }

    public static void clearKey(String key) {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.remove(key);
//        prefsEditor.clear();
        prefsEditor.commit();
    }
    public static void clear() {
        SharedPreferences.Editor prefsEditor = mSharedPref.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }
}
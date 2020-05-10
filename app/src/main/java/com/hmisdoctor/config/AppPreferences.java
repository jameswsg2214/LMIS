package com.hmisdoctor.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {

    private SharedPreferences appSharedPrefs;
    private Editor prefsEditor;
    private static AppPreferences instance;

    @SuppressLint("CommitPrefEdits")
    private AppPreferences(Context context, String Preferncename) {
        this.appSharedPrefs = context.getSharedPreferences(Preferncename,
                Activity.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }
    public static AppPreferences getInstance(Context context, String Preferncename) {
        if (instance == null)
            instance = new AppPreferences(context, Preferncename);
        return instance;
    }

    public String getString(String key) {
        return appSharedPrefs.getString(key, "");
    }
    public boolean getBoolean(String key) {
        return appSharedPrefs.getBoolean(key, false);
    }

    public Integer getInt(String key) {
        return appSharedPrefs.getInt(key, 0);
    }



    public void saveString(String Tag, String text) {
        prefsEditor.putString(Tag, text);
        prefsEditor.apply();
    }

    public void saveBoolean(String Tag, boolean value) {
        prefsEditor.putBoolean(Tag, value);
        prefsEditor.commit();
    }

    public void saveInt(String Tag, int value) {
        prefsEditor.putInt(Tag, value);
        prefsEditor.commit();
    }
    public void clearData() {
        prefsEditor.clear();
        prefsEditor.commit();
    }

}
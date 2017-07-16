package com.rpis.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    private static final String KEY_LAST_SERVER_UID = "last_server_uid";

    public Settings(Context context){
        mContext = context;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public String getLastServerUid(){
        return mPrefs.getString(KEY_LAST_SERVER_UID, null);
    }

    public void setLastServerUid(String uid){
        mPrefs.edit().putString(KEY_LAST_SERVER_UID, uid).commit();
    }

    public static Settings getInstance(){
        return sInstance;
    }

    public static Settings instantiate(Context context){
        sInstance = new Settings(context);
        return getInstance();
    }

    private static Settings sInstance = null;
    private Context mContext = null;
    private SharedPreferences mPrefs = null;
}

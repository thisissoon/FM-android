package com.soon.fm.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private Context ctx;
    private SharedPreferences sharedPref;

    private final static String user_sfm_token = "user_sfm_token";

    private SharedPreferences getPreferences() {
        if (sharedPref == null) {
            sharedPref = ctx.getSharedPreferences("soon_fm", Context.MODE_PRIVATE);
        }
        return sharedPref;
    }

    public PreferencesHelper(Context context) {
        ctx = context;
    }

    public void saveUserApiToken(String token) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(user_sfm_token, token);
        editor.commit();
    }

    public String getUserApiToken() {
        return getPreferences().getString(user_sfm_token, null);
    }

}

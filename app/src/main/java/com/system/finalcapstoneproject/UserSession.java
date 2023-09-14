package com.system.finalcapstoneproject;
import android.content.Context;
import android.content.SharedPreferences;

public class UserSession {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "user_id";
    private SharedPreferences sharedPreferences;

    public UserSession(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setUserID(String userID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userID);
        editor.apply();
    }

    public String getUserID() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }
}

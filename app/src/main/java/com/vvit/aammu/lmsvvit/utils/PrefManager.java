package com.vvit.aammu.lmsvvit.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Ammu on 11-02-2017.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "My_Pref";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_FIRST_TIME_LOGIN = "IsFirstTimeLogin";
    private static final String USER_EMAIL = "email";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.apply();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLogin(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LOGIN, isFirstTime);
        editor.apply();
    }

    public void setUserEmail(String email){
        editor.putString(USER_EMAIL,email);
        editor.apply();
    }
    public String getUserEmail(){
        return pref.getString(USER_EMAIL,"");
    }

    public boolean isFirstTimeLogin() {
        return pref.getBoolean(IS_FIRST_TIME_LOGIN, true);
    }
}


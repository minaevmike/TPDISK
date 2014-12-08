package com.example.mike.tpdisk;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

/**
 * Created by Mike on 29.10.2014.
 */
public class Utils {
    public static final String TOKEN_NAME = "token";
    public static final String EXPIRSE_NAME = "expires";
    public int getExpires(Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return settings.getInt(EXPIRSE_NAME, -1);
    }
    public String getToken(Context context){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return settings.getString(TOKEN_NAME, null);
    }
    public void saveData(Context context, String token, Integer expires){
        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(TOKEN_NAME, token);
        editor.putInt(EXPIRSE_NAME, (int) System.currentTimeMillis() / 1000 + expires);
        editor.apply();
    }

    public String getStringFromCursor(Cursor c, String key){
        return c.getString(c.getColumnIndexOrThrow(key));
    }
}

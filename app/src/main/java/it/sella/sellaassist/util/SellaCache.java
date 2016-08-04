package it.sella.sellaassist.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GodwinRoseSamuel on 30-Jul-16.
 */
public class SellaCache {
    public static final String PREFERENCE = "SellaAssist";

    public static void putCache(String key, String value, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getCache(String key, String defaultValue, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE, context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
}

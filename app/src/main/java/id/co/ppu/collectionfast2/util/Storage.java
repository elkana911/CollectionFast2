package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by Eric on 16-Aug-16.
 */
public class Storage {
    public static final String PREF_APP = "RealmPref";

    public static final String KEY_SERVER_ID = "serverID";
//    public static final String KEY_SERVER_DATE = "server.date";
    public static final String KEY_USER = "user";
    public static final String KEY_USER_LAST_MORNING = "user.lastMorning";

    /*
    public static String getPrefAsString(Context ctx, String key) {
        SharedPreferences sp = ctx.getSharedPreferences("pref", Context.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static void setPrefAsString(Context ctx, String key, String value) {
        SharedPreferences sp = ctx.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }
*/
    public static void savePreference(Context ctx, String key, String value) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply(); //asynkron
    }

    public static void savePreferenceAsInt(Context ctx, String key, int value) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply(); //asynkron
    }

    public static void saveObjPreference(Context ctx, String key, Object value) {

        if (value == null) return;

        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();

        String json = new Gson().toJson(value);
        prefsEditor.putString(key, json);
        prefsEditor.commit();   //synkron
    }

    public static Object getObjPreference(Context ctx, String key, Class cls) {
        String val = null;

        try {
            //Get Reg Token on shared pref
            SharedPreferences userPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode

            Gson gson = new Gson();
            String json = userPrefs.getString(key, "");

            return new Gson().fromJson(json, cls);

        } catch (Exception e) {
            return null;
        }
    }

    public static String getPreference(Context ctx, String key) {
        String val = null;

        try {
            //Get Reg Token on shared pref
            SharedPreferences userPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
            val = userPrefs.getString(key, "");
        } catch (Exception e) {
            return null;
        }
        return val;
    }

    public static int getPreferenceAsInt(Context ctx, String key, int defaultValue) {
        int val;

        try {
            //Get Reg Token on shared pref
            SharedPreferences userPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
            val = userPrefs.getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
        return val;
    }

    public static void clearObjOnSharedPref(Context ctx, String ObjPref) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(ObjPref, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }

}

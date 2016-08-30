package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Eric on 16-Aug-16.
 */
public class Storage {

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

}

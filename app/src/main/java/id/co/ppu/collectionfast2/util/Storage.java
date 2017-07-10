package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import id.co.ppu.collectionfast2.pojo.LoginInfo;
import id.co.ppu.collectionfast2.rest.APIClientBuilder;
import id.co.ppu.collectionfast2.rest.APInterface;
import io.realm.Realm;

/**
 * Created by Eric on 16-Aug-16.
 */
public class Storage {
    public static final String PREF_APP = "RealmPref";

    public static final String KEY_SERVER_ID = "serverID";
    //    public static final String KEY_SERVER_DATE = "server.date";

    // TODO: perlu diganti key_user spy tau mana aja yg versi lama
    public static final String KEY_USERID = "user.id";
    public static final String KEY_PASSWORD = "user.password";
    public static final String KEY_USER_BRANCH_ID = "user.branchId";
    public static final String KEY_USER_BRANCH_NAME = "user.branchName";
    public static final String KEY_USER_EMAIL = "user.email";
    public static final String KEY_USER_JABATAN = "user.jabatan";
    public static final String KEY_USER_NIK = "user.nik";
    public static final String KEY_USER_ADDRESS = "user.alamat";
    public static final String KEY_USER_PHONE = "user.phone";
    public static final String KEY_USER_COLL_TYPE = "user.collType";
    public static final String KEY_USER_BIRTH_PLACE = "user.birthPlace";
    public static final String KEY_USER_BIRTH_DATE = "user.birthDate";
    public static final String KEY_USER_MOBILE_PHONE = "user.mobilePhone";
    public static final String KEY_USER_FULLNAME = "user.fullName";
    public static final String KEY_USER_BUSS_UNIT = "user.bussUnit";
    public static final String KEY_USER_PHOTO_PROFILE_URI = "user.photoProfileUri";


    //    public static final String KEY_USER_LAST_DAY = "user.lastMorning";
//    public static final String KEY_USER_NAME_LAST = "lastUsername";
    public static final String KEY_PASSWORD_REMEMBER = "password.remember";
    public static final String KEY_PASSWORD_LAST = "password.last";

    public static final String KEY_IS_LKPINQUIRY = "isLKPInqury";

    public static final String KEY_LOGIN_DATE = "login.date";
    public static final String KEY_LOGOUT_DATE = "logout.date";
    public static final String KEY_ANDROID_ID = "android.id";
    public static final String KEY_SERVER_DEV_IP = "server.dev.ip";
    public static final String KEY_SERVER_DEV_PORT = "server.dev.port";

    public static final String KEY_POA_DATA_TEMPORARY = "poa.data.tmp";

    /**
     H2U:
     <br>savePref("StringKey", string);
     <br>savePref("IntegerKey", String.valueOf(int));
     <br>savePref("BooleanKey", String.valueOf(boolean));
     * @param key
     * @param value
     */
    public static void savePref(final String key, final String value) {
        if (key == null)
            return;

        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(new LoginInfo(key, value));
                }
            });

        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static void savePrefAsDate(final String key, final Date value) {
        savePref(key, value == null ? null : String.valueOf(value.getTime()));
    }

    public static void savePrefAsBoolean(final String key, final boolean value) {
        savePref(key, String.valueOf(value));
    }

    public static void savePrefAsJson(final String key, final Object value) {
        String json = new Gson().toJson(value);
        savePref(key, json);
    }


    public static String getPref(final String key, final String defValue) {
        if (key == null)
            return null;

        Realm realm = Realm.getDefaultInstance();
        try {
            LoginInfo first = realm.where(LoginInfo.class)
                    .equalTo("key", key).findFirst();

            if (first == null) {
                return defValue;
            }

            return first.getValue() == null ? defValue : first.getValue();

        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static Integer getPrefAsInt(final String key, final int defValue) {
        String ret = getPref(key, String.valueOf(defValue));

        return ret == null ? defValue : new Integer(ret);
    }

    public static Date getPrefAsDate(final String key, final Date defValue) {
        // must in long format
        String ret = getPref(key, defValue == null ? null : String.valueOf(defValue.getTime()));

        return ret == null ? defValue : new Date(Long.parseLong(ret));
    }

    public static boolean getPrefAsBoolean(final String key, final boolean defValue) {
        String ret = getPref(key, String.valueOf(defValue));

        return Boolean.parseBoolean(ret);

    }

    public static Object getPrefAsJson(final String key, Class cls, final String defValue) {
        String ret = getPref(key, defValue);

        return new Gson().fromJson(ret, cls);
    }

    public static File getCompressedImage(Context context, File rawFile, String photoId) throws IOException {
        InputStream in = new FileInputStream(rawFile);
        Bitmap bm2 = BitmapFactory.decodeStream(in);

        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile(photoId + "-", ".jpg", outputDir);

        OutputStream stream = new FileOutputStream(outputFile);
        bm2.compress(Bitmap.CompressFormat.JPEG, 10, stream);
//        bm2.compress(Bitmap.CompressFormat.JPEG, 20, stream);
        stream.close();
        in.close();

        return outputFile;
    }

    public static String getAndroidToken() {
        String androidId = getPref(KEY_ANDROID_ID, null);
        if (TextUtils.isEmpty(androidId)) {
            try {
                // may force close during disabling plugin com.google.gms.google-services
                androidId = FirebaseInstanceId.getInstance().getToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return androidId;
    }

    public static APInterface getAPIInterface() {
        return APIClientBuilder.create(Utility.buildUrl(getPrefAsInt(KEY_SERVER_ID, 0)));
    }

    public static String getLanguageId(Context ctx) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String langId = sharedPrefs.getString("language", "x"); //id / en

        return langId;

    }

    // For to Delete the directory inside list of files and inner Directory
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }


    public static void putNoMediaInto(File dir) {
        File output = new File(dir, ".nomedia");
        try {
            boolean fileCreated = output.createNewFile();
            if (fileCreated) {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

/*
    public static void clearObjOnSharedPref(Context ctx, String ObjPref) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(ObjPref, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }
*/

    /*
        public static String getAndroidToken(Context ctx) {
            String androidId =  Storage.getPreference(ctx.getApplicationContext(), Storage.KEY_ANDROID_ID);
            if (TextUtils.isEmpty(androidId)) {
                androidId = FirebaseInstanceId.getInstance().getToken();
            }

            return androidId;
        }

        public static APInterface getAPIInterface(Context ctx) {
            return
                    APIClientBuilder.create(APInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx.getApplicationContext(), Storage.KEY_SERVER_ID, 0)));
        }
    */

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

    /*
    public static void savePreference(final String key, final Object value) {
        if (key == null)
            return;

        Realm realm = Realm.getDefaultInstance();
        try {
            // when value is object/table, replace !
            if (value != null && value instanceof LoginInfo) {
                realm.beginTransaction();
                realm.delete(LoginInfo.class);
                realm.copyToRealm((LoginInfo) value);
                realm.commitTransaction();

                return;
            } else if (value != null && value instanceof TrnFlagTimestamp) {
                realm.beginTransaction();
                realm.delete(TrnFlagTimestamp.class);
                realm.copyToRealm((TrnFlagTimestamp) value);
                realm.commitTransaction();
                return;
            }

            // otherwise, its LoginInfo as user preference
            if (realm.where(LoginInfo.class).count() > 1)
                throw new RuntimeException("Multiple " + LoginInfo.class.getSimpleName() + " not supported");

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    LoginInfo first = realm.where(LoginInfo.class)
                            .findFirst();

                    // mandatory
                    if (first == null) {
                        first = new LoginInfo();
                        first.setUid(java.util.UUID.randomUUID().toString());
                    }

                    // handle all string key
                    if (key.equals(KEY_SERVER_DEV_IP))
                        first.setServerDevIP((String) value);
                    else if (key.equals(KEY_SERVER_DEV_PORT))
                        first.setServerDevPort((String) value);
                    else if (key.equals(KEY_PASSWORD_REMEMBER))
                        first.setPasswordRemember((String) value);
                    else if (key.equals(KEY_PASSWORD_LAST))
                        first.setPasswordLast((String) value);
                    else if (key.equals(KEY_LOGIN_DATE))
                        first.setLastLogin((Date) value);
                    else if (key.equals(KEY_LOGOUT_DATE))
                        first.setLastLogout((Date) value);
                    else if (key.equals(KEY_ANDROID_ID))
                        first.setAndroidId((String) value);
                    else if (key.equals(KEY_USER)) {

                    }

                    realm.copyToRealmOrUpdate(first);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public static void savePreferenceAsInt(final String key, final int value) {
        if (key == null)
            return;

        Realm realm = Realm.getDefaultInstance();

        try {
            if (realm.where(LoginInfo.class).count() > 1)
                throw new RuntimeException("Multiple " + LoginInfo.class.getSimpleName() + " not supported");

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    LoginInfo first = realm.where(LoginInfo.class)
                            .findFirst();

                    // mandatory
                    if (first == null) {
                        first = new LoginInfo();
                        first.setUid(java.util.UUID.randomUUID().toString());
                    }

                    // handle all numeric key
                    if (key.equals(KEY_SERVER_ID))
                        first.setServerId(value);

                    realm.copyToRealmOrUpdate(first);
                }
            });
        } finally {
            if (realm != null) {
                realm.close();
            }
        }

    }

    public static void savePreferenceAsBoolean(final String key, final boolean value) {
        if (key == null)
            return;

        Realm realm = Realm.getDefaultInstance();

        try {
            if (realm.where(LoginInfo.class).count() > 1)
                throw new RuntimeException("Multiple " + LoginInfo.class.getSimpleName() + " not supported");

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    LoginInfo first = realm.where(LoginInfo.class)
                            .findFirst();

                    // mandatory
                    if (first == null) {
                        first = new LoginInfo();
                        first.setUid(java.util.UUID.randomUUID().toString());
                    }

                    // handle all boolean key
                    if (key.equals(KEY_IS_LKPINQUIRY))
                        first.setLKPInquiry(value);

                    realm.copyToRealmOrUpdate(first);
                }
            });

        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }
*/

        /*
    public static Object getPreference(final String key, final Object defaultValue) {
        if (key == null)
            return null;

        Realm realm = Realm.getDefaultInstance();
        try {

            // TrnFlagTimestamp part
            if (key.equals(KEY_POA_DATA_TEMPORARY)) {
                TrnFlagTimestamp table = realm.where(TrnFlagTimestamp.class).findFirst();
                return table;
            }

            // LoginInfo part
            LoginInfo first = realm.where(LoginInfo.class).findFirst();

            if (first == null)
                return defaultValue;

            Object retValue = null;

            if (key.equals(KEY_LOGIN_DATE))
                retValue = first.getLastLogin();
            else if (key.equals(KEY_LOGOUT_DATE))
                retValue = first.getLastLogout();
            else if (key.equals(KEY_PASSWORD_LAST))
                retValue = first.getPasswordLast();
            else if (key.equals(KEY_PASSWORD_REMEMBER))
                retValue = first.getPasswordRemember();
            else if (key.equals(KEY_SERVER_DEV_IP))
                retValue = first.getServerDevIP();
            else if (key.equals(KEY_SERVER_DEV_PORT))
                retValue = first.getServerDevPort();
//        else if (key.equals(KEY_USER_LAST_DAY))
//            return first.getLastLogin();
            else if (key.equals(KEY_SERVER_ID))
                retValue = first.getServerId();
            else if (key.equals(KEY_USER))
                retValue = first;

            return retValue == null ? defaultValue : retValue;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }
    */
/*
    @Deprecated
    public static synchronized void savePreference(Context ctx, String key, String value) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply(); //asynkron
    }

    @Deprecated
    public static synchronized void savePreferenceAsInt(Context ctx, String key, int value) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply(); //asynkron
    }

    @Deprecated
    public static synchronized void savePreferenceAsBoolean(Context ctx, String key, boolean value) {
        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply(); //asynkron
    }

    @Deprecated
    public static synchronized void saveObjPreference(Context ctx, String key, Object value) {

        if (value == null) return;

        SharedPreferences objPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode
        SharedPreferences.Editor prefsEditor = objPrefs.edit();

        String json = new Gson().toJson(value);
        prefsEditor.putString(key, json);
        prefsEditor.commit();   //synkron
    }

    @Deprecated
    public static SharedPreferences getSharedPreferences(Context ctx) {
        return ctx.getSharedPreferences(PREF_APP, 0);
    }

    @Deprecated
    public static Object getObjPreference(Context ctx, String key, Class cls) {
//        String val = null;

        try {
            //Get Reg Token on shared pref
            SharedPreferences userPrefs = ctx.getSharedPreferences(PREF_APP, 0); // 0 - for private mode

//            Gson gson = new Gson();
            String json = userPrefs.getString(key, "");

            return new Gson().fromJson(json, cls);

        } catch (Exception e) {
            return null;
        }
    }

    @Deprecated
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

    @Deprecated
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
    */


}

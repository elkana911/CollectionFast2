package id.co.ppu.collectionfast2.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;

import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.listener.OnRootListener;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.master.MstMobileSetup;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRepo;
import id.co.ppu.collectionfast2.rest.request.RequestBasic;
import id.co.ppu.collectionfast2.settings.SettingsActivity;
import id.co.ppu.collectionfast2.util.DataUtil;
import id.co.ppu.collectionfast2.util.PoAUtil;
import id.co.ppu.collectionfast2.util.RootUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;

/**
 * Created by Eric on 06-Sep-16.
 */
public class BasicActivity extends AppCompatActivity {

    public Realm realm;
    protected int mSelectedNavMenuIndex = 0;

    public String currentLDVNo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.realm = Realm.getDefaultInstance();

        this.realm.addChangeListener(new RealmChangeListener<Realm>() {
            @Override
            public void onChange(Realm element) {
                onRealmChangeListener();
            }
        });

        changeLocale(Storage.getLanguageId(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }else  if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void resetData() {
//        Storage.savePreference(getApplicationContext(), Storage.KEY_USER, null);

        // clean photo arrival cache
        PoAUtil.cleanPoA();

        DataUtil.resetData(realm);
    }

    protected void onRealmChangeListener() {

    }

    protected RealmQuery<TrnLDVComments> getLDVComments(Realm realm, String ldvNo, String contractNo) {
        /*
        dont use global realm due to error: Realm access from incorrect thread. Realm objects can only be accessed on the thread they were created.
         */
        return realm.where(TrnLDVComments.class)
                .equalTo("pk.ldvNo", ldvNo)
                .equalTo("pk.contractNo", contractNo)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY);
    }

    protected RealmQuery<TrnRepo> getRepo(Realm realm, String contractNo) {
        return realm.where(TrnRepo.class)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY);

    }

    protected RealmQuery<TrnRVColl> getRVColl(Realm realm, String contractNo) {
        return realm.where(TrnRVColl.class)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY);
    }

    protected Date getServerDateFromDB(Realm realm) {
        return realm.where(ServerInfo.class)
                .findFirst()
                .getServerDate();

    }

    protected String getCurrentUserId() {
//        UserData currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);
//        if (currentUser == null)
//            return null;
        return Storage.getPref(Storage.KEY_USERID, null);
//        return currentUser.getUserId();
    }

    protected void fillRequest(String actionName, RequestBasic req) {
        try {
            double[] gps = id.co.ppu.collectionfast2.location.Location.getGPS(this);
            String latitude = String.valueOf(gps[0]);
            String longitude = String.valueOf(gps[1]);
            req.setLatitude(latitude);
            req.setLongitude(longitude);
        } catch (Exception e) {
            e.printStackTrace();

            req.setLatitude("0.0");
            req.setLongitude("0.0");
        }

        req.setActionName(actionName);
        req.setUserId(getCurrentUserId());
        req.setSysInfo(Utility.buildSysInfoAsCsv(this));
    }

    protected boolean isGPSMandatory(Realm realm) {
        MstMobileSetup mobileSetup = realm.where(MstMobileSetup.class)
                .equalTo("key", "DISABLE_GPS_MDTRY")
                .findFirst();

        // default should be true
        if (mobileSetup == null)
            return true;

        return !mobileSetup.getValue1().equals("1");
    }

    /*
    cek Storage.getAPIInterface
        protected APInterface getAPIInterface() {
            return HttpClientBuilder.create(APInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        }
    cek Storage.getAndroidToken
        protected String getAndroidToken() {
            String androidId = Storage.getPreference(getApplicationContext(), Storage.KEY_ANDROID_ID);
            if (TextUtils.isEmpty(androidId)) {
                androidId = FirebaseInstanceId.getInstance().getToken();
            }

            return androidId;
        }
    */
    protected String getDebugInfo() {
        TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        StringBuffer sb = new StringBuffer();
        sb.append("imei=").append(mngr.getDeviceId());
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String serial1 = (String) get.invoke(c, "ril.serialnumber");
            sb.append(",").append("deviceSN=").append(serial1);
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return sb.toString();

    }

    //http://www.sureshjoshi.com/mobile/changing-android-locale-programmatically/
    protected boolean changeLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return true;
    }

    protected void checkRooted(final OnRootListener listener) {
        if (!RootUtil.isDeviceRooted()) {

            if (listener != null)
                listener.asPureDevice();

            return;
        }

        Toast.makeText(this, getString(R.string.error_rooted), Toast.LENGTH_SHORT).show();

        if (!Utility.DEVELOPER_MODE) {

            if (listener != null) {
                resetData();

                listener.asRootDevice();
            }

            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Rooted");

        String msg = "Bypass validation ?\nYES to skip, NO to reset data.";

        alertDialogBuilder.setMessage(msg);

        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (listener != null)
                    listener.asPureDevice();
            }
        });

        alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                resetData();

                if (listener != null)
                    listener.asRootDevice();
            }
        });

        alertDialogBuilder.show();

    }

}

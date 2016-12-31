package id.co.ppu.collectionfast2.component;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;

import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.master.MstMobileSetup;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRepo;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestBasic;
import id.co.ppu.collectionfast2.settings.SettingsActivity;
import id.co.ppu.collectionfast2.util.DataUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by Eric on 06-Sep-16.
 */
public class BasicActivity extends AppCompatActivity {

    protected Realm realm;
    protected int mSelectedNavMenuIndex = 0;

    protected UserData currentUser;

    public String currentLDVNo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.realm = Realm.getDefaultInstance();

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
        DataUtil.resetData(realm);
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

    protected Date getServerDate(Realm realm) {
        return realm.where(ServerInfo.class)
                .findFirst()
                .getServerDate();

    }

    protected String getCurrentUserId() {
        UserData currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);
        if (currentUser == null)
            return null;

        return currentUser.getUserId();
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

    protected ApiInterface getAPIService() {
        return ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

    }

    protected String getAndroidToken() {
        String androidId = Storage.getPreference(getApplicationContext(), Storage.KEY_ANDROID_ID);
        if (TextUtils.isEmpty(androidId)) {
            androidId = FirebaseInstanceId.getInstance().getToken();
        }

        return androidId;
    }
}

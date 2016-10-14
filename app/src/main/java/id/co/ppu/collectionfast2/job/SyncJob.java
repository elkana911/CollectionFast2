package id.co.ppu.collectionfast2.job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import id.co.ppu.collectionfast2.util.NetUtil;

/**
 * Created by Eric on 12-Sep-16.
 */
public class SyncJob extends BroadcastReceiver{
    @Override
    public void onReceive(final Context context, Intent intent) {

        NetUtil.syncLocation(context);
        /*
        try {

            final double[] gps = Location.getGPS(context);

            Log.i("eric.gps", "lat=" + String.valueOf(gps[0]) + ",lng=" + String.valueOf(gps[1]));
            final Date twoDaysAgo = Utility.getTwoDaysAgo(new Date());

            Realm realm = Realm.getDefaultInstance();
            try {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        // delete data two days ago
                        long total = realm.where(TrnCollPos.class).count();

                        RealmResults<TrnCollPos> all = realm.where(TrnCollPos.class).findAll();

//                        long totalTwoDaysAgo = realm.where(TrnCollPos.class).lessThanOrEqualTo("lastUpdate", twoDaysAgo).count();

                        UserData userData = (UserData) Storage.getObjPreference(context, Storage.KEY_USER, UserData.class);

                        TrnCollPos trnCollPos = new TrnCollPos();

                        trnCollPos.setUid(java.util.UUID.randomUUID().toString());

                        trnCollPos.setCollectorId(userData.getUserId());
                        trnCollPos.setLatitude(String.valueOf(gps[0]));
                        trnCollPos.setLongitude(String.valueOf(gps[1]));
                        trnCollPos.setLastUpdate(new Date());
                        realm.copyToRealmOrUpdate(trnCollPos);

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                realm.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}

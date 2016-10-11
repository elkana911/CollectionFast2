package id.co.ppu.collectionfast2.job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Date;

import id.co.ppu.collectionfast2.location.Location;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollPos;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;

/**
 * Created by Eric on 12-Sep-16.
 */
public class SyncJob extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Realm realm = Realm.getDefaultInstance();


        try {

            final double[] gps = Location.getGPS(context);

            Date twoDaysAgo = Utility.getTwoDaysAgo(new Date());

            // delete data two days ago
            long total = realm.where(TrnCollPos.class).count();

            long totalTwoDaysAgo = realm.where(TrnCollPos.class).lessThanOrEqualTo("lastUpdate", twoDaysAgo).count();

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
//            long totalTwoDaysAgo = realm.where(TrnCollPos.class).lessThanOrEqualTo("lastUpdate", twoDaysAgo).findAll().deleteAllFromRealm();

                    TrnCollPos trnCollPos = new TrnCollPos();

//                    trnCollPos.setCollectorId();
                    trnCollPos.setLatitude(String.valueOf(gps[0]));
                    trnCollPos.setLongitude(String.valueOf(gps[1]));
                    trnCollPos.setLastUpdate(new Date());

                    realm.copyToRealm(trnCollPos);
                }
            });

            // just query created today
//            RealmResults<SyncInsert> allInsert = realm.where(TrnRvSyncInsert.class). findAll();
//            RealmResults<SyncUpdate> allUpdate = realm.where(SyncUpdate.class).findAll();
//            realm.where()
/*

            Log.e("eric.job", "sync insert = " + allInsert.size() + ", sync update = " + allUpdate.size());

            // send inserted data
            for (SyncInsert insert : allInsert) {
                String tableName = insert.getTableName();

                if (tableName.equalsIgnoreCase("MC_TRN_COLLECT_ADDR")) {

                    RealmResults<TrnCollectAddr> all = realm.where(TrnCollectAddr.class).equalTo("pk.seqNo", "").equalTo("pk.contractNo", "").findAll();
                }
            }

            // send updated data
            for (SyncUpdate update : allUpdate) {

            }
*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }
}

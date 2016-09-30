package id.co.ppu.collectionfast2.job;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.realm.Realm;

/**
 * Created by Eric on 12-Sep-16.
 */
public class SyncJob extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Realm realm = Realm.getDefaultInstance();


        try {

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

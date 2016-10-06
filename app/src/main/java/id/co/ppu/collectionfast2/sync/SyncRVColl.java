package id.co.ppu.collectionfast2.sync;

import java.util.Date;

import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Eric on 29-Sep-16.
 */

public class SyncRVColl extends ASyncDataHandler {

    public SyncRVColl(Realm realm) {
        super(realm);
    }

    @Override
    protected void initData() {
        list.clear();

        RealmResults<TrnRVColl> _buffer = this.realm.where(TrnRVColl.class)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
                .findAll();

        // pernahkah di sync sebelumnya ?
        for (TrnRVColl obj : _buffer) {

            String key1 = obj.getLdvNo();
            String key2 = obj.getContractNo();

            SyncTrnRVColl sync = realm.where(SyncTrnRVColl.class)
                    .equalTo("ldvNo", key1)
                    .equalTo("contractNo", key2)
                    .findFirst();

            if (sync == null || sync.getSyncedDate() == null) {
                list.add(realm.copyFromRealm(obj));
            }
        }
    }

    @Override
    public void syncData() {
        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<SyncTrnRVColl> syncTrnRVColls = realm.where(SyncTrnRVColl.class)
                        .findAll();

                for (Object obj : list) {

                    TrnRVColl t = (TrnRVColl) obj;
                    String key1 = t.getLdvNo();
                    String key2 = t.getContractNo();

                    SyncTrnRVColl sync = realm.where(SyncTrnRVColl.class)
                            .equalTo("ldvNo", key1)
                            .equalTo("contractNo", key2)
                            .findFirst();
                    if (sync == null) {
                        sync = new SyncTrnRVColl();
                        sync.setLdvNo(key1);
                        sync.setContractNo(key2);
                        sync.setLastUpdateBy(t.getLastupdateBy());
                        sync.setCreatedBy(t.getCreatedBy());
                    }

                    sync.setSyncedDate(new Date());

                    realm.copyToRealm(sync);

                }

            }
        });

    }

}

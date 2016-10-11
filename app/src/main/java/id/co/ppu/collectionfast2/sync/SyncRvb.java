package id.co.ppu.collectionfast2.sync;

import java.util.Date;

import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVB;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Eric on 29-Sep-16.
 */
public class SyncRvb extends ASyncDataHandler {

    public SyncRvb(Realm realm) {
        super(realm);
    }

    @Override
    protected void initData() {
        list.clear();

        RealmResults<TrnRVB> _buffer = this.realm.where(TrnRVB.class)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).findAll();

        for (TrnRVB obj : _buffer) {
            SyncTrnRVB sync = realm.where(SyncTrnRVB.class)
                    .equalTo("rvbNo", obj.getRvbNo())
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
                RealmResults<SyncTrnRVB> syncTrnRVBs = realm.where(SyncTrnRVB.class)
                        .findAll();
                for (Object obj : list) {

                    TrnRVB t = (TrnRVB) obj;

                    String key1 = t.getRvbNo();

                    SyncTrnRVB sync = realm.where(SyncTrnRVB.class)
                            .equalTo("rvbNo", key1)
                            .findFirst();
                    if (sync == null) {
                        sync = new SyncTrnRVB();
                        sync.setRvbNo(key1);
                        sync.setLastUpdateBy(t.getLastupdateBy());
                        sync.setCreatedBy(t.getCreatedBy());

                    }
                    sync.setSyncedDate(new Date());

                    realm.copyToRealmOrUpdate(sync);

                }

            }
        });

    }

}

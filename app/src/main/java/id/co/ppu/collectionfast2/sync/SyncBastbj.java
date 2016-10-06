package id.co.ppu.collectionfast2.sync;

import java.util.Date;

import id.co.ppu.collectionfast2.pojo.sync.SyncTrnBastbj;
import id.co.ppu.collectionfast2.pojo.trn.TrnBastbj;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Eric on 29-Sep-16.
 */

public class SyncBastbj extends ASyncDataHandler {
    public SyncBastbj(Realm realm) {
        super(realm);
    }

    @Override
    protected void initData() {
        list.clear();

        RealmResults<TrnBastbj> _buffer = this.realm.where(TrnBastbj.class)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).findAll();

        for (TrnBastbj obj : _buffer) {
            SyncTrnBastbj sync = realm.where(SyncTrnBastbj.class)
                    .equalTo("bastbjNo", obj.getBastbjNo())
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
                RealmResults<SyncTrnBastbj> syncTrnRVBs = realm.where(SyncTrnBastbj.class)
                        .findAll();

                for (Object obj : list) {

                    TrnBastbj t = (TrnBastbj) obj;

                    String key1 = t.getBastbjNo();

                    SyncTrnBastbj sync = realm.where(SyncTrnBastbj.class)
                            .equalTo("bastbjNo", key1)
                            .findFirst();
                    if (sync == null) {
                        sync = new SyncTrnBastbj();
                        sync.setBastbjNo(key1);
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

package id.co.ppu.collectionfast2.sync;

import java.util.Date;

import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Eric on 29-Sep-16.
 */

public class SyncLdvHeader extends ASyncDataHandler {
    public SyncLdvHeader(Realm realm) {
        super(realm);
    }

    @Override
    protected void initData() {
        list.clear();

        RealmResults<TrnLDVHeader> _buffer = this.realm.where(TrnLDVHeader.class)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).findAll();

        for (TrnLDVHeader obj : _buffer) {
            SyncTrnLDVHeader sync = realm.where(SyncTrnLDVHeader.class)
                    .equalTo("ldvNo", obj.getLdvNo())
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
                RealmResults<SyncTrnLDVHeader> syncTrnRVBs = realm.where(SyncTrnLDVHeader.class)
                        .findAll();
                for (Object obj : list) {

                    TrnLDVHeader t = (TrnLDVHeader) obj;

                    String key1 = t.getLdvNo();

                    SyncTrnLDVHeader sync = realm.where(SyncTrnLDVHeader.class)
                            .equalTo("ldvNo", key1)
                            .findFirst();
                    if (sync == null) {
                        sync = new SyncTrnLDVHeader();
                        sync.setLdvNo(key1);
                        sync.setLastUpdateBy(t.getLastupdateBy());
                        sync.setCreatedBy(t.getCreatedBy());

                    }
                    sync.setSyncedDate(new Date());

                    realm.copyToRealmOrUpdate(sync);

//                    t.setWorkFlag("C");
//                    t.setLastupdateBy(Utility.LAST_UPDATE_BY);
//                    t.setLastupdateTimestamp(new Date());
//                    realm.copyToRealmOrUpdate(t); // hanya di update waktu close batch
                }

            }
        });
    }
}

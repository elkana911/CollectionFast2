package id.co.ppu.collectionfast2.sync;

import java.util.Date;

import id.co.ppu.collectionfast2.pojo.sync.SyncTrnChangeAddr;
import id.co.ppu.collectionfast2.pojo.trn.TrnChangeAddr;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Eric on 29-Sep-16.
 */

public class SyncChangeAddr extends ASyncDataHandler {
    public SyncChangeAddr(Realm realm) {
        super(realm);
    }

    @Override
    protected void initData() {
        list.clear();

        RealmResults<TrnChangeAddr> _buffer = this.realm.where(TrnChangeAddr.class)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).findAll();

        for (TrnChangeAddr obj : _buffer) {
            SyncTrnChangeAddr sync = realm.where(SyncTrnChangeAddr.class)
                    .equalTo("contractNo", obj.getPk().getContractNo())
                    .equalTo("seqNo", obj.getPk().getSeqNo())
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
                RealmResults<SyncTrnChangeAddr> syncTrnRVBs = realm.where(SyncTrnChangeAddr.class)
                        .findAll();
                for (Object obj : list) {

                    TrnChangeAddr t = (TrnChangeAddr) obj;

                    String key1 = t.getPk().getContractNo();
                    Long key2 = t.getPk().getSeqNo();

                    SyncTrnChangeAddr sync = realm.where(SyncTrnChangeAddr.class)
                            .equalTo("contractNo", key1)
                            .equalTo("seqNo", key2)
                            .findFirst();
                    if (sync == null) {
                        sync = new SyncTrnChangeAddr();
                        sync.setContractNo(key1);
                        sync.setSeqNo(key2);
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

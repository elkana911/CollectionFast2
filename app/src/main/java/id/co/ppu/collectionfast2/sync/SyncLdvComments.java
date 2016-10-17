package id.co.ppu.collectionfast2.sync;

import java.util.Date;

import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Eric on 29-Sep-16.
 */

public class SyncLdvComments extends ASyncDataHandler {
    public SyncLdvComments(Realm realm) {
        super(realm);
    }

    @Override
    protected void initData() {
        list.clear();

        RealmResults<TrnLDVComments> _buffer = this.realm.where(TrnLDVComments.class)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).findAll();

        // pernahkah di sync sebelumnya ?
        for (TrnLDVComments obj : _buffer) {

            String key1 = obj.getPk().getLdvNo();
            Long key2 = obj.getPk().getSeqNo();
            String key3 = obj.getPk().getContractNo();

            SyncTrnLDVComments sync = realm.where(SyncTrnLDVComments.class)
                    .equalTo("ldvNo", key1)
                    .equalTo("seqNo", key2)
                    .equalTo("contractNo", key3)
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
                RealmResults<SyncTrnLDVComments> syncTrnRVColls = realm.where(SyncTrnLDVComments.class)
                        .findAll();

                for (Object obj : list) {

                    TrnLDVComments t = (TrnLDVComments) obj;
                    String key1 = t.getPk().getLdvNo();
                    Long key2 = t.getPk().getSeqNo();
                    String key3 = t.getPk().getContractNo();

                    SyncTrnLDVComments sync = realm.where(SyncTrnLDVComments.class)
                            .equalTo("ldvNo", key1)
                            .equalTo("seqNo", key2)
                            .equalTo("contractNo", key3)
                            .findFirst();
                    if (sync == null) {
                        sync = new SyncTrnLDVComments();
                        sync.setLdvNo(key1);
                        sync.setSeqNo(key2);
                        sync.setContractNo(key3);
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

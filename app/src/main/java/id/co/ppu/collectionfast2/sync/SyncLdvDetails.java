package id.co.ppu.collectionfast2.sync;

import java.util.Date;

import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Eric on 29-Sep-16.
 */

public class SyncLdvDetails extends ASyncDataHandler {
    public SyncLdvDetails(Realm realm) {
        super(realm);
    }

    @Override
    protected void initData() {
        list.clear();

        RealmResults<TrnLDVDetails> _buffer = this.realm.where(TrnLDVDetails.class)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
                .findAll();

        // pernahkah di sync sebelumnya ?
        for (TrnLDVDetails obj : _buffer) {

            String key1 = obj.getPk().getLdvNo();
            Long key2 = obj.getPk().getSeqNo();
            String key3 = obj.getContractNo();

            SyncTrnLDVDetails sync = realm.where(SyncTrnLDVDetails.class)
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
                RealmResults<SyncTrnLDVDetails> syncTrnLDVDetailses = realm.where(SyncTrnLDVDetails.class)
                        .findAll();

                for (Object obj : list) {

                    TrnLDVDetails t = (TrnLDVDetails) obj;
                    String key1 = t.getPk().getLdvNo();
                    Long key2 = t.getPk().getSeqNo();
                    String key3 = t.getContractNo();

                    SyncTrnLDVDetails sync = realm.where(SyncTrnLDVDetails.class)
                            .equalTo("ldvNo", key1)
                            .equalTo("seqNo", key2)
                            .equalTo("contractNo", key3)
                            .findFirst();
                    if (sync == null) {
                        sync = new SyncTrnLDVDetails();
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

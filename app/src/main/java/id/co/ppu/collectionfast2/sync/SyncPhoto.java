package id.co.ppu.collectionfast2.sync;

import java.util.Date;

import id.co.ppu.collectionfast2.pojo.sync.SyncFileUpload;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Eric on 29-Sep-16.
 */

public class SyncPhoto extends ASyncDataHandler {
    public SyncPhoto(Realm realm) {
        super(realm);
    }

    @Override
    protected void initData() {
        list.clear();

        RealmResults<TrnPhoto> _buffer = this.realm.where(TrnPhoto.class)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).findAll();

        for (TrnPhoto obj : _buffer) {
            SyncFileUpload sync = realm.where(SyncFileUpload.class)
                    .equalTo("contractNo", obj.getContractNo())
                    .equalTo("collectorId", obj.getCollCode())
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
                RealmResults<SyncFileUpload> all = realm.where(SyncFileUpload.class)
                        .findAll();
                for (Object obj : list) {

                    TrnPhoto t = (TrnPhoto) obj;

                    String key1 = t.getContractNo();
                    String key2 = t.getCollCode();

                    SyncFileUpload sync = realm.where(SyncFileUpload.class)
                            .equalTo("contractNo", key1)
                            .equalTo("collectorId", key2)
                            .findFirst();
                    if (sync == null) {
                        sync = new SyncFileUpload();
                        sync.setContractNo(key1);
                        sync.setPictureId(t.getPhotoId());

                    }
                    sync.setSyncedDate(new Date());

                    realm.copyToRealm(sync);

                }

            }
        });
    }
}

package id.co.ppu.collectionfast2.sync;

import java.util.Date;

import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.TrnRepo;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Eric on 29-Sep-16.
 */

public class SyncRepo extends ASyncDataHandler {
    public SyncRepo(Realm realm) {
        super(realm);
    }

    @Override
    protected void initData() {
        list.clear();

        RealmResults<TrnRepo> _buffer = this.realm.where(TrnRepo.class)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).findAll();

        for (TrnRepo obj : _buffer) {
            SyncTrnRepo sync = realm.where(SyncTrnRepo.class)
                    .equalTo("repoNo", obj.getRepoNo())
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
                RealmResults<SyncTrnRepo> syncTrnRVBs = realm.where(SyncTrnRepo.class)
                        .findAll();

                for (Object obj : list) {

                    TrnRepo t = (TrnRepo) obj;

                    String key1 = t.getRepoNo();
                    String key2 = t.getContractNo();

                    SyncTrnRepo sync = realm.where(SyncTrnRepo.class)
                            .equalTo("repoNo", key1)
                            .equalTo("contractNo", key2)
                            .findFirst();

                    if (sync == null) {
                        sync = new SyncTrnRepo();
                        sync.setRepoNo(key1);
                        sync.setContractNo(key2);
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

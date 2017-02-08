package id.co.ppu.collectionfast2.sync;

import java.util.Set;

import id.co.ppu.collectionfast2.MainbaseActivity;
import id.co.ppu.collectionfast2.pojo.DisplayTrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.DisplayTrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.UploadPicture;
import id.co.ppu.collectionfast2.pojo.trn.HistInstallments;
import id.co.ppu.collectionfast2.pojo.trn.HistInstallmentsPK;
import io.realm.Realm;
import io.realm.RealmModel;

/**
 * Created by Eric on 28-Sep-16.
 */

public abstract class SyncActivity extends MainbaseActivity {
    /**
     * Untuk menghindari kesalahan voucher fisik dengan data di gadget
     */
    protected void syncRVB() {

    }

    /*
        private List<TrnRVB> collectRVB() {
            List<TrnRVB> list = new ArrayList<>();

            RealmResults<TrnRVB> trnRVBs = this.realm.where(TrnRVB.class)
                    .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).findAll();

            for (TrnRVB obj : trnRVBs) {
                SyncTrnRVB syncTrnRVB = realm.where(SyncTrnRVB.class)
                        .equalTo("rvbNo", obj.getRvbNo())
                        .findFirst();

                if (syncTrnRVB == null || syncTrnRVB.getSyncedDate() == null) {
                    list.add(realm.copyFromRealm(obj));
                }
            }

            return list;
        }

        private List<TrnRVColl> collectRVColl() {
            List<TrnRVColl> list = new ArrayList<>();

            RealmResults<TrnRVColl> trnRVColls = this.realm.where(TrnRVColl.class)
                    .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).findAll();

            // pernahkah di sync sebelumnya ?
            for (TrnRVColl obj : trnRVColls) {
                SyncTrnRVColl syncTrnRVColl = realm.where(SyncTrnRVColl.class)
                        .equalTo("ldvNo", obj.getLdvNo())
                        .equalTo("contractNo", obj.getContractNo())
                        .findFirst();

                if (syncTrnRVColl == null || syncTrnRVColl.getSyncedDate() == null) {
                    list.add(realm.copyFromRealm(obj));
                }
            }

            return list;
        }
    */
    protected boolean anyDataToSync() {
//        final SyncLdvHeader syncLdvHeader = new SyncLdvHeader(this.realm);
        final SyncLdvDetails syncLdvDetails = new SyncLdvDetails(this.realm);
        final SyncLdvComments syncLdvComments = new SyncLdvComments(this.realm);
        final SyncRvb syncRvb = new SyncRvb(this.realm);
        final SyncRVColl syncRVColl = new SyncRVColl(this.realm);
        final SyncBastbj syncBastbj = new SyncBastbj(this.realm);
        final SyncRepo syncRepo = new SyncRepo(this.realm);
        final SyncChangeAddr syncChangeAddr = new SyncChangeAddr(this.realm);

        boolean anyDataToSync =
                syncLdvDetails.anyDataToSync()
                        || syncLdvComments.anyDataToSync()
                        || syncRvb.anyDataToSync()
                        || syncRVColl.anyDataToSync()
                        || syncBastbj.anyDataToSync()
                        || syncRepo.anyDataToSync()
                        || syncChangeAddr.anyDataToSync();

        return anyDataToSync;
    }

    protected void clearLKPTables() {
        // TODO: please always check here each time there are database changes
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Set<Class<? extends RealmModel>> tables = realm.getConfiguration().getRealmObjectClasses();
                for (Class<? extends RealmModel> table : tables) {
                    String key = table.getSimpleName();
                    if (key.toLowerCase().startsWith("trn")) {

                        realm.where(table).findAll().deleteAllFromRealm();

                    }

                }
/*
                realm.where(TrnLDVHeader.class).findAll().deleteAllFromRealm();
                realm.where(TrnLDVDetails.class).findAll().deleteAllFromRealm();
                realm.where(TrnLDVDetailsPK.class).findAll().deleteAllFromRealm();
                realm.where(TrnLDVComments.class).findAll().deleteAllFromRealm();
                realm.where(TrnLDVCommentsPK.class).findAll().deleteAllFromRealm();
                realm.where(TrnBastbj.class).findAll().deleteAllFromRealm();
                realm.where(TrnChangeAddr.class).findAll().deleteAllFromRealm();
                realm.where(TrnChangeAddrPK.class).findAll().deleteAllFromRealm();
                realm.where(TrnCollectAddr.class).findAll().deleteAllFromRealm();
                realm.where(TrnCollectAddrPK.class).findAll().deleteAllFromRealm();
                realm.where(TrnContractBuckets.class).findAll().deleteAllFromRealm();
                realm.where(TrnContractBucketsPK.class).findAll().deleteAllFromRealm();
                realm.where(TrnRepo.class).findAll().deleteAllFromRealm();
                realm.where(TrnRVB.class).findAll().deleteAllFromRealm();
                realm.where(TrnRVColl.class).findAll().deleteAllFromRealm();
                realm.where(TrnVehicleInfo.class).findAll().deleteAllFromRealm();
                realm.where(TrnPhoto.class).findAll().deleteAllFromRealm();
                realm.where(TrnErrorLog.class).findAll().deleteAllFromRealm();
                */
                realm.where(UploadPicture.class).findAll().deleteAllFromRealm();
                realm.where(HistInstallments.class).findAll().deleteAllFromRealm();
                realm.where(HistInstallmentsPK.class).findAll().deleteAllFromRealm();

                realm.where(DisplayTrnContractBuckets.class).findAll().deleteAllFromRealm();
                realm.where(DisplayTrnLDVDetails.class).findAll().deleteAllFromRealm();


            }
        });
    }
/*
    protected void syncTransaction(boolean closeBatch) {
        // TODO: important !
        Utility.createAndShowProgressDialog(this, "Close Batch", "Cannot close batch.\nPlease finish all transactions.");

        // update
        long ldvHeader = this.realm.where(TrnLDVHeader.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long ldvDetails = this.realm.where(TrnLDVDetails.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long trnContractBuckets = this.realm.where(TrnContractBuckets.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long trnBastbj = this.realm.where(TrnBastbj.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();

        // insert / update
        long trnLDVComments = this.realm.where(TrnLDVComments.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long trnRepo = this.realm.where(TrnRepo.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();
        long trnChangeAddr = this.realm.where(TrnChangeAddr.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).count();

        if (closeBatch) {

        }

        List<TrnRVColl> trnRVCollList = collectRVColl();
        List<TrnRVB> trnRVBList = collectRVB();


        boolean anyDataToSync = ldvHeader > 0
                || ldvDetails > 0
                || trnRVCollList.size() > 0
                || trnRVBList.size() > 0
                || trnContractBuckets > 0
                || trnLDVComments > 0
                || trnRepo > 0
                || trnBastbj > 0
                || trnChangeAddr > 0;

        if (!anyDataToSync) {
            Toast.makeText(this, "No Data to sync", Toast.LENGTH_SHORT).show();
            return;
        }

        final RequestSyncLKP req = new RequestSyncLKP();

        if (trnRVCollList.size() > 0)
            req.setRvColl(trnRVCollList);

        req.setLdvDetails(this.realm.copyFromRealm(this.realm.where(TrnLDVDetails.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY).findAll()));

        if (trnRVBList.size() > 0)
            req.setRvb(trnRVBList);


        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

        Call<ResponseSync> call = fastService.syncLKP(req);
        call.enqueue(new Callback<ResponseSync>() {
            @Override
            public void onResponse(Call<ResponseSync> call, Response<ResponseSync> response) {
                if (response.isSuccessful()) {
                    final ResponseSync respSync = response.body();

                    if (respSync.getError() != null) {
                        Toast.makeText(SyncActivity.this, "Data Error (" + respSync.getError() + ")\n" + respSync.getError().getErrorDesc(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // TODO: tackle successful sync result here
                    if (respSync.getData() != 1)
                        return;

                    if (req.getRvb() != null && req.getRvb().size() > 0) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                RealmResults<SyncTrnRVB> syncTrnRVBs = realm.where(SyncTrnRVB.class)
                                        .findAll();
                                for (TrnRVB obj : req.getRvb()) {

                                    String rvbNo = obj.getRvbNo();

                                    SyncTrnRVB syncTrnRVB = realm.where(SyncTrnRVB.class)
                                            .equalTo("rvbNo", rvbNo)
                                            .findFirst();
                                    if (syncTrnRVB == null) {
                                        syncTrnRVB = new SyncTrnRVB();
                                        syncTrnRVB.setRvbNo(rvbNo);
                                        syncTrnRVB.setLastUpdateBy(obj.getLastupdateBy());
                                        syncTrnRVB.setCreatedBy(obj.getCreatedBy());

                                    }
                                    syncTrnRVB.setSyncedDate(new Date());

                                    realm.copyToRealmOrUpdate(syncTrnRVB);

                                }

                            }
                        });

                    }

                    if (req.getRvColl() != null && req.getRvColl().size() > 0) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<SyncTrnRVColl> syncTrnRVColls = realm.where(SyncTrnRVColl.class)
                                        .findAll();

                                for (TrnRVColl obj : req.getRvColl()) {

                                    String key1 = obj.getLdvNo();
                                    String key2 = obj.getContractNo();

                                    SyncTrnRVColl syncTrnRVColl = realm.where(SyncTrnRVColl.class)
                                            .equalTo("ldvNo", key1)
                                            .equalTo("contractNo", key2)
                                            .findFirst();
                                    if (syncTrnRVColl == null) {
                                        syncTrnRVColl = new SyncTrnRVColl();
                                        syncTrnRVColl.setLdvNo(key1);
                                        syncTrnRVColl.setContractNo(key2);
                                        syncTrnRVColl.setLastUpdateBy(obj.getLastupdateBy());
                                        syncTrnRVColl.setCreatedBy(obj.getCreatedBy());

                                    }
                                    syncTrnRVColl.setSyncedDate(new Date());

                                    realm.copyToRealm(syncTrnRVColl);

                                }

                            }
                        });
                    }

                    if (req.getLdvDetails() != null && req.getLdvDetails().size() > 0) {

                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseSync> call, Throwable t) {
                Log.e("eric.onFailure", t.getMessage(), t);

                Toast.makeText(SyncActivity.this, "Sync Failed\n" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
*/

    public void clearSyncTables() {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Set<Class<? extends RealmModel>> tables = realm.getConfiguration().getRealmObjectClasses();
                for (Class<? extends RealmModel> table : tables) {
                    String key = table.getSimpleName();
                    if (key.toLowerCase().startsWith("sync")) {

                        realm.where(table).findAll().deleteAllFromRealm();

                    }

                }
                /*
                realm.where(SyncTrnLDVHeader.class).findAll().deleteAllFromRealm();
                realm.where(SyncTrnLDVDetails.class).findAll().deleteAllFromRealm();
                realm.where(SyncTrnLDVComments.class).findAll().deleteAllFromRealm();
                realm.where(SyncTrnBastbj.class).findAll().deleteAllFromRealm();
                realm.where(SyncTrnChangeAddr.class).findAll().deleteAllFromRealm();
                realm.where(SyncTrnRepo.class).findAll().deleteAllFromRealm();
                realm.where(SyncTrnRVB.class).findAll().deleteAllFromRealm();
                realm.where(SyncTrnRVColl.class).findAll().deleteAllFromRealm();
                realm.where(SyncFileUpload.class).findAll().deleteAllFromRealm();
                */

            }
        });
    }


}

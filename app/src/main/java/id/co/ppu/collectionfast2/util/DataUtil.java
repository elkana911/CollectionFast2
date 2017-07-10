package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import id.co.ppu.collectionfast2.exceptions.ContractNotFoundException;
import id.co.ppu.collectionfast2.exceptions.LKPNotFoundException;
import id.co.ppu.collectionfast2.exceptions.NoConnectionException;
import id.co.ppu.collectionfast2.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collectionfast2.pojo.DisplayTrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.LKPData;
import id.co.ppu.collectionfast2.pojo.LoginInfo;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.chat.TrnChatMsg;
import id.co.ppu.collectionfast2.pojo.master.MasterData;
import id.co.ppu.collectionfast2.pojo.master.MstDelqReasons;
import id.co.ppu.collectionfast2.pojo.master.MstLDVClassifications;
import id.co.ppu.collectionfast2.pojo.master.MstLDVParameters;
import id.co.ppu.collectionfast2.pojo.master.MstLDVStatus;
import id.co.ppu.collectionfast2.pojo.master.MstOffices;
import id.co.ppu.collectionfast2.pojo.master.MstParam;
import id.co.ppu.collectionfast2.pojo.master.MstPotensi;
import id.co.ppu.collectionfast2.pojo.master.MstZip;
import id.co.ppu.collectionfast2.pojo.sync.SyncFileUpload;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnBastbj;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVComments;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVB;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVColl;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.HistInstallments;
import id.co.ppu.collectionfast2.pojo.trn.TrnBastbj;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollectAddr;
import id.co.ppu.collectionfast2.pojo.trn.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.TrnVehicleInfo;
import id.co.ppu.collectionfast2.rest.APInterface;
import id.co.ppu.collectionfast2.rest.request.RequestZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMasterData;
import id.co.ppu.collectionfast2.rest.response.ResponseGetZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseServerInfo;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Eric on 23-Sep-16.
 */

public class DataUtil {
    public static final int SYNC_AS_PAYMENT = 1;
    public static final int SYNC_AS_VISIT = 2;
    public static final int SYNC_AS_REPO = 3;

    /*
    public static boolean isSynced(Realm realm, String tableName, String key1, String contractNo, String createdBy) {
        return realm.where(TrnSync.class)
                .equalTo("tblName", tableName)
                .equalTo("key1", key1)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", createdBy)
                .isNotNull("syncedDate")
                .findFirst() != null;
    }
    */
    // generate runningnumber: 1 koletor 1 nomor per hari. maka triknya yyyymmdd<collectorId>
    public static String generateRunningNumber(Date date, String collCode) {
        //yyyyMMdd-runnningnumber2digit
        StringBuilder sb = new StringBuilder();
        sb.append(Utility.convertDateToString(date, "dd"))
                .append(Utility.convertDateToString(date, "MM"))
                .append(Utility.convertDateToString(date, "yyyy"))
//                            .append(Utility.leftPad(runningNumber, 3));
                .append(collCode);

        return sb.toString();
    }

    public static boolean isLDVHeaderTodayOpen(Realm realm, String collCode) {
        final String createdBy = "JOB" + Utility.convertDateToString(new Date(), "yyyyMMdd");

        TrnLDVHeader ldvHeader = realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collCode)
                .equalTo("createdBy", createdBy)
                .findFirst();

        //1. kalo kosong brarti belum ketarik
        if (ldvHeader == null) {
            return false;
        }

        //2. kalo belum closebatch brarti dinyatakan masih open
        return ldvHeader.getCloseBatch() == null || ldvHeader.getCloseBatch().equals("N");

    }

    public static boolean isLDVHeaderAnyOpen(Realm realm, String collCode) {

        TrnLDVHeader ldvHeader = realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collCode)
                .equalTo("closeBatch", "N")
                .or()
                .isNull("closeBatch")
                .findFirst();

        // kalo belum closebatch brarti ada ldvheader yg masih open !
        return ldvHeader != null;

    }

    public static boolean isLDVHeaderClosed(Realm realm, String collCode, Date lkpDate) {
        final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");

        TrnLDVHeader ldvHeader = realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collCode)
                .equalTo("createdBy", createdBy)
                .equalTo("closeBatch", "Y")
                .findFirst();

        return ldvHeader != null;
    }

    /**
     * @param realm
     * @param collCode
     * @return null jika valid
     */
    public static TrnLDVHeader isLDVHeaderValid(Realm realm, String collCode) {
        TrnLDVHeader ldvHeader = realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collCode)
                .notEqualTo("closeBatch", "Y")
                .or()
                .isEmpty("closeBatch")
                .findFirst();

        if (ldvHeader != null) {
            Date deviceDate = new Date();

            if (!Utility.isSameDay(ldvHeader.getCreatedTimestamp(), deviceDate) && deviceDate.after(ldvHeader.getCreatedTimestamp())) {
                return ldvHeader;
            }
        }

        return null;
    }

    public static boolean isMasterTransactionTable(String tableName) {
        return tableName.equalsIgnoreCase(MstDelqReasons.class.getSimpleName())
                || tableName.equalsIgnoreCase(MstLDVClassifications.class.getSimpleName())
                || tableName.equalsIgnoreCase(MstLDVParameters.class.getSimpleName())
                || tableName.equalsIgnoreCase(MstLDVStatus.class.getSimpleName())
                || tableName.equalsIgnoreCase(MstParam.class.getSimpleName())
                || tableName.equalsIgnoreCase(MstPotensi.class.getSimpleName())
                ;
    }

    /**
     * Memastikan hanya table master utk transaksi yang akan ditarik
     *
     * @param ctx
     * @param realm
     * @return
     * @see #isMasterTransactionTable(String)
     */
    public static boolean isMasterDataDownloaded(Context ctx, Realm realm) {

        ServerInfo serverInfo = realm.where(ServerInfo.class).findFirst();
        UserConfig userConfig = realm.where(UserConfig.class).findFirst();

        boolean masterIsEmpty = false;
        Set<Class<? extends RealmModel>> tables = realm.getConfiguration().getRealmObjectClasses();
        for (Class<? extends RealmModel> table : tables) {
            String key = table.getSimpleName();
            if (!key.toLowerCase().startsWith("mst"))
                continue;

            // skip juga yg bukan table master transaksi, jk bukan tdk akan ditarik
            if (!isMasterTransactionTable(key))
                continue;

            long count = realm.where(table).count();

            if (count < 1) {
                masterIsEmpty = true;
                break;
            }
        }

//        long count = realm.where(MstParam.class).count();

        if (serverInfo != null) {
            if (!masterIsEmpty && Utility.isSameDay(new Date(), serverInfo.getServerDate())) {
                return true;
            }
        }

        // is master data complete  ?

        // skip master data
        if (!masterIsEmpty) {
//        if (count > 0) {
            return true;
        }

        if (!NetUtil.isConnected(ctx)) {
            if (DemoUtil.isDemo()) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        MasterData data = DemoUtil.buildMasterData();

                        saveMastersToDB(realm, data);
                    }
                });

                return true;
            }else
                return false;
        }

        try {
            retrieveMasterFromServerBackground(realm, ctx);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public static void retrieveMasterFromServerBackground(Realm realm, Context ctx) throws Exception {

        APInterface api = Storage.getAPIInterface();
//        APInterface api =
//                APIClientBuilder.create(APInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        Call<ResponseGetMasterData> callMasterData = api.getMasterData();

        // hrs async karena error networkonmainthreadexception
        callMasterData.enqueue(new Callback<ResponseGetMasterData>() {
            @Override
            public void onResponse(Call<ResponseGetMasterData> call, Response<ResponseGetMasterData> response) {
                if (response.isSuccessful()) {
                    final ResponseGetMasterData respGetMasterData = response.body();

                    if (respGetMasterData.getError() != null) {

                    } else {
                        // save db here, tp krn async disarankan buat instance baru
                        Realm _realm = Realm.getDefaultInstance();
                        // save db here
                        try {
                            _realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    saveMastersToDB(bgRealm, respGetMasterData.getData());
                                }
                            });

                        } finally {
                            if (_realm != null) {
                                _realm.close();
                            }
                        }
                    }
                } else {
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    try {
                        Log.e("eric.unSuccessful", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetMasterData> call, Throwable t) {
                Log.e("eric.onFailure", t.getMessage(), t);
            }
        });

    }

    public static void retrieveZipCodeFromServerBackground(Realm realm, Context ctx) throws Exception {

        APInterface api = Storage.getAPIInterface();
//        APInterface api =
//                APIClientBuilder.create(APInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        RequestZipCode requestZipCode = new RequestZipCode();
//        request.setZipCode("11058");

        Call<ResponseGetZipCode> callZipCode = api.getZipCode(requestZipCode);

        callZipCode.enqueue(new Callback<ResponseGetZipCode>() {
            @Override
            public void onResponse(Call<ResponseGetZipCode> call, Response<ResponseGetZipCode> response) {
                if (response.isSuccessful()) {
                    final ResponseGetZipCode respGetZipCode = response.body();

                    if (respGetZipCode.getError() != null) {

                    } else {
                        // save db here, tp krn async disarankan buat instance baru
                        Realm _realm = Realm.getDefaultInstance();

                        try {
                            _realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    // insert
                                    long count = bgRealm.where(MstZip.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstZip.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetZipCode.getData());
                                }
                            });
                        } finally {
                            if (_realm != null) {
                                _realm.close();
                            }
                        }
                    }
                } else {
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    try {
                        Log.e("eric.unSuccessful", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseGetZipCode> call, Throwable t) {
                Log.e("eric.onFailure", t.getMessage(), t);
            }
        });

    }

    public static void retrieveServerInfo(final String collCode, final Realm realm, final Context ctx, final OnPostRetrieveServerInfo listener) throws Exception {

        if (!NetUtil.isConnected(ctx)) {
            if (DemoUtil.isDemo()) {
                final ServerInfo si = new ServerInfo();
                si.setServerDate(new Date());

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(ServerInfo.class);
                        realm.copyToRealm(si);
                    }
                });

                if (listener != null)
                    listener.onSuccess(si);
            } else if (listener != null)
                listener.onFailure(new NoConnectionException());

            return;
        }

        APInterface api = Storage.getAPIInterface();
//        APInterface api =
//                APIClientBuilder.create(APInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        Call<ResponseServerInfo> call = api.getServerInfo();
        call.enqueue(new Callback<ResponseServerInfo>() {
            @Override
            public void onResponse(Call<ResponseServerInfo> call, Response<ResponseServerInfo> response) {
                if (response.isSuccessful()) {
                    final ResponseServerInfo responseServerInfo = response.body();

                    if (responseServerInfo.getError() == null) {
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.delete(ServerInfo.class);
                                realm.copyToRealm(responseServerInfo.getData());
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                if (listener != null)
                                    listener.onSuccess(responseServerInfo.getData());
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                if (error != null)
                                    NetUtil.syncLogError(ctx, realm, collCode, "retrieveServerInfo", error.getMessage(), null);

                                Toast.makeText(ctx, "Database Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseServerInfo> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);

//                throw new RuntimeException("Failure retrieve server information");
            }
        });

    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     *
     * @param realm
     * @param ldvNo
     * @param contractNo
     * @return < 1 if not synced. 1 synced as {@link #SYNC_AS_PAYMENT}, 2 synced as {@link #SYNC_AS_VISIT} visit, 4 synced as {@link #SYNC_AS_REPO}
     */
    public static int isLKPSynced(Realm realm, String ldvNo, String contractNo) {
        RealmResults<SyncTrnRVColl> trnSync = realm.where(SyncTrnRVColl.class)
                .equalTo("ldvNo", ldvNo)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .isNotNull("syncedDate")
                .findAll();

        if (trnSync.size() > 0)
            return SYNC_AS_PAYMENT;

        RealmResults<SyncTrnLDVComments> trnSyncLDVComments = realm.where(SyncTrnLDVComments.class)
                .equalTo("ldvNo", ldvNo)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .isNotNull("syncedDate")
                .findAll();
        if (trnSyncLDVComments.size() > 0)
            return SYNC_AS_VISIT;

        RealmResults<SyncTrnRepo> trnSyncRepo = realm.where(SyncTrnRepo.class)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .isNotNull("syncedDate")
                .findAll();
        if (trnSyncRepo.size() > 0)
            return SYNC_AS_REPO;

        return 0;   // no sync

    }

    public static int isLKPSynced(Realm realm, TrnLDVDetails dtl) {
        return isLKPSynced(realm, dtl.getPk().getLdvNo(), dtl.getContractNo());
    }

    public static void resetData(Realm realm) {

        if (realm != null) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.deleteAll();
                }
            });
        }

    }

    public static void resetData() {
        Realm r = Realm.getDefaultInstance();
        try {
            resetData(r);
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }

    public static RealmQuery<TrnChatMsg> queryChatMsg(Realm r, String user1, String user2) {

        RealmQuery<TrnChatMsg> group = r.where(TrnChatMsg.class)
                .beginGroup()
                .equalTo("fromCollCode", user1)
                .equalTo("toCollCode", user2)
                .endGroup()
                .or()
                .beginGroup()
                .equalTo("fromCollCode", user2)
                .equalTo("toCollCode", user1)
                .endGroup();

        return group;

    }

    // store db without commit/rollback. you need to prepare it first
    public static void saveMastersToDB(Realm bgRealm, MasterData data) {
        // insert ldp classifications
        long count = bgRealm.where(MstLDVClassifications.class).count();
        if (count > 0) {
            bgRealm.delete(MstLDVClassifications.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getLdpClassifications());

        // insert param
        count = bgRealm.where(MstParam.class).count();
        if (count > 0) {
            bgRealm.delete(MstParam.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getParams());

        // insert ldp status
        count = bgRealm.where(MstLDVStatus.class).count();
        if (count > 0) {
            bgRealm.delete(MstLDVStatus.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getLdpStatus());

        // insert ldp parameter
        count = bgRealm.where(MstLDVParameters.class).count();
        if (count > 0) {
            bgRealm.delete(MstLDVParameters.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getLdpParameters());

        // insert delq reasons
        count = bgRealm.where(MstDelqReasons.class).count();
        if (count > 0) {
            bgRealm.delete(MstDelqReasons.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getDelqReasons());

        // insert office
        count = bgRealm.where(MstOffices.class).count();
        if (count > 0) {
            bgRealm.delete(MstOffices.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getOffices());

        // insert potensi
        count = bgRealm.where(MstPotensi.class).count();
        if (count > 0) {
            bgRealm.delete(MstPotensi.class);
        }
        bgRealm.copyToRealmOrUpdate(data.getPotensi());

    }

    // store db without commit/rollback. you need to prepare it first
    public static void saveLKPToDB(Realm bgRealm, String collectorCode, String createdBy, LKPData data) {
        // insert header
        // wipe existing tables?
        boolean d = bgRealm.where(TrnLDVHeader.class)
                .equalTo("collCode", collectorCode)
                .equalTo(Utility.COLUMN_CREATED_BY, createdBy)
                .findAll()
                .deleteAllFromRealm();

        bgRealm.copyToRealm(data.getHeader());

        // refresh synctable
        TrnLDVHeader _header = data.getHeader();
        if (_header.getFlagDone() != null && _header.getFlagDone().equalsIgnoreCase("Y")) {
            SyncTrnLDVHeader sync = bgRealm.where(SyncTrnLDVHeader.class)
                    .equalTo("ldvNo", _header.getLdvNo()).findFirst();

            if (sync == null) {
                sync = new SyncTrnLDVHeader();
            }
            sync.setLdvNo(_header.getLdvNo());
            sync.setLastUpdateBy(_header.getLastupdateBy());
            sync.setCreatedBy(_header.getCreatedBy());

            sync.setSyncedDate(_header.getDateDone());

            bgRealm.copyToRealm(sync);
        }

        // insert address
        d = bgRealm.where(TrnCollectAddr.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
        bgRealm.copyToRealm(data.getAddress());

        // insert rvb
        d = bgRealm.where(TrnRVB.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
        bgRealm.copyToRealm(data.getRvb());

        for (TrnRVB _rvb : data.getRvb()) {
            if (_rvb == null)
                continue;

            if (_rvb.getFlagDone() != null && _rvb.getFlagDone().equalsIgnoreCase("Y")) {
                SyncTrnRVB s = bgRealm.where(SyncTrnRVB.class)
                        .equalTo("rvbNo", _rvb.getRvbNo()).findFirst();

                if (s == null) {
                    s = new SyncTrnRVB();
                }
                s.setRvbNo(_rvb.getRvbNo());
                s.setLastUpdateBy(_rvb.getLastupdateBy());
                s.setCreatedBy(_rvb.getCreatedBy());
                s.setSyncedDate(_rvb.getDateDone());

                bgRealm.copyToRealm(s);
            }
        }


        // insert bastbj
        d = bgRealm.where(TrnBastbj.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
        bgRealm.copyToRealm(data.getBastbj());

        for (TrnBastbj _bastbj : data.getBastbj()) {

            if (_bastbj == null)
                continue;

            if (_bastbj.getFlagDone() != null && _bastbj.getFlagDone().equalsIgnoreCase("Y")) {
                SyncTrnBastbj sync = bgRealm.where(SyncTrnBastbj.class)
                        .equalTo("bastbjNo", _bastbj.getBastbjNo()).findFirst();

                if (sync == null) {
                    sync = new SyncTrnBastbj();
                }
                sync.setBastbjNo(_bastbj.getBastbjNo());
                sync.setLastUpdateBy(_bastbj.getLastupdateBy());
                sync.setCreatedBy(_bastbj.getCreatedBy());
                sync.setSyncedDate(_bastbj.getDateDone());

                bgRealm.copyToRealm(sync);
            }
        }

        // insert vehicle info
        d = bgRealm.where(TrnVehicleInfo.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
        bgRealm.copyToRealmOrUpdate(data.getVehicleInfo());

        // insert history installments
        d = bgRealm.where(HistInstallments.class).findAll().deleteAllFromRealm();
        bgRealm.copyToRealmOrUpdate(data.getHistoryInstallments());

        // insert buckets
        d = bgRealm.where(TrnContractBuckets.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();
        bgRealm.copyToRealm(data.getBuckets());

        // insert details
        d = bgRealm.where(TrnLDVDetails.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();

        // link trxCollectAddr
        for (TrnLDVDetails ldvDetails : data.getDetails()) {

            TrnCollectAddr _address = null;
            for (TrnCollectAddr addr : data.getAddress()) {
                if (addr.getPk().getContractNo().equalsIgnoreCase(ldvDetails.getContractNo())) {
                    _address = addr;
                    break;
                }
            }
            ldvDetails.setAddress(_address);
            bgRealm.copyToRealm(ldvDetails);
        }

        for (TrnLDVDetails _ldvDtl : data.getDetails()) {
            if (_ldvDtl == null)
                continue;

            if (_ldvDtl.getFlagDone() != null && _ldvDtl.getFlagDone().equalsIgnoreCase("Y")) {
                SyncTrnLDVDetails sync = bgRealm.where(SyncTrnLDVDetails.class)
                        .equalTo("ldvNo", _ldvDtl.getPk().getLdvNo())
                        .equalTo("seqNo", _ldvDtl.getPk().getSeqNo())
                        .equalTo("contractNo", _ldvDtl.getContractNo())
                        .findFirst();

                if (sync == null) {
                    sync = new SyncTrnLDVDetails();
                }
                sync.setLdvNo(_ldvDtl.getPk().getLdvNo());
                sync.setSeqNo(_ldvDtl.getPk().getSeqNo());
                sync.setContractNo(_ldvDtl.getContractNo());
                sync.setLastUpdateBy(_ldvDtl.getLastupdateBy());
                sync.setCreatedBy(_ldvDtl.getCreatedBy());
                sync.setSyncedDate(_ldvDtl.getDateDone());

                bgRealm.copyToRealm(sync);
            }
        }

        // for faster show, but must be load after ldvdetails
        bgRealm.delete(DisplayTrnContractBuckets.class);
        for (TrnContractBuckets obj : data.getBuckets()) {

            DisplayTrnContractBuckets displayTrnContractBuckets = bgRealm.createObject(DisplayTrnContractBuckets.class);

            displayTrnContractBuckets.setContractNo(obj.getPk().getContractNo());
            displayTrnContractBuckets.setCreatedBy(obj.getCreatedBy());
            displayTrnContractBuckets.setCustName(obj.getCustName());

            bgRealm.copyToRealm(displayTrnContractBuckets);
        }

        //repo inserted by MOBCOL
        d = bgRealm.where(TrnRepo.class).equalTo(Utility.COLUMN_CREATED_BY, Utility.LAST_UPDATE_BY).findAll().deleteAllFromRealm();
        bgRealm.copyToRealmOrUpdate(data.getRepo());

        for (TrnRepo _obj : data.getRepo()) {
            if (_obj == null)
                continue;

            if (_obj.getFlagDone() != null && _obj.getFlagDone().equalsIgnoreCase("Y")) {
                SyncTrnRepo sync = bgRealm.where(SyncTrnRepo.class)
                        .equalTo("repoNo", _obj.getRepoNo())
                        .equalTo("contractNo", _obj.getContractNo())
                        .findFirst();

                if (sync == null) {
                    sync = new SyncTrnRepo();
                }
                sync.setRepoNo(_obj.getRepoNo());
                sync.setContractNo(_obj.getContractNo());
                sync.setLastUpdateBy(_obj.getLastupdateBy());
                sync.setCreatedBy(_obj.getCreatedBy());
                sync.setSyncedDate(_obj.getDateDone());


                bgRealm.copyToRealm(sync);
            }
        }
        //changeaddr ?

        //ldvcomment ?
        d = bgRealm.where(TrnLDVComments.class).equalTo(Utility.COLUMN_CREATED_BY, Utility.LAST_UPDATE_BY).findAll().deleteAllFromRealm();
        bgRealm.copyToRealmOrUpdate(data.getLdvComments());

        for (TrnLDVComments _obj : data.getLdvComments()) {
            if (_obj == null)
                continue;

            if (_obj.getFlagDone() != null && _obj.getFlagDone().equalsIgnoreCase("Y")) {
                SyncTrnLDVComments sync = bgRealm.where(SyncTrnLDVComments.class)
                        .equalTo("ldvNo", _obj.getPk().getLdvNo())
                        .equalTo("seqNo", _obj.getPk().getSeqNo())
                        .equalTo("contractNo", _obj.getPk().getContractNo())
                        .findFirst();

                if (sync == null) {
                    sync = new SyncTrnLDVComments();
                }
                sync.setLdvNo(_obj.getPk().getLdvNo());
                sync.setSeqNo(_obj.getPk().getSeqNo());
                sync.setContractNo(_obj.getPk().getContractNo());
                sync.setLastUpdateBy(_obj.getLastupdateBy());
                sync.setCreatedBy(_obj.getCreatedBy());
                sync.setSyncedDate(_obj.getDateDone());


                bgRealm.copyToRealm(sync);
            }
        }

        //rvcoll
        d = bgRealm.where(TrnRVColl.class).equalTo(Utility.COLUMN_CREATED_BY, Utility.LAST_UPDATE_BY).findAll().deleteAllFromRealm();
        bgRealm.copyToRealmOrUpdate(data.getRvColl());

        for (TrnRVColl _obj : data.getRvColl()) {
            if (_obj == null)
                continue;

            if (_obj.getFlagDone() != null && _obj.getFlagDone().equalsIgnoreCase("Y")) {
                SyncTrnRVColl sync = bgRealm.where(SyncTrnRVColl.class)
                        .equalTo("ldvNo", _obj.getLdvNo())
                        .equalTo("contractNo", _obj.getContractNo())
                        .findFirst();

                if (sync == null) {
                    sync = new SyncTrnRVColl();
                }
                sync.setLdvNo(_obj.getLdvNo());
                sync.setContractNo(_obj.getContractNo());
                sync.setLastUpdateBy(_obj.getLastupdateBy());
                sync.setCreatedBy(_obj.getCreatedBy());
                sync.setSyncedDate(_obj.getDateDone());


                bgRealm.copyToRealm(sync);
            }
        }

        // photo
        d = bgRealm.where(TrnPhoto.class).findAll().deleteAllFromRealm();
        bgRealm.copyToRealmOrUpdate(data.getPhoto());

        for (TrnPhoto _obj : data.getPhoto()) {
            if (_obj == null)
                continue;

            if (_obj.getCreatedTimestamp() != null) {
                SyncFileUpload sync = bgRealm.where(SyncFileUpload.class)
                        .equalTo("contractNo", _obj.getContractNo())
                        .equalTo("collectorId", _obj.getCollCode())
                        .equalTo("pictureId", _obj.getPhotoId())
                        .findFirst();

                if (sync == null) {
                    sync = new SyncFileUpload();
                    sync.setUid(java.util.UUID.randomUUID().toString());
                }
//                                            sync.setLdvNo(_obj.getLdvNo());
                sync.setContractNo(_obj.getContractNo());
                sync.setCollectorId(_obj.getCollCode());
                sync.setPictureId(_obj.getPhotoId());
                sync.setSyncedDate(_obj.getCreatedTimestamp());


                bgRealm.copyToRealmOrUpdate(sync);
            }
        }

        //PoA. no need because it will not used after sync
//        d = bgRealm.where(TrnFlagTimestamp.class).findAll().deleteAllFromRealm();
//        bgRealm.copyToRealmOrUpdate(data.getFlagTimestamps());

    }

    public static TrnLDVHeader getLDVHeaderByContract(Realm realm, String contractNo) throws ContractNotFoundException, LKPNotFoundException{
        TrnLDVDetails dtl = realm.where(TrnLDVDetails.class)
                .equalTo("contractNo", contractNo)
                .findFirst();

        if (dtl == null)
            throw new ContractNotFoundException("Contract " + contractNo + " not found");

        TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                .equalTo("ldvNo", dtl.getPk().getLdvNo())
                .findFirst();

        if (trnLDVHeader == null)
            throw new LKPNotFoundException("LDV " + dtl.getPk().getLdvNo() + " for contract " + contractNo + " not found");

        return trnLDVHeader;

    }

    public static boolean isLDVHeaderClosed(Realm realm, String contractNo) throws ContractNotFoundException, LKPNotFoundException{
        TrnLDVHeader trnLDVHeader = getLDVHeaderByContract(realm, contractNo);

        return trnLDVHeader != null && trnLDVHeader.getCloseBatch() != null && trnLDVHeader.getCloseBatch().equals("Y");
    }

    public static void convertUserDataToLoginInfo(UserData userData) {
        Realm realm = Realm.getDefaultInstance();
        try{
            realm.beginTransaction();

            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USERID, userData.getUserId()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_BRANCH_ID, userData.getBranchId()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_BRANCH_NAME, userData.getBranchName()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_EMAIL, userData.getEmailAddr()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_JABATAN, userData.getJabatan()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_NIK, userData.getNik()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_ADDRESS, userData.getAlamat()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_PHONE, userData.getPhoneNo()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_COLL_TYPE, userData.getCollectorType()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_PASSWORD, userData.getUserPwd()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_BIRTH_PLACE, userData.getBirthPlace()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_BIRTH_DATE, userData.getBirthDate()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_FULLNAME, userData.getFullName()));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_BUSS_UNIT, userData.getBussUnit()));

            realm.commitTransaction();
        }finally {
            if (realm != null) {
                realm.close();
            }
        }

//        loginInfo.setDeviceId(userData.getConfig().getDeviceId());
//        loginInfo.setImeiDevice(userData.getConfig().getImeiDevice());
//        loginInfo.setLastLogin(userData.getConfig().getLastLogin());
//        loginInfo.setSyncMinute(userData.getConfig().getSyncMinute());
//        loginInfo.setServerDate(userData.getConfig().getServerDate());
//        loginInfo.setPhotoProfileUri(userData.getConfig().getPhotoProfileUri());
    }

    public static final String prettyAddress(TrnCollectAddr addr) {

        if (addr == null) return "";

        StringBuilder alamat = new StringBuilder();
        alamat.append(addr.getCollAddr())
                    .append("\n").append(addr.getCollCity()).append(", ").append(addr.getCollZip())
                    .append("\nRT/RW: ").append(addr.getCollRt() + "/" + addr.getCollRw())
                    .append("\nKel/Kec: ").append(addr.getCollKel() + "/" + addr.getCollKec())
        ;

        if (TextUtils.isEmpty(addr.getCollMobPhone())) {
            alamat.append("\nPhone").append(addr.getCollMobPhone());
        }

        return alamat.toString();

    }
}


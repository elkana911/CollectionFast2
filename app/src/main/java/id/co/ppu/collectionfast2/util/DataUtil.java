package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import id.co.ppu.collectionfast2.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.chat.TrnChatMsg;
import id.co.ppu.collectionfast2.pojo.master.MstDelqReasons;
import id.co.ppu.collectionfast2.pojo.master.MstLDVClassifications;
import id.co.ppu.collectionfast2.pojo.master.MstLDVParameters;
import id.co.ppu.collectionfast2.pojo.master.MstLDVStatus;
import id.co.ppu.collectionfast2.pojo.master.MstOffices;
import id.co.ppu.collectionfast2.pojo.master.MstParam;
import id.co.ppu.collectionfast2.pojo.master.MstPotensi;
import id.co.ppu.collectionfast2.pojo.master.MstZip;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVComments;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVColl;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
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
     *
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
     * @see #isMasterTransactionTable(String)
     * @param ctx
     * @param realm
     * @return
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

        if (!NetUtil.isConnected(ctx))
            return false;

        try {
            retrieveMasterFromServerBackground(realm, ctx);

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public static void retrieveMasterFromServerBackground(Realm realm, Context ctx) throws Exception {

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        Call<ResponseGetMasterData> callMasterData = fastService.getMasterData();

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
                                    // insert ldp classifications
                                    long count = bgRealm.where(MstLDVClassifications.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstLDVClassifications.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getLdpClassifications());

                                    // insert param
                                    count = bgRealm.where(MstParam.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstParam.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getParams());

                                    // insert ldp status
                                    count = bgRealm.where(MstLDVStatus.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstLDVStatus.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getLdpStatus());

                                    // insert ldp parameter
                                    count = bgRealm.where(MstLDVParameters.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstLDVParameters.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getLdpParameters());

                                    // insert delq reasons
                                    count = bgRealm.where(MstDelqReasons.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstDelqReasons.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getDelqReasons());

                                    // insert office
                                    count = bgRealm.where(MstOffices.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstOffices.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getOffices());

                                    // insert potensi
                                    count = bgRealm.where(MstPotensi.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstPotensi.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getPotensi());

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

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        RequestZipCode requestZipCode = new RequestZipCode();
//        request.setZipCode("11058");

        Call<ResponseGetZipCode> callZipCode = fastService.getZipCode(requestZipCode);

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
        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        Call<ResponseServerInfo> call = fastService.getServerInfo();
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

    public static int isLKPSynced(Realm realm, TrnLDVDetails dtl) {
        RealmResults<SyncTrnRVColl> trnSync = realm.where(SyncTrnRVColl.class)
                .equalTo("ldvNo", dtl.getPk().getLdvNo())
                .equalTo("contractNo", dtl.getContractNo())
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .isNotNull("syncedDate")
                .findAll();

        if (trnSync.size() > 0)
            return SYNC_AS_PAYMENT;

        RealmResults<SyncTrnLDVComments> trnSyncLDVComments = realm.where(SyncTrnLDVComments.class)
                .equalTo("ldvNo", dtl.getPk().getLdvNo())
                .equalTo("contractNo", dtl.getContractNo())
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .isNotNull("syncedDate")
                .findAll();
        if (trnSyncLDVComments.size() > 0)
            return SYNC_AS_VISIT;

        RealmResults<SyncTrnRepo> trnSyncRepo = realm.where(SyncTrnRepo.class)
                .equalTo("contractNo", dtl.getContractNo())
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .isNotNull("syncedDate")
                .findAll();
        if (trnSyncRepo.size() > 0)
            return SYNC_AS_REPO;
//        return trnSync.size() > 0 || trnSyncLDVComments.size() > 0 || trnSyncRepo.size() > 0;

        return 0;   // no sync
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

}

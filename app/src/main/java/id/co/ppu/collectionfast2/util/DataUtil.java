package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

import id.co.ppu.collectionfast2.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.master.MstDelqReasons;
import id.co.ppu.collectionfast2.pojo.master.MstLDVClassifications;
import id.co.ppu.collectionfast2.pojo.master.MstLDVParameters;
import id.co.ppu.collectionfast2.pojo.master.MstLDVStatus;
import id.co.ppu.collectionfast2.pojo.master.MstOffices;
import id.co.ppu.collectionfast2.pojo.master.MstParam;
import id.co.ppu.collectionfast2.pojo.master.MstZip;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVComments;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVColl;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMasterData;
import id.co.ppu.collectionfast2.rest.response.ResponseGetZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseServerInfo;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Eric on 23-Sep-16.
 */

public class DataUtil {

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

    public static boolean isMasterDataDownloaded(Context ctx, Realm realm) {

        ServerInfo serverInfo = realm.where(ServerInfo.class).findFirst();
        UserConfig userConfig = realm.where(UserConfig.class).findFirst();

        long count = realm.where(MstParam.class).count();

        if (serverInfo != null) {
            if (count > 0 && Utility.isSameDay(new Date(), serverInfo.getServerDate())) {
                return true;
            }
        }

        // is master data complete  ?

        // skip master data
        if (count > 0) {
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

    public static void retrieveServerInfo(final Realm realm, Context ctx, final OnPostRetrieveServerInfo listener) throws Exception {
        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        Call<ResponseServerInfo> call = fastService.getServerInfo();
        call.enqueue(new Callback<ResponseServerInfo>() {
            @Override
            public void onResponse(Call<ResponseServerInfo> call, Response<ResponseServerInfo> response) {
                if (response.isSuccessful()) {
                    final ResponseServerInfo responseServerInfo = response.body();

                    if (responseServerInfo.getError() == null) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealm(responseServerInfo.getData());

                                if (listener != null)
                                    listener.onSuccess(responseServerInfo.getData());
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
            String[] proj = { MediaStore.Images.Media.DATA };
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

    public static boolean isLKPSynced(Realm realm, TrnLDVDetails dtl) {
        RealmResults<SyncTrnRVColl> trnSync = realm.where(SyncTrnRVColl.class)
                .equalTo("ldvNo", dtl.getPk().getLdvNo())
                .equalTo("contractNo", dtl.getContractNo())
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .isNotNull("syncedDate")
                .findAll();

        RealmResults<SyncTrnLDVComments> trnSyncLDVComments = realm.where(SyncTrnLDVComments.class)
                .equalTo("ldvNo", dtl.getPk().getLdvNo())
                .equalTo("contractNo", dtl.getContractNo())
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .isNotNull("syncedDate")
                .findAll();

        RealmResults<SyncTrnRepo> trnSyncRepo = realm.where(SyncTrnRepo.class)
                .equalTo("contractNo", dtl.getPk().getLdvNo())
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .isNotNull("syncedDate")
                .findAll();

        return trnSync.size() > 0 || trnSyncLDVComments.size() > 0 || trnSyncRepo.size() > 0;

    }

}

package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import id.co.ppu.collectionfast2.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collectionfast2.pojo.MstDelqReasons;
import id.co.ppu.collectionfast2.pojo.MstLDVClassifications;
import id.co.ppu.collectionfast2.pojo.MstLDVParameters;
import id.co.ppu.collectionfast2.pojo.MstLDVStatus;
import id.co.ppu.collectionfast2.pojo.MstOffices;
import id.co.ppu.collectionfast2.pojo.MstZip;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMasterData;
import id.co.ppu.collectionfast2.rest.response.ResponseGetZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseServerInfo;
import io.realm.Realm;
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
    public static void retrieveMasterFromServerBackground(Realm realm, Context ctx) throws Exception{

        long count = realm.where(MstDelqReasons.class).count();

        // skip master data
        if (count > 0) {
            return;
        }

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
                        try{
                            _realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm bgRealm) {
                                    // insert ldp classifications
                                    long count = bgRealm.where(MstLDVClassifications.class).count();
                                    if (count > 0) {
                                        bgRealm.delete(MstLDVClassifications.class);
                                    }
                                    bgRealm.copyToRealmOrUpdate(respGetMasterData.getData().getLdpClassifications());

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

                        }finally {
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

    public static void retrieveZipCodeFromServerBackground(Realm realm, Context ctx) throws Exception{

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

                        try{
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
                        }finally {
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

    public static void retrieveServerInfo(final Realm realm, Context ctx, final OnPostRetrieveServerInfo listener) throws Exception{
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


}

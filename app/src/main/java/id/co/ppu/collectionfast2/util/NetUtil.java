package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import id.co.ppu.collectionfast2.exceptions.NoConnectionException;
import id.co.ppu.collectionfast2.listener.OnGetChatContactListener;
import id.co.ppu.collectionfast2.listener.OnSuccessError;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.chat.TrnChatContact;
import id.co.ppu.collectionfast2.pojo.chat.TrnChatMsg;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollPos;
import id.co.ppu.collectionfast2.pojo.trn.TrnErrorLog;
import id.co.ppu.collectionfast2.pojo.trn.TrnFlagTimestamp;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestLogError;
import id.co.ppu.collectionfast2.rest.request.RequestRVB;
import id.co.ppu.collectionfast2.rest.request.RequestSyncLocation;
import id.co.ppu.collectionfast2.rest.request.chat.RequestChatContacts;
import id.co.ppu.collectionfast2.rest.request.chat.RequestChatMsg;
import id.co.ppu.collectionfast2.rest.request.chat.RequestChatStatus;
import id.co.ppu.collectionfast2.rest.response.ResponseRVB;
import id.co.ppu.collectionfast2.rest.response.chat.ResponseGetOnlineContacts;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Eric on 30-Aug-16.
 */
public class NetUtil {
    public static boolean isConnected(Context ctx) {
        ConnectivityManager connec = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        return (connec.getActiveNetworkInfo() != null)
                && (connec.getActiveNetworkInfo().isAvailable())
                && (connec.getActiveNetworkInfo().isConnected());
    }

    /**
     * KOnsepnya kalo ada koneksi langsung kirim, kalo tidak ada, simpan dulu di lokal.
     * Kalo sukses terkirim clear out data
     *
     * @param ctx
     * @param realm
     * @param collectorId
     * @param moduleName
     * @param message1
     * @param message2
     */
    public static void syncLogError(final Context ctx, final Realm realm, final String collectorId, final String moduleName, final String message1, final String message2) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TrnErrorLog trnErrorLog = new TrnErrorLog();
                trnErrorLog.setUid(java.util.UUID.randomUUID().toString());
                trnErrorLog.setCollectorId(collectorId);
                trnErrorLog.setCreatedTimestamp(new Date());
                trnErrorLog.setModule(moduleName);
                trnErrorLog.setMessage1(message1);
                trnErrorLog.setMessage2(message2);
                realm.copyToRealm(trnErrorLog);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (isConnected(ctx)) {

                    ApiInterface fastService =
                            ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));
                    RequestLogError req = new RequestLogError();

                    RealmResults<TrnErrorLog> trnErrorLogs = realm.where(TrnErrorLog.class)
                            .equalTo("collectorId", collectorId)
                            .findAll();

                    req.setLogs(realm.copyFromRealm(trnErrorLogs));

                    Call<ResponseBody> call = fastService.logError(req);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            Realm r = Realm.getDefaultInstance();
                            r.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    boolean b = realm.where(TrnErrorLog.class)
                                            .equalTo("collectorId", collectorId)
                                            .findAll().deleteAllFromRealm();

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                } else {
                }

            }
        });

    }

    public static void refreshRVBFromServer(final Context ctx) {

        if (!isConnected(ctx)) {
            return;
        }

        // dan hanya kalo ada header saja
        Realm r1 = Realm.getDefaultInstance();
        try {
            if (r1.where(TrnLDVHeader.class).count() < 1) {
                return;
            }
        } finally {
            if (r1 != null) {
                r1.close();
            }
        }


        final UserData userData = (UserData) Storage.getObjPreference(ctx.getApplicationContext(), Storage.KEY_USER, UserData.class);

        if (userData == null) {
            return;
        }

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        RequestRVB req = new RequestRVB();
        req.setCollectorId(userData.getUserId());

        Call<ResponseRVB> call = fastService.getRVB(req);
        call.enqueue(new Callback<ResponseRVB>() {
            @Override
            public void onResponse(Call<ResponseRVB> call, Response<ResponseRVB> response) {
                if (response.isSuccessful()) {

                    final ResponseRVB respGetRVB = response.body();

                    if (respGetRVB.getData() == null || respGetRVB.getData().size() < 1) {
                        return;
                    }

                    final Realm r = Realm.getDefaultInstance();

                    r.executeTransactionAsync(new Realm.Transaction() {
                                                  @Override
                                                  public void execute(Realm realm) {
                                                      // delete yg OP
                                                      boolean b = realm.where(TrnRVB.class)
                                                              .equalTo("rvbStatus", "OP")
                                                              .findAll().deleteAllFromRealm();

                                                      for (TrnRVB obj : respGetRVB.getData()) {

                                                          TrnRVB rvbNo = realm.where(TrnRVB.class)
                                                                  .equalTo("rvbNo", obj.getRvbNo())
                                                                  .findFirst();
                                                          //kalo ada brarti CL, jgn diupdate
                                                          if (rvbNo != null) {

                                                          } else {
                                                              realm.copyToRealm(obj);
                                                          }

                                                      }
                                                      // isi ulang dgn list yg baru, kecuali yg udah kepake di lokal jgn di OP lagi

                                                  }
                                              }, new Realm.Transaction.OnSuccess() {
                                                  @Override
                                                  public void onSuccess() {
                                                      r.close();
                                                  }
                                              }, new Realm.Transaction.OnError() {
                                                  @Override
                                                  public void onError(Throwable error) {
                                                      r.close();
                                                  }
                                              }
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseRVB> call, Throwable t) {

            }
        });

    }

    /**
     * Make sure this method is run in background or asynctask
     *
     * @param ctx
     * @param offline if true will only store to local without syncing data to server
     */
    public static void syncLocation(final Context ctx, final double[] gps, boolean offline) {
        Realm realm = Realm.getDefaultInstance();
        try {
//            final double[] gps = Location.getGPS(ctx);

            Log.i("eric.gps", "lat=" + String.valueOf(gps[0]) + ",lng=" + String.valueOf(gps[1]));
//            final Date twoDaysAgo = Utility.getTwoDaysAgo(new Date());

            final UserData userData = (UserData) Storage.getObjPreference(ctx.getApplicationContext(), Storage.KEY_USER, UserData.class);

            if (userData == null) {
                return;
            }

            // hanya kalo masih open saja
            if (!DataUtil.isLDVHeaderAnyOpen(realm, userData.getUserId()))
                return;


            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    long total = realm.where(TrnCollPos.class).count();

                    RealmResults<TrnCollPos> all = realm.where(TrnCollPos.class).findAll();

//                        long totalTwoDaysAgo = realm.where(TrnCollPos.class).lessThanOrEqualTo("lastUpdate", twoDaysAgo).count();


                    TrnCollPos trnCollPos = new TrnCollPos();

                    trnCollPos.setUid(java.util.UUID.randomUUID().toString());

                    trnCollPos.setCollectorId(userData.getUserId());
                    trnCollPos.setLatitude(String.valueOf(gps[0]));
                    trnCollPos.setLongitude(String.valueOf(gps[1]));
                    trnCollPos.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnCollPos);

                }
            });

            if (offline)
                return;

            if (!isConnected(ctx)) {
                return;
            }

            ApiInterface fastService =
                    ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

            RequestSyncLocation req = new RequestSyncLocation();

            RealmResults<TrnCollPos> trnCollPoses = realm.where(TrnCollPos.class)
                    .equalTo("collectorId", userData.getUserId())
                    .findAll();

            req.setList(realm.copyFromRealm(trnCollPoses));

            Call<ResponseBody> call = fastService.syncLocation(req);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Realm r = Realm.getDefaultInstance();
                        r.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                // delete local data, tapi sisain satu buat VisitResult
                                TrnCollPos lastCollPos = realm.where(TrnCollPos.class)
                                        .equalTo("collectorId", userData.getUserId())
                                        .findAllSorted("lastupdateTimestamp", Sort.DESCENDING)
                                        .first();

                                if (lastCollPos != null) {
                                    TrnCollPos firstCollPos = realm.copyFromRealm(lastCollPos);

                                    boolean b = realm.where(TrnCollPos.class)
                                            .equalTo("collectorId", userData.getUserId())
                                            .findAll().deleteAllFromRealm();

                                    // masukin lagi
                                    realm.copyToRealm(firstCollPos);
                                }


                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (realm != null)
                realm.close();
        }

    }
/*
    public static boolean uploadPicture(Context ctx, String officeCode, String collectorId, String ldvNo, String contractNo, String pictureId, String latitude, String longitude, Uri uri, Callback<ResponseBody> callback) {
        if (TextUtils.isEmpty(officeCode)
                || TextUtils.isEmpty(collectorId)
                || TextUtils.isEmpty(ldvNo)
                || TextUtils.isEmpty(contractNo)
                || TextUtils.isEmpty(pictureId)
                )
            return false;

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        File file4 = new File(DataUtil.getRealPathFromUri(ctx, uri));

        boolean b4 = file4.exists();

        if (!file4.canRead()) {
            return false;
        }

        RequestBody body_officeCode =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), officeCode);

        RequestBody body_collectorId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), collectorId);

        RequestBody body_ldvNo =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), ldvNo);

        RequestBody body_contractNo =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), contractNo);

        RequestBody body_picId =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), pictureId);

        RequestBody body_latitude =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), latitude);

        RequestBody body_longitude =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), longitude);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file4);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file4.getName(), requestFile);

        Call<ResponseBody> call = fastService.uploadPhoto(body_officeCode, body_collectorId, body_ldvNo, body_contractNo, body_picId, body_latitude, body_longitude, body);
//        Call<ResponseBody> call = fastService.uploadPhoto(contractNo, pictureId, body);
        call.enqueue(callback);

        return true;
    }
*/

    // TODO: sync all photos
    public static boolean uploadPhotos(Context ctx, Realm realm, OnSuccessError listener) {

        RealmResults<TrnPhoto> trnPhotos = realm.where(TrnPhoto.class)
//                .equalTo("collCode", collectorCode)
                .findAll();

        return trnPhotos.size() >= 1;

    }

    public static boolean uploadPhoto(Context ctx, TrnPhoto trnPhoto, Uri uri, Callback<ResponseBody> callback) {
        if (trnPhoto == null)
            return false;

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        File file4 = new File(DataUtil.getRealPathFromUri(ctx, uri));

        boolean b4 = file4.exists();

        if (!file4.canRead()) {
            return false;
        }

        // create RequestBody instance from file
//        RequestBody requestFile =
//                RequestBody.create(MediaType.parse("multipart/form-data"), file4);
        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), Storage.getCompressedImage(ctx, file4, trnPhoto.getPhotoId()));
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("picture", file4.getName(), requestFile);

            Gson gson = new GsonBuilder()
                    .setDateFormat("dd-MM-yyyy HH:mm:ss")
                    .create();
            String jsonData = gson.toJson(trnPhoto);
            RequestBody body_trnPhoto =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), jsonData);

            Call<ResponseBody> call = fastService.upload_Photo(body_trnPhoto, body);

            call.enqueue(callback);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
//        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), file4);

/*
        byte[] buf;
        try {
            InputStream in = new FileInputStream(file4Compressed);
            buf = new byte[in.available()];
            while (in.read(buf) != -1);

            RequestBody requestBody = RequestBody
                    .create(MediaType.parse("application/octet-stream"), buf);

        } catch (IOException e) {
            e.printStackTrace();
        }
*/
//        Call<ResponseBody> call = fastService.upload_Photo2(body_trnPhoto, requestBody);
//            Call<ResponseBody> call = fastService.upload_Photo(body_trnPhoto, body);
//            Call<ResponseBody> call = fastService.upload_Photo2(body_trnPhoto, reqFile);
//        Call<ResponseBody> call = fastService.uploadPhoto(contractNo, pictureId, body);

//        call.enqueue(callback);

        return true;
    }

    public static void chatLogOn(Context ctx, String collCode, final OnSuccessError listener) {
        // send to server that current contact is online
        if (!isConnected(ctx)) {
            return;
        }

        String androidId = Storage.getAndroidToken(ctx);
        String userStatus = ConstChat.FLAG_ONLINE;
        String userMsg = "Available";

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(userStatus);
        req.setMessage(userMsg);
        req.setAndroidId(androidId);

        Call<ResponseBody> call = Storage.getAPIService(ctx).sendStatus(req);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // update display status here
                if (!response.isSuccessful()) {

                    ResponseBody errorBody = response.errorBody();

                    try {
                        if (listener != null) {
                            listener.onFailure(new RuntimeException(errorBody.string()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                if (listener != null)
                    listener.onSuccess(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);
            }
        });

    }

    /**
     * Using array technique still confusing and hog my dev time
     * Mengapa diperlukan array, krn callbacknya cukup sekali. kalo satu2 kan callbacknya sendiri2 ngaruh ke progressbar
     *
     * @param ctx
     * @param poaData
     * @param poaFiles
     * @param callback
     */
    @Deprecated
    public static void uploadPoA(Context ctx, List<TrnFlagTimestamp> poaData, File[] poaFiles, Callback<ResponseBody> callback) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (int i = 0; i < poaFiles.length; i++) {
            File file = poaFiles[i];

            RequestBody bannerPicture1 = RequestBody.create(MediaType.parse("image/*"), file);

            builder.addFormDataPart("picture" + (i + 1), file.getName(), bannerPicture1);
        }

        MultipartBody bodies = builder.build();

//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), Storage.getCompressedImage(ctx, file4, trnPhoto.getPhotoId()));
        Gson gson = new GsonBuilder()
                .setDateFormat("dd-MM-yyyy HH:mm:ss")
                .create();
        String jsonData = gson.toJson(poaData);
        RequestBody body_poaData =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), jsonData);

        /*
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), Storage.getCompressedImage(ctx, file4, trnPhoto.getPhotoId()));
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("picture", file4.getName(), requestFile);

        MultipartBody.Part body1 = prepareFilePart("video", file1Uri);
        MultipartBody.Part body2 = prepareFilePart("thumbnail", file2Uri);
*/

        if (!NetUtil.isConnected(ctx)) {
            throw new NoConnectionException();
        }

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        Call<ResponseBody> call = fastService.upload_PhotoOnArrival(body_poaData, bodies);

        call.enqueue(callback);

    }

    //    public static boolean uploadPoA(Context ctx, TrnFlagTimestamp trnFlagTimestamp, File file, Callback<ResponseBody> callback) {
    public static boolean uploadPoA(Context ctx, TrnFlagTimestamp trnFlagTimestamp, File file, OnSuccessError listener) {

        if (trnFlagTimestamp == null)
            return false;

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

//        File file4 = new File(DataUtil.getRealPathFromUri(ctx, uri));

        boolean b4 = file.exists();

        if (!file.canRead()) {
            return false;
        }

        // create RequestBody instance from file
        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), Storage.getCompressedImage(ctx, file, trnFlagTimestamp.getFileName()));
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

            Gson gson = new GsonBuilder()
                    .setDateFormat("dd-MM-yyyy HH:mm:ss")
                    .create();
            String jsonData = gson.toJson(trnFlagTimestamp);
            RequestBody body_trnPoA =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), jsonData);


            Call<ResponseBody> call = fastService.upload_PhotoOnArrival(body_trnPoA, body);
//            Call<ResponseBody> call = fastService.upload_Photo(body_trnPhoto, body);

            Response<ResponseBody> execute = call.execute();

            if (!execute.isSuccessful()) {
                int statusCode = execute.code();

                // handle request errors yourself
                ResponseBody errorBody = execute.errorBody();

                if (listener != null) {
                    listener.onFailure(new RuntimeException("Server Problem (" + statusCode + ")" + errorBody.string()));
                }

                return false;
            }

            final ResponseBody resp = execute.body();

            String msgStatus = resp.string();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void chatLogOff(Context ctx, String collCode, final OnSuccessError listener) {
        if (!isConnected(ctx)) {
            return;
        }

        String androidId = Storage.getAndroidToken(ctx);
        String userStatus = ConstChat.FLAG_OFFLINE;
        String userMsg = "Offline";

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(userStatus);
        req.setMessage(userMsg);
        req.setAndroidId(androidId);

        Call<ResponseBody> call = Storage.getAPIService(ctx).sendStatus(req);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // update display status here
                if (!response.isSuccessful()) {

                    ResponseBody errorBody = response.errorBody();

                    try {
                        if (listener != null) {
                            listener.onFailure(new RuntimeException(errorBody.string()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return;
                }

                // TODO: this is dangerous thread
                Realm r = Realm.getDefaultInstance();
                try {
                    r.beginTransaction();
                    r.delete(TrnChatContact.class);
//                    r.delete(TrnChatMsg.class);
                    r.commitTransaction();
                } finally {
                    if (r != null)
                        r.close();
                }

                if (listener != null)
                    listener.onSuccess(null);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);
            }
        });

    }

    public static void chatUpdateContacts(Context ctx, String collCode, final OnGetChatContactListener listener) {
        if (!isConnected(ctx)) {
            return;
        }

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(null);
        req.setMessage(null);

        Call<ResponseGetOnlineContacts> call = Storage.getAPIService(ctx).getOnlineContacts(req);
        call.enqueue(new Callback<ResponseGetOnlineContacts>() {
            @Override
            public void onResponse(Call<ResponseGetOnlineContacts> call, Response<ResponseGetOnlineContacts> response) {

                if (!response.isSuccessful()) {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    if (listener != null) {
                        listener.onFailure(null);
                    }

                    return;
                }

                final ResponseGetOnlineContacts resp = response.body();

                if (resp == null || resp.getData() == null) {
//                    Utility.createAndShowProgressDialog(MainChatActivity.this, "No Contacts found", "You have empty List.\nPlease try again.");
                    return;
                }

                if (resp.getError() != null) {
                    if (listener != null) {
                        String msg = "Error (" + resp.getError().getErrorCode() + ")\n" + resp.getError().getErrorDesc();
                        listener.onFailure(new RuntimeException(msg));
                    }

                    return;
                }
/*
dangerous code
                Realm r = Realm.getDefaultInstance();

                r.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(TrnChatContact.class);

                        realm.copyToRealm(resp.getData());
                    }
                });

                final int size = resp.getData().size();

                r.close();
*/
                if (listener != null) {
                    listener.onSuccess(resp.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetOnlineContacts> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);

            }
        });

    }

    public static void chatGetGroupContacts(Context ctx, String collCode, final OnGetChatContactListener listener) {

        if (!isConnected(ctx)) {
            return;
        }

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(null);
        req.setMessage(null);

        Call<ResponseGetOnlineContacts> call = Storage.getAPIService(ctx).getGroupContacts(req);
        call.enqueue(new Callback<ResponseGetOnlineContacts>() {
            @Override
            public void onResponse(Call<ResponseGetOnlineContacts> call, Response<ResponseGetOnlineContacts> response) {

                if (!response.isSuccessful()) {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    if (listener != null) {
                        listener.onFailure(null);
                    }

                    return;
                }

                final ResponseGetOnlineContacts resp = response.body();

                if (resp == null || resp.getData() == null) {
//                    Utility.createAndShowProgressDialog(MainChatActivity.this, "No Contacts found", "You have empty List.\nPlease try again.");
                    return;
                }

                if (resp.getError() != null) {
                    if (listener != null) {
                        String msg = "Error (" + resp.getError().getErrorCode() + ")\n" + resp.getError().getErrorDesc();
                        listener.onFailure(new RuntimeException(msg));
                    }

                    return;
                }
/*
                Realm r = Realm.getDefaultInstance();

                r.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(TrnChatContact.class);

                        realm.copyToRealmOrUpdate(resp.getData());
                    }
                });

                final int size = resp.getData().size();

                r.close();
                */

                if (listener != null) {

//                    listener.onSuccess("" + size + " CONTACTS");
                    listener.onSuccess(resp.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetOnlineContacts> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);

            }
        });

    }

    public static void chatGetContacts(Context ctx, final List<String> collectorsCode, final OnGetChatContactListener listener) {

        if (!isConnected(ctx)) {
            return;
        }

        RequestChatContacts req = new RequestChatContacts();
        req.setCollsCode(collectorsCode);

        Call<ResponseGetOnlineContacts> call = Storage.getAPIService(ctx).getChatContacts(req);
        call.enqueue(new Callback<ResponseGetOnlineContacts>() {
            @Override
            public void onResponse(Call<ResponseGetOnlineContacts> call, Response<ResponseGetOnlineContacts> response) {

                if (!response.isSuccessful()) {
                    int statusCode = response.code();

                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();

                    if (listener != null) {
                        listener.onFailure(null);
                    }

                    return;
                }

                final ResponseGetOnlineContacts resp = response.body();

                if (resp == null || resp.getData() == null) {
//                    Utility.createAndShowProgressDialog(MainChatActivity.this, "No Contacts found", "You have empty List.\nPlease try again.");
                    return;
                }

                if (resp.getError() != null) {
                    if (listener != null) {
                        String msg = "Error (" + resp.getError().getErrorCode() + ")\n" + resp.getError().getErrorDesc();
                        listener.onFailure(new RuntimeException(msg));
                    }

                    return;
                }
/*
                Realm r = Realm.getDefaultInstance();

                r.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<TrnChatContact> all = realm.where(TrnChatContact.class)
                                .in("collCode", collectorsCode.toArray((new String[collectorsCode.size()])))
                                .findAll();

                        all.deleteAllFromRealm();

                        realm.copyToRealmOrUpdate(resp.getData());
                    }
                });

                final int size = resp.getData().size();

                r.close();
*/
                if (listener != null) {
                    listener.onSuccess(resp.getData());
                }
            }

            @Override
            public void onFailure(Call<ResponseGetOnlineContacts> call, Throwable t) {
                if (listener != null)
                    listener.onFailure(t);

            }
        });

    }

    public static void chatSendQueueMessage(final Context ctx) {
        if (Utility.isScreenOff(ctx) || !NetUtil.isConnected(ctx))
            return;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                final Realm r = Realm.getDefaultInstance();
                try {

                    // 1. check transmitting data
                    final RealmResults<TrnChatMsg> anyTransmittingData = r.where(TrnChatMsg.class)
                            .equalTo("messageStatus", ConstChat.MESSAGE_STATUS_TRANSMITTING)
                            .findAll();

                    if (anyTransmittingData.size() > 0) {
//                Log.d(TAG, "There are " + anyTransmittingData.size() + " chats transmitting");
                        // wait until all chats sent to server
                        // if 5 minutes expired will be reset to unopened or firsttime
                        r.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (TrnChatMsg _obj : anyTransmittingData) {
                                    long minutesAge = Utility.getMinutesDiff(_obj.getCreatedTimestamp(), new Date());

                                    if (minutesAge > 5) {
                                        _obj.setMessageStatus(ConstChat.MESSAGE_STATUS_UNOPENED_OR_FIRSTTIME);
                                    }
                                }
                                // no need to do this
//                            realm.copyToRealmOrUpdate(anyTransmittingData);
                            }
                        });
                        return;
                    }

                    // 2. check pending message
                    final RealmResults<TrnChatMsg> pendings = r.where(TrnChatMsg.class)
                            .equalTo("messageStatus", ConstChat.MESSAGE_STATUS_UNOPENED_OR_FIRSTTIME)
                            .findAll();

                    if (pendings.size() > 0) {
                        r.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (TrnChatMsg _obj : pendings) {
                                    _obj.setMessageStatus(ConstChat.MESSAGE_STATUS_TRANSMITTING);
                                }
                            }
                        });

                        final RequestChatMsg req = new RequestChatMsg();
                        req.setMsg(r.copyFromRealm(pendings));

                        Call<ResponseBody> call = Storage.getAPIService(ctx).sendMessages(req);
                        // ga boleh call.enqueue krn bisa hilang dr memory utk variable2 di atas
                        try {
                            Response<ResponseBody> execute = call.execute();

                            if (!execute.isSuccessful()) {
                                r.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        for (TrnChatMsg _obj : pendings) {
                                            _obj.setMessageStatus(ConstChat.MESSAGE_STATUS_FAILED);
                                        }
                                    }
                                });

                                return;
                            }

                            final ResponseBody resp = execute.body();

                            try {
                                String msgStatus = resp.string();

//                        Log.d(TAG, "msgStatus = " + msgStatus);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    for (TrnChatMsg msg : pendings) {
                                        msg.setMessageStatus(ConstChat.MESSAGE_STATUS_SERVER_RECEIVED);
                                    }

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
//                            Utility.throwableHandler(ctx, e, false);
                        }

                    }

                } finally {
                    if (r != null)
                        r.close();
                }


            }
        });
    }

}

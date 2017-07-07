package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import id.co.ppu.collectionfast2.exceptions.NoConnectionException;
import id.co.ppu.collectionfast2.fcm.MyFirebaseMessagingService;
import id.co.ppu.collectionfast2.listener.OnGetChatContactListener;
import id.co.ppu.collectionfast2.listener.OnSuccessError;
import id.co.ppu.collectionfast2.pojo.chat.TrnChatContact;
import id.co.ppu.collectionfast2.pojo.chat.TrnChatMsg;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollPos;
import id.co.ppu.collectionfast2.pojo.trn.TrnErrorLog;
import id.co.ppu.collectionfast2.pojo.trn.TrnFlagTimestamp;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.rest.APIClientBuilder;
import id.co.ppu.collectionfast2.rest.APInterface;
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

        if (TextUtils.isEmpty(collectorId))
            return;

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

                    APInterface api =
                            APIClientBuilder.create(Utility.buildUrl(Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0)));

                    RequestLogError req = new RequestLogError();

                    RealmResults<TrnErrorLog> trnErrorLogs = realm.where(TrnErrorLog.class)
                            .equalTo("collectorId", collectorId)
                            .findAll();

                    req.setLogs(realm.copyFromRealm(trnErrorLogs));

                    Call<ResponseBody> call = api.logError(req);
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


//        final LoginInfo userData = (LoginInfo) Storage.getPreference(Storage.KEY_USER, null);
//        final UserData userData = (UserData) Storage.getObjPreference(ctx.getApplicationContext(), Storage.KEY_USER, UserData.class);

        final String collCode = Storage.getPref(Storage.KEY_USERID, null);

        if (collCode == null) {
            return;
        }

        APInterface api = APIClientBuilder.create(Utility.buildUrl(Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0)));

        RequestRVB req = new RequestRVB();
        req.setCollectorId(collCode);

        Call<ResponseRVB> call = api.getRVB(req);
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
        final String collCode = Storage.getPref(Storage.KEY_USERID, null);

        if (TextUtils.isEmpty(collCode))
            return;

        Realm realm = Realm.getDefaultInstance();
        try {
//            final double[] gps = Location.getGPS(ctx);

            Log.i("eric.gps", "lat=" + String.valueOf(gps[0]) + ",lng=" + String.valueOf(gps[1]));
//            final Date twoDaysAgo = Utility.getTwoDaysAgo(new Date());

//            final UserData userData = (UserData) Storage.getObjPreference(ctx.getApplicationContext(), Storage.KEY_USER, UserData.class);

            // hanya kalo masih open saja
            if (!DataUtil.isLDVHeaderAnyOpen(realm, collCode))
                return;


            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    long total = realm.where(TrnCollPos.class).count();

                    RealmResults<TrnCollPos> all = realm.where(TrnCollPos.class).findAll();

//                        long totalTwoDaysAgo = realm.where(TrnCollPos.class).lessThanOrEqualTo("lastUpdate", twoDaysAgo).count();


                    TrnCollPos trnCollPos = new TrnCollPos();

                    trnCollPos.setUid(java.util.UUID.randomUUID().toString());

                    trnCollPos.setCollectorId(collCode);
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

            APInterface api = APIClientBuilder.create(Utility.buildUrl(Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0)));

            RequestSyncLocation req = new RequestSyncLocation();

            RealmResults<TrnCollPos> trnCollPoses = realm.where(TrnCollPos.class)
                    .equalTo("collectorId", collCode)
                    .findAll();

            req.setList(realm.copyFromRealm(trnCollPoses));

            Call<ResponseBody> call = api.syncLocation(req);
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
                                        .equalTo("collectorId", collCode)
                                        .findAllSorted("lastupdateTimestamp", Sort.DESCENDING)
                                        .first();

                                if (lastCollPos != null) {
                                    TrnCollPos firstCollPos = realm.copyFromRealm(lastCollPos);

                                    boolean b = realm.where(TrnCollPos.class)
                                            .equalTo("collectorId", collCode)
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

        APInterface fastService =
                APIClientBuilder.create(APInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

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

        File file4 = new File(DataUtil.getRealPathFromUri(ctx, uri));

        boolean b4 = file4.exists();

        if (!file4.canRead()) {
            return false;
        }

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

            // override demo user
            if (!NetUtil.isConnected(ctx)) {
                if (DemoUtil.isDemo()) {

                    if (callback != null) {

                        okhttp3.Response rawResponse = new okhttp3.Response.Builder()
                                .code(200)
//                                .message(data)
                                .body(ResponseBody.create(MediaType.parse("application/json"), "message from server"))
//                                .protocol(Protocol.HTTP_1_0)
                                .addHeader("Content-Type", "application/json")
                                .build();

                        Response<ResponseBody> response = Response.success(null, rawResponse);

                        callback.onResponse(null, response);
                    }
                }
            }

            APInterface api = APIClientBuilder.create(Utility.buildUrl(Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0)));

            Call<ResponseBody> call = api.upload_Photo(body_trnPhoto, body);

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
        String androidId = Storage.getAndroidToken();
        String userStatus = ConstChat.FLAG_ONLINE;
        String userMsg = "Available";

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(userStatus);
        req.setMessage(userMsg);
        req.setAndroidId(androidId);

        if (!isConnected(ctx)) {
            if (DemoUtil.isDemo()) {
                if (listener != null)
                    listener.onSuccess(null);
            }

            return;
        }

        Call<ResponseBody> call = Storage.getAPIInterface().sendStatus(req);
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

        APInterface api = APIClientBuilder.create(Utility.buildUrl(Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0)));

        Call<ResponseBody> call = api.upload_PhotoOnArrival(body_poaData, bodies);

        call.enqueue(callback);

    }

    public static boolean uploadPoA(Context ctx, TrnFlagTimestamp trnFlagTimestamp, File file, OnSuccessError listener) {

        APInterface api = APIClientBuilder.create(Utility.buildUrl(Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0)));

        if (!file.canRead()) {
            return false;
        }

        // create RequestBody instance from file
        try {
            // ga perlu kompress lagi krn udah dikompress
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), Storage.getCompressedImage(ctx, file, trnFlagTimestamp.getFileName()));
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

            Gson gson = new GsonBuilder()
                    .setDateFormat("dd-MM-yyyy HH:mm:ss")
                    .create();
            String jsonData = gson.toJson(trnFlagTimestamp);
            RequestBody body_trnPoA =
                    RequestBody.create(
                            MediaType.parse("multipart/form-data"), jsonData);

            // override demo user
            if (!NetUtil.isConnected(ctx)
                    && DemoUtil.isDemo()) {
                if (listener != null)
                    listener.onSuccess(file.getName());

                return true;
            }

            Call<ResponseBody> call = api.upload_PhotoOnArrival(body_trnPoA, body);

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

            if (listener != null)
                listener.onSuccess(file.getName());


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void uploadPoAs(final Context ctx, final List<TrnFlagTimestamp> trnFlagTimestamps, final File[] files, final OnSuccessError listener) {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                final List<String> errors = new ArrayList<String>();

                for (int i = 0; i < trnFlagTimestamps.size(); i++) {
                    uploadPoA(ctx, trnFlagTimestamps.get(i), files[i], new OnSuccessError() {
                        @Override
                        public void onSuccess(String msg) {

                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            if (throwable == null)
                                return;
                            errors.add(throwable.getMessage());
                        }

                        @Override
                        public void onSkip() {

                        }
                    });
                }

                if (errors.size() < 1)
                    return null;

                return errors.get(0);
            }

            @Override
            protected void onPostExecute(String ret) {
                super.onPostExecute(ret);

                if (listener != null) {
                    if (ret != null)
                        listener.onFailure(new RuntimeException(ret));
                    else
                        listener.onSuccess(null);
                }
            }
        }.execute();

    }

    public static void chatLogOff(Context ctx, String collCode, final OnSuccessError listener) {

        String androidId = Storage.getAndroidToken();
        String userStatus = ConstChat.FLAG_OFFLINE;
        String userMsg = "Offline";

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(userStatus);
        req.setMessage(userMsg);
        req.setAndroidId(androidId);

        if (!isConnected(ctx)) {
            if (DemoUtil.isDemo()) {

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

            return;
        }

        Call<ResponseBody> call = Storage.getAPIInterface().sendStatus(req);
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

        RequestChatStatus req = new RequestChatStatus();
        req.setCollCode(collCode);
        req.setStatus(null);
        req.setMessage(null);

        if (!isConnected(ctx)) {
            if (DemoUtil.isDemo()) {

                if (listener != null) {

                    ResponseGetOnlineContacts resp = new ResponseGetOnlineContacts();
                    resp.setData(DemoUtil.buildDummyChatContacts());

                    listener.onSuccess(resp.getData());
                }
            }


            return;
        }

        Call<ResponseGetOnlineContacts> call = Storage.getAPIInterface().getOnlineContacts(req);
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

        Call<ResponseGetOnlineContacts> call = Storage.getAPIInterface().getGroupContacts(req);
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

        Call<ResponseGetOnlineContacts> call = Storage.getAPIInterface().getChatContacts(req);
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
        if (Utility.isScreenOff(ctx))
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

                    if (pendings.size() < 1)
                        return;

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

                    if (!NetUtil.isConnected(ctx)) {
                        if (DemoUtil.isDemo()) {

                            r.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    for (TrnChatMsg msg : pendings) {
                                        msg.setMessageStatus(ConstChat.MESSAGE_STATUS_ALL_READ_AND_OPENED);
                                    }
                                }
                            });

                            // give dummy respond rightaway
                            for (TrnChatMsg msg : pendings) {
                                Map<String, String> data = new HashMap<String, String>();
                                data.put(ConstChat.KEY_FROM, msg.getToCollCode());
                                data.put(ConstChat.KEY_UID, UUID.randomUUID().toString());
                                data.put(ConstChat.KEY_MESSAGE, "OK, i read your message:" + msg.getMessage());
                                data.put(ConstChat.KEY_STATUS, ConstChat.MESSAGE_STATUS_SERVER_RECEIVED);
                                data.put(ConstChat.KEY_TIMESTAMP, Utility.convertDateToString(new Date(), "yyyyMMddHHmmssSSS"));

                                MyFirebaseMessagingService.broadcastMessage(ctx, msg.getToCollCode(), null, data);
                            }
                        }

                        return;
                    }

                    Call<ResponseBody> call = Storage.getAPIInterface().sendMessages(req);
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

                            Log.i("RC-chat", "msgStatus = " + msgStatus);
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
                    }

                } finally {
                    if (r != null)
                        r.close();
                }


            }
        });
    }

}

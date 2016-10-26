package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import id.co.ppu.collectionfast2.listener.OnSuccessError;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollPos;
import id.co.ppu.collectionfast2.pojo.trn.TrnErrorLog;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestLogError;
import id.co.ppu.collectionfast2.rest.request.RequestSyncLocation;
import io.realm.Realm;
import io.realm.RealmResults;
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

    /**
     * Make sure this method is run in background or asynctask
     * @param ctx
     */
    public static void syncLocation(final Context ctx, final double[] gps, boolean offline) {
        Realm realm = Realm.getDefaultInstance();
        try {
//            final double[] gps = Location.getGPS(ctx);

            Log.i("eric.gps", "lat=" + String.valueOf(gps[0]) + ",lng=" + String.valueOf(gps[1]));
//            final Date twoDaysAgo = Utility.getTwoDaysAgo(new Date());

            final UserData userData = (UserData) Storage.getObjPreference(ctx, Storage.KEY_USER, UserData.class);

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
                                // delete local data
                                boolean b = realm.where(TrnCollPos.class)
                                        .equalTo("collectorId", userData.getUserId())
                                        .findAll().deleteAllFromRealm();

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
}

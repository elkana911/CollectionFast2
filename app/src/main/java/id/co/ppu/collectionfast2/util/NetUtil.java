package id.co.ppu.collectionfast2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

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

    public static boolean uploadPicture(Context ctx, String officeCode, String collectorId, String contractNo, String pictureId, String latitude, String longitude, Uri uri, Callback<ResponseBody> callback) {
        if (TextUtils.isEmpty(officeCode)
                || TextUtils.isEmpty(collectorId)
                || TextUtils.isEmpty(contractNo)
                || TextUtils.isEmpty(pictureId)
                )
            return false;

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(ctx, Storage.KEY_SERVER_ID, 0)));

        File file4 = new File(DataUtil.getRealPathFromUri(ctx, uri ));

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

        Call<ResponseBody> call = fastService.uploadPhoto(body_officeCode, body_collectorId, body_contractNo, body_picId, body_latitude, body_longitude, body);
//        Call<ResponseBody> call = fastService.uploadPhoto(contractNo, pictureId, body);
        call.enqueue(callback);

        return true;
    }
}

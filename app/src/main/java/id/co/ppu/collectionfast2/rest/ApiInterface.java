package id.co.ppu.collectionfast2.rest;

import id.co.ppu.collectionfast2.rest.request.RequestArea;
import id.co.ppu.collectionfast2.rest.request.RequestLKPByDate;
import id.co.ppu.collectionfast2.rest.request.RequestLogin;
import id.co.ppu.collectionfast2.rest.request.RequestRVB;
import id.co.ppu.collectionfast2.rest.request.RequestSyncLKP;
import id.co.ppu.collectionfast2.rest.request.RequestZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseArea;
import id.co.ppu.collectionfast2.rest.response.ResponseAreaList;
import id.co.ppu.collectionfast2.rest.response.ResponseGetLKP;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMasterArea;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMasterData;
import id.co.ppu.collectionfast2.rest.response.ResponseGetUsers;
import id.co.ppu.collectionfast2.rest.response.ResponseGetZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseRVB;
import id.co.ppu.collectionfast2.rest.response.ResponseServerInfo;
import id.co.ppu.collectionfast2.rest.response.ResponseSync;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Eric on 19-Aug-16.
 */
public interface ApiInterface {
    @POST("fast/login")
    Call<ResponseLogin> login(@Body RequestLogin request);

    @POST("fast/users")
    Call<ResponseGetUsers> getUsers();

    @POST("fast/lkp_date")
    Call<ResponseGetLKP> getLKPByDate(@Body RequestLKPByDate request);

    @POST("fast/masterdata")
    Call<ResponseGetMasterData> getMasterData();

    @POST("fast/masterarea")
    Call<ResponseGetMasterArea> getMasterArea();

    @POST("fast/zip_code")
    Call<ResponseGetZipCode> getZipCode(@Body RequestZipCode request);

    @POST("fast/area")
    Call<ResponseArea> getArea(@Body RequestArea req);

    @POST("fast/area_list")
    Call<ResponseAreaList> getAreaList(@Body RequestArea req);

    @POST("fast/server_info")
    Call<ResponseServerInfo> getServerInfo();

    @POST("fast/rvb")
    Call<ResponseRVB> getRVB(@Body RequestRVB req);

    @POST("fast/sync_lkp")
    Call<ResponseSync> syncLKP(@Body RequestSyncLKP req);

    @GET("fast/close_batch_yesterday")
    Call<ResponseBody> closeBatchYesterday(@Query("collector_code") String collectorCode);

    @Multipart
    @POST("/fast/upload_photo")
    Call<ResponseBody> uploadPhoto(@Part("office_code") RequestBody officeCode
            , @Part("collector_id") RequestBody collectorId
            , @Part("ldv_no") RequestBody ldvNo
            , @Part("contract_no") RequestBody contractNo
            , @Part("photo_id") RequestBody photoId
            , @Part("latitude") RequestBody latitude
            , @Part("longitude") RequestBody longitude
            , @Part MultipartBody.Part file);

//    Call<ResponseBody> uploadPhoto(@Part("contract_no") String contractNo, @Part("photo_id") String photoId, @Part MultipartBody.Part file);
    @Multipart
    @POST("/fast/upload_photo")
    Call<ResponseBody> upload_Photo(@Part("trn_photo") RequestBody trnPhoto
            , @Part MultipartBody.Part file);


}

package id.co.ppu.collectionfast2.rest;

import id.co.ppu.collectionfast2.rest.request.RequestArea;
import id.co.ppu.collectionfast2.rest.request.RequestLKPByDate;
import id.co.ppu.collectionfast2.rest.request.RequestLogin;
import id.co.ppu.collectionfast2.rest.request.RequestZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseArea;
import id.co.ppu.collectionfast2.rest.response.ResponseGetLKP;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMasterArea;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMasterData;
import id.co.ppu.collectionfast2.rest.response.ResponseGetUsers;
import id.co.ppu.collectionfast2.rest.response.ResponseGetZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseServerInfo;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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

    @POST("fast/server_info")
    Call<ResponseServerInfo> getServerInfo();

//    @Multipart
//    @POST("/api/Accounts/editaccount")
//    Call<User> uploadPhoto(@Part MultipartBody.Part filePart);


}

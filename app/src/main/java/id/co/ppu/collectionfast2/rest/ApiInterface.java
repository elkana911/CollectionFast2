package id.co.ppu.collectionfast2.rest;

import id.co.ppu.collectionfast2.rest.request.RequestLKP;
import id.co.ppu.collectionfast2.rest.request.RequestLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseGetLKP;
import id.co.ppu.collectionfast2.rest.response.ResponseGetUsers;
import id.co.ppu.collectionfast2.rest.response.ResponseLogin;
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

    @POST("fast/lkp")
    Call<ResponseGetLKP> getLKP(@Body RequestLKP request);

}

package id.co.ppu.collectionfast2.rest;

import id.co.ppu.collectionfast2.pojo.chat.TrnChatMsg;
import id.co.ppu.collectionfast2.rest.request.RequestArea;
import id.co.ppu.collectionfast2.rest.request.RequestLKPByDate;
import id.co.ppu.collectionfast2.rest.request.RequestLogError;
import id.co.ppu.collectionfast2.rest.request.RequestLogin;
import id.co.ppu.collectionfast2.rest.request.RequestRVB;
import id.co.ppu.collectionfast2.rest.request.RequestSyncLKP;
import id.co.ppu.collectionfast2.rest.request.RequestSyncLocation;
import id.co.ppu.collectionfast2.rest.request.RequestZipCode;
import id.co.ppu.collectionfast2.rest.request.chat.RequestChatContacts;
import id.co.ppu.collectionfast2.rest.request.chat.RequestChatMsg;
import id.co.ppu.collectionfast2.rest.request.chat.RequestChatMsgStatus;
import id.co.ppu.collectionfast2.rest.request.chat.RequestChatStatus;
import id.co.ppu.collectionfast2.rest.request.chat.RequestGetChatHistory;
import id.co.ppu.collectionfast2.rest.response.ResponseArea;
import id.co.ppu.collectionfast2.rest.response.ResponseAreaList;
import id.co.ppu.collectionfast2.rest.response.ResponseGetLKP;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMasterArea;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMasterData;
import id.co.ppu.collectionfast2.rest.response.ResponseGetMobileConfig;
import id.co.ppu.collectionfast2.rest.response.ResponseGetUsers;
import id.co.ppu.collectionfast2.rest.response.ResponseGetZipCode;
import id.co.ppu.collectionfast2.rest.response.ResponseLogin;
import id.co.ppu.collectionfast2.rest.response.ResponseRVB;
import id.co.ppu.collectionfast2.rest.response.ResponseServerInfo;
import id.co.ppu.collectionfast2.rest.response.ResponseSync;
import id.co.ppu.collectionfast2.rest.response.ResponseUserPwd;
import id.co.ppu.collectionfast2.rest.response.chat.ResponseGetChatHistory;
import id.co.ppu.collectionfast2.rest.response.chat.ResponseGetOnlineContacts;
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
public interface APInterface {

    @POST("fast/login")
    Call<ResponseLogin> login(@Body RequestLogin request);

    @POST("fast/lkp_date")
    Call<ResponseGetLKP> getLKPByDate(@Body RequestLKPByDate request);

    @POST("fast/sync_lkp")
    Call<ResponseSync> syncLKP(@Body RequestSyncLKP req);

    /*
    @POST("fast/lkp_date_ex")
    Call<ResponseGetLKP> getLKPByDateEx(@Body RequestLKPByDateEx request);
*/

    @POST("fast/users")
    Call<ResponseGetUsers> getUsers();

    @POST("fast/get_lkp_paid")
    Call<ResponseGetLKP> getLKPPaidByDate(@Body RequestLKPByDate request);

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

    @GET("fast/close_batch_yesterday")
    Call<ResponseBody> closeBatchYesterday(@Query("collector_code") String collectorCode);

    @Multipart
    @POST("fast/upload_photo")
    Call<ResponseBody> uploadPhoto(@Part("office_code") RequestBody officeCode
            , @Part("collector_id") RequestBody collectorId
            , @Part("ldv_no") RequestBody ldvNo
            , @Part("contract_no") RequestBody contractNo
            , @Part("photo_id") RequestBody photoId
            , @Part("latitude") RequestBody latitude
            , @Part("longitude") RequestBody longitude
            , @Part MultipartBody.Part file);

    // recommended
    @Multipart
    @POST("fast/upload_photo")
    Call<ResponseBody> upload_Photo(@Part("trn_photo") RequestBody trnPhoto
            , @Part MultipartBody.Part file);

    @Multipart
    @POST("fast/upload_poa")
    Call<ResponseBody> upload_PhotoOnArrival(@Part("poa_data") RequestBody trnPoA
            , @Part MultipartBody.Part file);

    @Multipart
    @POST("fast/upload_poa1")
    Call<ResponseBody> upload_PhotoOnArrival(@Part("poa_data") RequestBody poaData
                                            , @Part("poa_body") MultipartBody bodies);

    @POST("fast/sync_gps")
    Call<ResponseBody> syncLocation(@Body RequestSyncLocation req);

    @POST("fast/log_error")
    Call<ResponseBody> logError(@Body RequestLogError req);

    @POST("fast/get_any_lkp_user")
    Call<ResponseUserPwd> getAnyLKPUser();

    @GET("fast/get_app_version")
    Call<ResponseBody> getAppVersion(@Query("version") String version);

    @POST("fast/mobile_cfg")
    Call<ResponseGetMobileConfig> getMobileConfig();

    ///////////////////////////////  CHAT FUNCTIONS  ////////////////////////////////////////
    @POST("fastchat/send")
    Call<ResponseBody> sendMessage(@Body TrnChatMsg msg);

    @POST("fastchat/send_messages")
    Call<ResponseBody> sendMessages(@Body RequestChatMsg msg);

    @POST("fastchat/status")
    Call<ResponseBody> sendStatus(@Body RequestChatStatus status);

    @GET("fastchat/update_msg_status")
    Call<ResponseBody> updateMessageStatus(@Query("uid") String uid, @Query("status") String status);

    @GET("fastchat/get_msg")
    Call<ResponseGetChatHistory> getMessage(@Query("uid") String uid);

    @GET("fastchat/get_latest_msg")
    Call<ResponseGetChatHistory> getLatestMessage(@Query("user1") String user1, @Query("user2") String user2);

    /**
     * When user open app, the previous messages wont update, so this function will check the selected messages.
     * in return, a message will contain UID and messageStatus only. The remaining fields will be empty to save bandwidth
     * @param req
     * @return
     */
    @POST("fastchat/get_msg_status")
    Call<ResponseGetChatHistory> checkMessageStatus(@Body RequestChatMsgStatus req);

    @POST("fastchat/status_check")
    Call<ResponseBody> checkStatus(@Body RequestChatStatus req);

//    @POST("fastchat/online_contacts")
//    Call<ResponseGetOnlineContacts> getGroupContacts();

    @POST("fastchat/online_contacts_by")
    Call<ResponseGetOnlineContacts> getOnlineContacts(@Body RequestChatStatus req);

    @POST("fastchat/chat_contacts")
    Call<ResponseGetOnlineContacts> getChatContacts(@Body RequestChatContacts req);

    @POST("fastchat/group_contacts_by")
    Call<ResponseGetOnlineContacts> getGroupContacts(@Body RequestChatStatus req);

    @POST("fastchat/chat_hist")
    Call<ResponseGetChatHistory> getChatHistory(@Body RequestGetChatHistory req);

}

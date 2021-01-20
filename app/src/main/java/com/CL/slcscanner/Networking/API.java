package com.CL.slcscanner.Networking;

import com.CL.slcscanner.Pojo.AddOther.AddOtherMaster;
import com.CL.slcscanner.Pojo.CheckInternalSLCEdit;
import com.CL.slcscanner.Pojo.CheckUniqueCode.CheckUniqueCode;
import com.CL.slcscanner.Pojo.ClientAssets.ClientAssestMaster;
import com.CL.slcscanner.Pojo.ClientType.ClientType;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.Pojo.ListResponse.ListResponse;
import com.CL.slcscanner.Pojo.Login.LoginResponse;
import com.CL.slcscanner.Pojo.Note.NoteMaster;
import com.CL.slcscanner.Pojo.PoleDisplayData.PoleDisplayMaster;
import com.CL.slcscanner.Pojo.PoleMaster.PoleMaster;
import com.CL.slcscanner.Pojo.SettingMaster.SettingMaster;
import com.CL.slcscanner.Pojo.SettingMaster2.SettingMaster2;
import com.CL.slcscanner.Utils.AppConstants;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

import static com.CL.slcscanner.Utils.AppConstants.API_BACKGROUND;
import static com.CL.slcscanner.Utils.AppConstants.API_CHANGE_LANGUAGE;
import static com.CL.slcscanner.Utils.AppConstants.API_CHECK_INTERNAL_UNIQUE_MACA_DDRESS_API;
import static com.CL.slcscanner.Utils.AppConstants.API_CHECK_INTERNAL_UNIQUE_MACA_DDRESS_EDIT_API;
import static com.CL.slcscanner.Utils.AppConstants.API_CHECK_LOGIN;
import static com.CL.slcscanner.Utils.AppConstants.API_CHECK_MAC_ADDRESS_BEFORE_SLCID;
import static com.CL.slcscanner.Utils.AppConstants.API_CHECK_MAC_ID;
import static com.CL.slcscanner.Utils.AppConstants.API_CHECK_SLC_ID;
import static com.CL.slcscanner.Utils.AppConstants.API_CHECK_UNIQUE_CODE;
import static com.CL.slcscanner.Utils.AppConstants.API_EDIT_UPDATE_SLC_DATA;
import static com.CL.slcscanner.Utils.AppConstants.API_GET_ADDRESS;
import static com.CL.slcscanner.Utils.AppConstants.API_GET_ADD_OTHER_DATA;
import static com.CL.slcscanner.Utils.AppConstants.API_GET_ASSESTS;
import static com.CL.slcscanner.Utils.AppConstants.API_GET_LAT_LONG;
import static com.CL.slcscanner.Utils.AppConstants.API_GET_NOTE;
import static com.CL.slcscanner.Utils.AppConstants.API_POLE_ID_CHECK;
import static com.CL.slcscanner.Utils.AppConstants.API_SAVE_NEW_SLC_DATA;
import static com.CL.slcscanner.Utils.AppConstants.API_SETTINGS;
import static com.CL.slcscanner.Utils.AppConstants.API_SLC_DETAIL;
import static com.CL.slcscanner.Utils.AppConstants.API_SLC_LIST;

/**
 * Created by vrajesh on 1/19/2018.
 */

public interface API {

    @FormUrlEncoded
    @POST(API_CHECK_UNIQUE_CODE)
    Call<CheckUniqueCode> checkUniqueCode(
            @Field("code") String code,
            @Field("udid") String udid,
            @Field("source") String source,
            @Field("language") String language
    );

    @FormUrlEncoded
    @POST(API_CHECK_LOGIN)
    Call<LoginResponse> checkUserLogin(
            @Field("client_id") String clientid,
            @Field("userid") String userid,
            @Field("username") String username,
            @Field("password") String password,
            @Field("lat") Double lattitude,
            @Field("long") Double longgitude,
            @Field("source") String source,
            @Field("Version") String version
    );

    ///{node_type}
    @GET(API_SLC_LIST + "/{user_id}/{PageNo}/{lat}/{lng}")
    Call<ListResponse> getSLCID(@Path("user_id") String user_id, @Path("PageNo") String pg, @Path("lat") String lat, @Path("lng") String lng);

    @GET(API_SETTINGS + "/{unit_type}")
    Call<SettingMaster> getSettingList(@Path("unit_type") String unit_type);

    @GET(API_SETTINGS + "/{client_id}")
    Call<SettingMaster2> getSettings(@Path("client_id") String client_id);

    ///{node_type} ,@Path("node_type") String node_type
    @FormUrlEncoded
    @POST(API_SLC_LIST + "/{user_id}/{page_number}/{lat}/{lng}")
    Call<ListResponse> getFilterData(@Path("user_id") String user_id, @Field("search") String searchKey, @Path("page_number") String pg, @Path("lat") String lat, @Path("lng") String lng,
                                     @Field("node_type") String node_type);

    @GET(API_SLC_DETAIL + "/{id}/{unit_type}")
    Call<PoleDisplayMaster> getSLCDetails(@Path("id") String id, @Path("unit_type") String unit_type);

    @GET(API_SLC_DETAIL + "/{id}/{unit_type}")
    Call<ResponseBody> getSLCDetails2(@Path("id") String id, @Path("unit_type") String unit_type);

    @FormUrlEncoded
    @POST(API_CHECK_MAC_ID)
    Call<CommonResponse2> checkMACId(@Field("macaddress") String id, @Field("client_id") String client_id, @Field("user_id") String user_id,
                                     @Field("node_type") String node_type);
    @FormUrlEncoded
    @POST(API_CHECK_SLC_ID)
    Call<CommonResponse2> checkSLCId(@Field("slcid") String id, @Field("client_id") String client_id, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST(API_POLE_ID_CHECK)
    Call<CommonResponse> checkPoleId(@Field("pole_id") String pole_id, @Field("client_id") String client_id, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST(API_SAVE_NEW_SLC_DATA)
    Call<CommonResponse2> saveNewSLCData(
            @Field("client_id") String client_id,
            @Field("user_id") String user_id,
            @Field("mac_address") String mac_address,
            @Field("slc_id") String slc_id,
            @Field("pole_id") String pole_id,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("skip") String skip,
            @Field("measurement_units") String measurement_units,
            @Field("source") String source,
            @Field("address") String address,
            @Field("date_of_installation") String date_of_installation,
            @Field("Assets") String json,
            @Field("SLCID") String slcid
    );

    @FormUrlEncoded
    @POST(API_SAVE_NEW_SLC_DATA)
    Call<CommonResponse2> saveNewSLCData2(
            @Field("client_id") String client_id,
            @Field("user_id") String user_id,
            @Field("mac_address") String mac_address,
            @Field("slc_id") String slc_id,
            @Field("pole_id") String pole_id,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("skip") String skip,
            @Field("measurement_units") String measurement_units,
            @Field("source") String source,
            @Field("address") String address,
            @Field("date_of_installation") String date_of_installation,
            @Field("SLCID") String slcid,
            @Body String body
    );

    @Multipart
    @POST(API_SAVE_NEW_SLC_DATA)
    Call<CommonResponse2> saveNewSLCDataWithFile(
            @Part("client_id") RequestBody client_id,
            @Part("user_id") RequestBody user_id,
            @Part("mac_address") RequestBody mac_address,
            @Part("slc_id") RequestBody slc_id,
            @Part("pole_id") RequestBody pole_id,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("skip") RequestBody skip,
            @Part("measurement_units") RequestBody measurement_units,
            @Part("source") RequestBody source,
            @Part("address") RequestBody address,
            @Part("date_of_installation") RequestBody date_of_installation,
            @Part("Assets") RequestBody json,
            @Part("SLCID") RequestBody slcid,
            @Part("copyasset") RequestBody copyasset,
            @Part MultipartBody.Part pole_image,
            @Part("notes") RequestBody notes,
            @PartMap Map<String, RequestBody> note_params,
            @Part("node_type") RequestBody node_type
    );

    @Multipart
    @POST(API_SAVE_NEW_SLC_DATA)
    Call<CommonResponse2> saveNewSLCDataWithFile2(
            @Part("client_id") RequestBody client_id,
            @Part("user_id") RequestBody user_id,
            @Part("mac_address") RequestBody mac_address,
            @Part("slc_id") RequestBody slc_id,
            @Part("pole_id") RequestBody pole_id,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("skip") RequestBody skip,
            @Part("measurement_units") RequestBody measurement_units,
            @Part("source") RequestBody source,
            @Part("address") RequestBody address,
            @Part("date_of_installation") RequestBody date_of_installation,
            @Part("SLCID") RequestBody slcid,
            @Part("copyasset") RequestBody copyasset,
            @Part MultipartBody.Part pole_image,
            @Part("notes") RequestBody notes,
            @PartMap Map<String, RequestBody> params,
            @Part("Assets") RequestBody json,
            @Part("node_type") RequestBody node_type,
            @Part("Version") RequestBody version
    );

    @Multipart
    @POST(API_EDIT_UPDATE_SLC_DATA)
    Call<CommonResponse2> edtitUpdateSLCData(
            @Part("id") RequestBody id,
            @Part("user_id") RequestBody user_id,
            @Part("client_id") RequestBody client_id,
            @Part("slc_id") RequestBody slc_id,
            @Part("pole_id") RequestBody pole_id,
            @Part("mac_address") RequestBody mac_id,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("measurement_units") RequestBody measurement_units,
            @Part("address") RequestBody address,
            @Part("date_of_installation") RequestBody date_of_installation,
            @Part("Assets") RequestBody json,
            @Part("source") RequestBody source,
            @Part MultipartBody.Part pole_image
    );

    @Multipart
    @POST(API_EDIT_UPDATE_SLC_DATA)
    Call<CommonResponse2> edtitUpdateSLCData2(
            @Part("id") RequestBody id,
            @Part("user_id") RequestBody user_id,
            @Part("client_id") RequestBody client_id,
            @Part("slc_id") RequestBody slc_id,
            @Part("pole_id") RequestBody pole_id,
            @Part("mac_address") RequestBody mac_id,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("measurement_units") RequestBody measurement_units,
            @Part("address") RequestBody address,
            @Part("date_of_installation") RequestBody date_of_installation,
            @Part("source") RequestBody source,
            @Part MultipartBody.Part pole_image,
            @Part("notes") RequestBody notes,
            @PartMap Map<String, RequestBody> params,
            @Part("Assets") RequestBody json,
            @Part("node_type") RequestBody node_type,
            @Part("Version") RequestBody version
    );

    @FormUrlEncoded
    @POST(API_GET_ADD_OTHER_DATA)
    Call<AddOtherMaster> getAddOtherList(
            @Field("tag") String tag,
            @Field("name") String name,
            @Field("measurement_units") String measurement_unit,
            @Field("client_id") String client_id,
            @Field("user_id") String user_id
    );

    @GET(API_GET_ASSESTS + "/{client_id}/{messaurment_unit}/{copy_last_entry}")
    Call<ClientAssestMaster> getClientAssets(@Path("client_id") String client_id, @Path("messaurment_unit") String messaurment_unit, @Path("copy_last_entry") String copy_last_entry);

    @GET(API_GET_ASSESTS + "/{client_id}/{messaurment_unit}/{copy_last_entry}")
    Call<String> getClientAssets2(@Path("client_id") String client_id, @Path("messaurment_unit") String messaurment_unit, @Path("copy_last_entry") String copy_last_entry);

    @FormUrlEncoded
    @POST(API_GET_ADDRESS)
    Call<CommonResponse2> getAddress(@Field("lat") Double lat, @Field("lng") Double lng, @Field("client_id") String client_id, @Field("user_id") String user_id);

    @GET(API_BACKGROUND + "/{cid}/{uid}")
    Call<CommonResponse> startBgcall(@Path("cid") String cid, @Path("uid") String uid);

    @GET(API_CHANGE_LANGUAGE + "/{user_id}/{language}/{client_id}")
    Call<CommonResponse> changeLanguage(@Path("user_id") String userid, @Path("language") String language, @Path("client_id") String client_id);

    @FormUrlEncoded
    @POST(API_GET_LAT_LONG)
    Call<ListResponse> getLatLong(@Field("latitude") Double latitude,
                                  @Field("longitude") Double longitude,
                                  @Field("kilometer") Double km,
                                  @Field("clientid") String client_id,
                                  @Field("userid") String userid,
                                  @Field("top_lat") String top_lat,
                                  @Field("top_lon") String top_lon,
                                  @Field("bottom_lat") String bottom_lat,
                                  @Field("bottom_lon") String bottom_lon);

    @POST(API_GET_NOTE)
    Call<NoteMaster> getNotes();

    @FormUrlEncoded
    @POST(API_CHECK_MAC_ADDRESS_BEFORE_SLCID)
    Call<CommonResponse> checkMacAddressSlcBeforeSave(
            @Field("user_id") String user_id,
            @Field("client_id") String client_id,
            @Field("mac_address") String mac_address,
            @Field("slc_id") String slc_id,
            @Field("source") String source,
            @Field("node_type") String node_type
    );

    @FormUrlEncoded
    @POST(API_CHECK_INTERNAL_UNIQUE_MACA_DDRESS_API)
    Call<CommonResponse> checkInternalUniqueMacAddressAPI(
            @Field("user_id") String user_id,
            @Field("client_id") String client_id,
            @Field("mac_address") String mac_address,
            @Field("source") String source
    );

    @FormUrlEncoded
    @POST(API_CHECK_INTERNAL_UNIQUE_MACA_DDRESS_EDIT_API)
    Call<CommonResponse> checkInternalUniqueMacAddressEditAPI(
            @Field("user_id") String user_id,
            @Field("client_id") String client_id,
            @Field("mac_address") String mac_address,
            @Field("source") String source
    );

    @GET(AppConstants.API_GET_CLIENT_TYPE)
    @Headers("Content-Type:application/json")
    Call<ClientType> getClientType(
            @Header("Authorization") String authorization,
            @Header("IsSecured") String IsSecured
    );

    @POST(AppConstants.API_CHECK_INTERNAL_UNIQUE_SLCID_EDIT_API)
    @Headers("Content-Type:application/json")
    Call<CheckInternalSLCEdit> checkInternalUniqueSlcIdEditApi(
            @Field("client_id") String client_id,
            @Field("user_id") String user_id,
            @Field("source") String source,
            @Field("slc_id") String slc_id
    );

}
package com.aiton.pestscontrolandroid.service;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.model.Result;

import java.util.List;
import java.util.Map;

import cn.com.qiter.common.vo.PestsControlModel;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PestsService {
    /**
     * 获取节点 使用rxjava
     * @return
     */
    @GET(AppConstance.URL_PESTS_FINDALL)
    Observable<Result> findAll();
   // Observable<ServiceResult> getNewsWithRxJava(@Query("key") String key, @Query("type") String type);

    /**
     * {qrcode}/{lat}/{lon}/{appId}/{userId}/{stime}
     * @return
     */
   @GET(AppConstance.URL_PESTS_CHECKUPLOAD)
   Observable<Result> checkUpload(@Path("qrcode") String qrcode,@Path("lat") Double lat,@Path("lon") Double lon,@Path("userId") String userId,@Path("stime") String stime);
    // Observable<ServiceResult> getNewsWithRxJava(@Query("key") String key, @Query("type") String type);

    /**
     * 获取节点 不使用rxjava
     * @return
     */
    @GET(AppConstance.URL_PESTS_FINDALL_CALL)
    Call<ResponseBody> findAllCall();
    //Call<ResponseBody> getNewsWithoutRxJava(@Query("key") String key, @Query("type") String type);


    // @FormUrlEncoded
    @POST(AppConstance.URL_PESTS_SAVE)
    Observable<Result> savePests(@Body PestsControlModel domain);

    /**
     * 获取节点 使用rxjava
     * @return
     */
    @GET(AppConstance.URL_ALIVE)
    Observable<Result> aLive();
}

package com.aiton.pestscontrolandroid.service;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.model.Result;

import cn.com.qiter.common.vo.PestsTrapModel;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TrapService {
    /**
     * 获取节点 使用rxjava
     * @return
     */
    @GET(AppConstance.URL_TRAP_FINDALL)
    Observable<Result> findAll();
   // Observable<ServiceResult> getNewsWithRxJava(@Query("key") String key, @Query("type") String type);

    @GET(AppConstance.URL_TRAP_CHECKUPLOAD)
    Observable<Result> checkUpload(@Path("qrcode") String qrcode,@Path("lat") Double lat,@Path("lon") Double lon,@Path("userId") String userId,@Path("stime") String stime);
    // Observable<ServiceResult> getNewsWithRxJava(@Query("key") String key, @Query("type") String type);

    /**
     * 获取节点 使用rxjava
     * @return
     */
    @GET(AppConstance.URL_TRAP_GET_QRCODE)
    Observable<Result> getByQrcode(@Path("qrcode") String qrcode);
    // Observable<ServiceResult> getNewsWithRxJava(@Query("key") String key, @Query("type") String type);
    /**
     * 获取节点 不使用rxjava
     * @return
     */
    @GET(AppConstance.URL_TRAP_FINDALL_CALL)
    Call<ResponseBody> findAllCall();
    //Call<ResponseBody> getNewsWithoutRxJava(@Query("key") String key, @Query("type") String type);


    // @FormUrlEncoded
    @POST(AppConstance.URL_TRAP_SAVE)
    Observable<Result> save(@Body PestsTrapModel domain);

}

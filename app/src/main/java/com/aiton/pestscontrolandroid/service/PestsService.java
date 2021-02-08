package com.aiton.pestscontrolandroid.service;

import com.aiton.pestscontrolandroid.AppConstance;

import java.util.List;
import java.util.Map;

import cn.com.qiter.pests.PestsModel;
import cn.com.qiter.pests.Result;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PestsService {
    /**
     * 获取节点 使用rxjava
     * @return
     */
    @GET(AppConstance.URL_PESTS_FINDALL)
    Observable<Result> findAll();
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
    Observable<Result> savePests(@Body PestsModel domain);

}

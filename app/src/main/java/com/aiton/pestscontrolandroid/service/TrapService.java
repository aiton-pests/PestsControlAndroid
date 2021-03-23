package com.aiton.pestscontrolandroid.service;

import com.aiton.pestscontrolandroid.AppConstance;

import cn.com.qiter.common.Result;
import cn.com.qiter.pests.PestsModel;
import cn.com.qiter.pests.TrapModel;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TrapService {
    /**
     * 获取节点 使用rxjava
     * @return
     */
    @GET(AppConstance.URL_TRAP_FINDALL)
    Observable<Result> findAll();
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
    Observable<Result> save(@Body TrapModel domain);

}

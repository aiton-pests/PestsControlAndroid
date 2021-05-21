package com.aiton.pestscontrolandroid.service;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.model.LoggedInUser;
import com.aiton.pestscontrolandroid.data.model.Result;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    /**
     * 获取节点 使用rxjava
     * @return
     */
    @GET(AppConstance.URL_USER_LOGIN)
    Observable<Result> login(@Path("mobile") String mobile, @Path("password") String password);
    // Observable<ServiceResult> getNewsWithRxJava(@Query("key") String key, @Query("type") String type);
    /**
     * 获取节点 使用rxjava
     * @return
     */
    @GET(AppConstance.URL_USER_GET_MEMBER_INFO)
    Observable<Result> getMemberInfo(@Header("token") String token);
}

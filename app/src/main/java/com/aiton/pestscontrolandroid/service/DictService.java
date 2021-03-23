package com.aiton.pestscontrolandroid.service;

import com.aiton.pestscontrolandroid.AppConstance;

import cn.com.qiter.common.Result;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface DictService {
    /**
     * 获取节点 使用rxjava
     * @return
     */
    @GET(AppConstance.URL_DICT_NAME)
    Observable<Result> getDictByName(@Path("name") String name);

}

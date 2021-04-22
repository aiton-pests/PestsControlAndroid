package com.aiton.pestscontrolandroid.service;

import com.aiton.pestscontrolandroid.AppConstance;

import cn.com.qiter.common.Result;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QrcodeService {
    /**
     * 获取节点 使用rxjava
     * @return
     */
    @GET(AppConstance.URL_QRCODE_INT)
    Observable<Result> getQrcode(@Path("codeNumber") String codeNumber);

}

package com.aiton.pestscontrolandroid.service;

import com.aiton.pestscontrolandroid.AppConstance;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {
    private volatile static RetrofitUtil sInstance;
    private Retrofit mRetrofit;
    private PestsService pestsService;
    private UserService userService;
    private TrapService trapService;
    private DictService dictService;
    private RetrofitUtil() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(AppConstance.URL_APP)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        pestsService = mRetrofit.create(PestsService.class);
        userService = mRetrofit.create(UserService.class);
        trapService = mRetrofit.create(TrapService.class);
        dictService = mRetrofit.create(DictService.class);
    }

    public static RetrofitUtil getInstance() {
        if (sInstance == null) {
            synchronized (RetrofitUtil.class) {
                if (sInstance == null) {
                    sInstance = new RetrofitUtil();
                }
            }
        }
        return sInstance;
    }

    public DictService getDictService() {
        return dictService;
    }

    public UserService getUserService() {
        return userService;
    }

    public TrapService getTrapService() {
        return trapService;
    }

    public PestsService getPestsService() {
        return pestsService;
    }
}

package com.aiton.pestscontrolandroid;

import android.app.Application;
import android.content.Context;


public class BaseApplication extends Application {
    private static Context mInstance;
    private static String uniqueID;
    private final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //查询下载补丁包
//        initData();
    }
}

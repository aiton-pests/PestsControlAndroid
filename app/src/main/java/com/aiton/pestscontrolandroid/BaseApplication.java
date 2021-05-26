package com.aiton.pestscontrolandroid;

import android.app.Application;
import android.content.Context;

import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.jeremyliao.liveeventbus.LiveEventBus;

public class BaseApplication extends Application {
    private final String TAG = "BaseApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 初始化LiveEventBus
         * 1、supportBroadcast配置支持跨进程、跨APP通信
         * 2、配置LifecycleObserver（如Activity）接收消息的模式（默认值true）：
         * true：整个生命周期（从onCreate到onDestroy）都可以实时收到消息
         * false：激活状态（Started）可以实时收到消息，非激活状态（Stoped）无法实时收到消息，需等到Activity重新变成激活状
         * 态，方可收到消息
         * 3、autoClear
         * 配置在没有Observer关联的时候是否自动清除LiveEvent以释放内存（默认值false）
         * */

        LiveEventBus.config()
            .setContext(getApplicationContext());

    }
}

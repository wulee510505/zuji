package com.wulee.administrator.zuji;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

/**
 * Created by wulee on 2016/12/8 09:37
 */

public class App extends Application {

   public static Context context;
    public static ACache aCache;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        aCache = ACache.get(this);

        SDKInitializer.initialize(context);

        initBmobSDK();
        //BmobUpdateAgent.initAppVersion();

        Bmob.initialize(this,"ac67374a92fdca635c75eb6388e217a4");

    }

    private void initBmobSDK() {
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId("ac67374a92fdca635c75eb6388e217a4")  //设置appkey
                .setConnectTimeout(30)//请求超时时间（单位为秒）：默认15s
                .setUploadBlockSize(1024 * 1024)//文件分片上传时每片的大小（单位字节），默认512*1024
                .setFileExpiration(2500)//文件的过期时间(单位为秒)：默认1800s
                .build();
        Bmob.initialize(config);
    }
    
}

package com.wulee.administrator.zuji;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.stetho.Stetho;
import com.wulee.administrator.zuji.database.dao.DaoMaster;
import com.wulee.administrator.zuji.database.dao.DaoSession;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobInstallation;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;

/**
 * Created by wulee on 2016/12/8 09:37
 */

public class App extends Application {

   public static Context context;
    public static ACache aCache;

    public static DaoMaster master;
    public static DaoSession session;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        aCache = ACache.get(this);

        SDKInitializer.initialize(context);

        initDB();

        initBmobSDK();

        Bmob.initialize(this,"ac67374a92fdca635c75eb6388e217a4");

        Stetho.initializeWithDefaults(this);

        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());
    }

    private static void initDB(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "zuji-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        master= new DaoMaster(db);
        session=master.newSession();
    }

    private void initBmobSDK() {
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId("ac67374a92fdca635c75eb6388e217a4")  //设置appkey
                .setConnectTimeout(30)//请求超时时间（单位为秒）：默认15s
                .setUploadBlockSize(1024 * 1024)//文件分片上传时每片的大小（单位字节），默认512*1024
                .setFileExpiration(2500)//文件的过期时间(单位为秒)：默认1800s
                .build();
        Bmob.initialize(config);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(this);
    }
    
}

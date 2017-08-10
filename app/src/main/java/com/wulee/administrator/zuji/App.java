package com.wulee.administrator.zuji;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDexApplication;

import com.baidu.mapapi.SDKInitializer;
import com.facebook.stetho.Stetho;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.wulee.administrator.zuji.database.dao.DaoMaster;
import com.wulee.administrator.zuji.database.dao.DaoSession;
import com.wulee.administrator.zuji.utils.CrashHandlerUtil;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.statistics.AppStat;
import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;

import static com.wulee.administrator.zuji.entity.Constant.BOMB_APP_ID;

/**
 * Created by wulee on 2016/12/8 09:37
 */

public class App extends MultiDexApplication {

   public static Context context;
    public static ACache aCache;

    public static DaoMaster master;
    public static DaoSession session;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        aCache = ACache.get(this);

        Config.DEBUG = true;

        SDKInitializer.initialize(context);

        initDB();

        initBmobSDK();

        Bmob.initialize(this,BOMB_APP_ID);

        Stetho.initializeWithDefaults(this);

        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());

        initUMShareSDK();

        //崩溃处理
        CrashHandlerUtil crashHandlerUtil = CrashHandlerUtil.getInstance();
        crashHandlerUtil.init(this);

    }


    private void initUMShareSDK() {
        UMShareAPI.get(this);
        PlatformConfig.setWeixin("wx1073aba850d8e7e9", "cab9b8657c10ed5c8c07ab78a4649935");
        PlatformConfig.setWeixin("1106189026", "fyeuctD7ZssYvDcw");
    }

    private static void initDB(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "zuji-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        master= new DaoMaster(db);
        session=master.newSession();
    }

    private void initBmobSDK() {
        BmobConfig config = new BmobConfig.Builder(this)
                .setApplicationId(BOMB_APP_ID)  //设置appkey
                .setConnectTimeout(30)//请求超时时间（单位为秒）：默认15s
                .setUploadBlockSize(1024 * 1024)//文件分片上传时每片的大小（单位字节），默认512*1024
                .setFileExpiration(2500)//文件的过期时间(单位为秒)：默认1800s
                .build();
        Bmob.initialize(config);
        AppStat.i(BOMB_APP_ID, "",true);//统计初始化
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(this);
    }
    
}

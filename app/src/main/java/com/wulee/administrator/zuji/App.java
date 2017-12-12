package com.wulee.administrator.zuji;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDexApplication;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.wulee.administrator.zuji.database.dao.DaoMaster;
import com.wulee.administrator.zuji.database.dao.DaoSession;
import com.wulee.administrator.zuji.service.UploadLocationService;
import com.wulee.administrator.zuji.utils.CrashHandlerUtil;
import com.wulee.administrator.zuji.utils.OtherUtil;
import com.wulee.administrator.zuji.utils.network.NetChangeObserver;
import com.wulee.administrator.zuji.utils.network.NetStateReceiver;
import com.wulee.administrator.zuji.utils.network.NetworkUtils;
import com.xdandroid.hellodaemon.DaemonEnv;

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

    public NetworkUtils.NetworkType mNetType;
    private NetStateReceiver netStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        context = getApplicationContext();
        aCache = ACache.get(this);

        SDKInitializer.initialize(context);

        initDB();

        initBmobSDK();

        Bmob.initialize(this,BOMB_APP_ID);

        Stetho.initializeWithDefaults(this);

        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());

        //崩溃处理
        CrashHandlerUtil crashHandlerUtil = CrashHandlerUtil.getInstance();
        crashHandlerUtil.init(this);

        DaemonEnv.initialize(this, UploadLocationService.class, DaemonEnv.DEFAULT_WAKE_UP_INTERVAL);
        UploadLocationService.sShouldStopService = false;
        DaemonEnv.startServiceMayBind(UploadLocationService.class);

        initNetChangeReceiver();
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

    /**
     * 应用全局的网络变化处理
     */
    private void initNetChangeReceiver() {
        //获取当前网络类型
        mNetType = NetworkUtils.getNetworkType(this);
        //定义网络状态的广播接受者
        netStateReceiver = NetStateReceiver.getReceiver();
        //给广播接受者注册一个观察者
        netStateReceiver.registerObserver(netChangeObserver);
        //注册网络变化广播
        NetworkUtils.registerNetStateReceiver(this, netStateReceiver);
    }

    private NetChangeObserver netChangeObserver = new NetChangeObserver() {
        @Override
        public void onConnect(NetworkUtils.NetworkType type) {
            App.this.onNetConnect(type);
        }
        @Override
        public void onDisConnect() {
            App.this.onNetDisConnect();
        }
    };

    protected void onNetDisConnect() {
        OtherUtil.showToastText("网络已断开,请检查网络设置");
        mNetType = NetworkUtils.NetworkType.NETWORK_NONE;
    }

    protected void onNetConnect(NetworkUtils.NetworkType type) {
        if (type == mNetType) return; //net not change
        switch (type) {
            case NETWORK_WIFI:
                OtherUtil.showToastText("已切换到 WIFI 网络");
                break;
            case NETWORK_MOBILE:
                OtherUtil.showToastText("已切换到 2G/3G/4G 网络");
                break;
        }
        mNetType = type;
    }

    //释放广播接受者(建议在 最后一个 Activity 退出前调用)
    public void destroyReceiver() {
        //移除里面的观察者
        netStateReceiver.removeObserver(netChangeObserver);
        //解注册广播接受者,
        try {
            NetworkUtils.unRegisterNetStateReceiver(this, netStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

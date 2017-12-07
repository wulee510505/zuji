package com.wulee.administrator.zuji.utils.network;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * 描述: 网络相关工具类
 * Created by mjd on 2016/12/19.
 * <p>
 * 需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}
 * 需添加权限 {@code <uses-permission android:name="android.permission.INTERNET"/>}
 * 需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>}
 */

public class NetworkUtils {

    public enum NetworkType {
        NETWORK_WIFI,
        NETWORK_MOBILE,
        NETWORK_NONE
    }

    /**
     * 打开网络设置界面
     */
    public static void openWirelessSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
    }

    /**
     * 判断网络是否可用
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    /**
     * 判断 wifi 是否可用
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 获取当前网络类型
     */
    public static NetworkType getNetworkType(Context context) {
        NetworkType netType = NetworkType.NETWORK_NONE;
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                netType = NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                netType = NetworkType.NETWORK_MOBILE;
            }
        }
        return netType;
    }


    /**
     * 注册网络状态广播
     */
    public static NetStateReceiver registerNetStateReceiver(Context context, NetStateReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NetStateReceiver.ANDROID_NET_CHANGE_ACTION);
        context.getApplicationContext().registerReceiver(receiver, filter);
        return receiver;
    }

    /**
     * 注销网络状态广播
     */
    public static void unRegisterNetStateReceiver(Context context, NetStateReceiver receiver) {
        try {
            context.getApplicationContext().unregisterReceiver(receiver);
        } finally {
            receiver.destroyInstance();
        }
    }


}

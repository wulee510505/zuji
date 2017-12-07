package com.wulee.administrator.zuji.utils.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;


/**
 * 描述:
 * Created by mjd on 2017/1/17.
 */

public class NetStateReceiver extends BroadcastReceiver {

    public final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private static NetStateReceiver receiver;

    private ArrayList<NetChangeObserver> observers = new ArrayList<>();

    public static NetStateReceiver getReceiver() {
        if (receiver == null) {
            receiver = new NetStateReceiver();
        }
        return receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)) {
            notifyObserver(context);
        }
    }

    private void notifyObserver(Context context) {
        for (int i = 0; i < observers.size(); i++) {
            NetChangeObserver observer = observers.get(i);
            if (observer != null) {
                boolean isConnected = NetworkUtils.isConnected(context);
                NetworkUtils.NetworkType currentType = NetworkUtils.getNetworkType(context);
                if (isConnected) {
                    // 2/3/4G <--> WIFI 之间的网络切换, 也会进入此回调
                    observer.onConnect(currentType);
                } else {
                    observer.onDisConnect();
                }
            }
        }

    }

    /**
     * 注册网络连接观察者,可以注册多个观察者
     */
    public void registerObserver(NetChangeObserver observer) {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        observers.add(observer);
    }

    /**
     * 注销网络连接观察者
     */
    public void removeObserver(NetChangeObserver observer) {
        if (observers != null) {
            observers.remove(observer);
        }
    }

    public void destroyInstance() {
        observers.clear();
        receiver = null;
    }
}

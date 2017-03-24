package com.wulee.administrator.zuji.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import com.wulee.administrator.zuji.AlarmReceiver;
import com.wulee.administrator.zuji.utils.LocationUtil;

/**
 * Created by wulee on 2017/3/24 09:55
 */

public class UploadLocationService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationUtil.getInstance().startGetLocation();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int alarmTime = 2 * 1000 * 60; // 定时2分钟
        long trigerAtTime = SystemClock.elapsedRealtime() + alarmTime;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigerAtTime, pi);
        }else{
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, trigerAtTime, pi);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}

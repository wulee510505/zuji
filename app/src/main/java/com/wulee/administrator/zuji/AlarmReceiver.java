package com.wulee.administrator.zuji;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wulee.administrator.zuji.service.UploadLocationService;

/**
 * Created by wulee on 2017/3/24 10:58
 */

public class AlarmReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, UploadLocationService.class);
        context.startService(i);
    }
}

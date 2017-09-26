package com.wulee.administrator.zuji;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.liangmayong.text2speech.Text2Speech;
import com.wulee.administrator.zuji.database.DBHandler;
import com.wulee.administrator.zuji.database.bean.PushMessage;
import com.wulee.administrator.zuji.ui.pushmsg.PushMsgListActivity;
import com.wulee.administrator.zuji.utils.GsonUtil;
import com.wulee.administrator.zuji.utils.OtherUtil;

import cn.bmob.push.PushConstants;
import de.greenrobot.event.EventBus;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by wulee on 2017/2/28 15:39
 */

public class PushMsgReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    public static  final String ACTION_HIDE_PUSH_MSG_NOTIFICATION = "action_hide_push_msg_notification";
    private static final int ID_PUSH_MSG = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        mNotificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        String action = intent.getAction();
        if(action.equals(PushConstants.ACTION_MESSAGE)){

            if(!OtherUtil.hasLogin())
                return;

            String jsonMessage = intent.getStringExtra("msg");

            Text2Speech.speech(context,"您有一条新的消息",false);
            PushMessage pushMessage = GsonUtil.parseJsonWithGson(jsonMessage, PushMessage.class);
            DBHandler.insertPushMessage(pushMessage);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("新消息")
                            .setContentText(pushMessage.getContent());
            Intent resultIntent = new Intent(context, PushMsgListActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    context, 0 , resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            Notification notification = mBuilder.build();

            mNotificationManager.notify(ID_PUSH_MSG, notification);

            EventBus.getDefault().post(pushMessage);
        }else if(TextUtils.equals(action,ACTION_HIDE_PUSH_MSG_NOTIFICATION)){
            mNotificationManager.cancel(ID_PUSH_MSG);
        }
    }
}

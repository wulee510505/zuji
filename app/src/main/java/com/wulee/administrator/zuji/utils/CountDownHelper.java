package com.wulee.administrator.zuji.utils;

import android.os.CountDownTimer;
import android.util.Log;

/**
 * Created by wulee on 2017/12/25 10:04
 */

public class CountDownHelper {
    // 倒计时
    private CountDownTimer countDownTimer;
    // 倒计时结束的回调接口
    private OnFinishListener listener;
    /**
     * @param max            需要进行倒计时的最大值,单位是秒
     * @param interval       倒计时的间隔，单位是秒
     */
    public CountDownHelper(int max, int interval) {
        // 由于CountDownTimer并不是准确计时，在onTick方法调用的时候，time会有1-10ms左右的误差，这会导致最后一秒不会调用onTick()
        // 因此，设置间隔的时候，默认减去了10ms，从而减去误差。
        // 经过以上的微调，最后一秒的显示时间会由于10ms延迟的积累，导致显示时间比1s长max*10ms的时间，其他时间的显示正常,总时间正常
        countDownTimer = new CountDownTimer(max * 1000, interval * 1000 - 10) {
            @Override
            public void onTick(long time) {
                Log.d("CountDownHelper", "time = " + (time) + " text = " + ((time + 15) / 1000));
            }
            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.finish();
                }
            }
        };
    }
    /**
     * 开始   倒计时
     */
    public void start() {
        countDownTimer.start();
    }
    /**
     * 设置倒计时结束的监听器
     * @param listener
     */
    public void setOnFinishListener(OnFinishListener listener) {
        this.listener = listener;
    }
    /**
     * 倒计时结束的回调接口
     */
    public interface OnFinishListener {
         void finish();
    }
}

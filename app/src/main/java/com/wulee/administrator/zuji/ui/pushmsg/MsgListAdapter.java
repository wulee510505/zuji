package com.wulee.administrator.zuji.ui.pushmsg;


import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liangmayong.text2speech.OnText2SpeechListener;
import com.liangmayong.text2speech.Text2Speech;
import com.nineoldandroids.animation.ValueAnimator;
import com.wulee.administrator.zuji.App;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.database.bean.PushMessage;
import com.wulee.administrator.zuji.utils.DateTimeUtils;

import java.util.ArrayList;


public class MsgListAdapter extends BaseQuickAdapter<PushMessage> {

    public MsgListAdapter(int layoutResId, ArrayList<PushMessage> dataList) {
        super(layoutResId, dataList);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final PushMessage msg) {

        baseViewHolder.setText(R.id.tv_title,msg.getContent());
        baseViewHolder.setText(R.id.tv_content, DateTimeUtils.getStringDateTime(msg.getTime()));

        final ImageView ivPlay = baseViewHolder.getView(R.id.iv_play_msg_content);
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ScaleAnimation animation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                animation.setDuration(500);
                animation.setRepeatCount(ValueAnimator.INFINITE);
                animation.setRepeatMode(ValueAnimator.INFINITE);
                animation.setInterpolator(new AccelerateInterpolator());

                Text2Speech.speech(App.context,msg.getContent(),true);
                Text2Speech.setOnText2SpeechListener(new OnText2SpeechListener() {
                    @Override
                    public void onCompletion() {
                        animation.cancel();
                    }
                    @Override
                    public void onPrepared() {
                        ivPlay.startAnimation(animation);
                    }
                    @Override
                    public void onError(Exception e, String s) {
                        animation.cancel();
                    }
                    @Override
                    public void onStart() {
                    }
                    @Override
                    public void onLoadProgress(int i, int i1) {
                    }
                    @Override
                    public void onPlayProgress(int i, int i1) {

                    }
                });
            }
        });
    }
}

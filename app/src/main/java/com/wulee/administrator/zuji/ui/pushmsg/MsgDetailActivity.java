package com.wulee.administrator.zuji.ui.pushmsg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.liangmayong.text2speech.OnText2SpeechListener;
import com.liangmayong.text2speech.Text2Speech;
import com.nineoldandroids.animation.ValueAnimator;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PushMessage;
import com.wulee.administrator.zuji.utils.DateTimeUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by wulee on 2017/2/28 21:15
 * 推送消息详情
 */

public class MsgDetailActivity extends BaseActivity {


    @InjectView(R.id.iv_back)
    ImageView ivBack;
    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.titlelayout)
    RelativeLayout titlelayout;
    @InjectView(R.id.tv_play_msg_content)
    TextView tvPlayMsgContent;
    @InjectView(R.id.tv_time)
    TextView tvTime;
    @InjectView(R.id.tv_content)
    TextView tvContent;
    @InjectView(R.id.iv_play_msg_content)
    ImageView ivPlayMsgContent;

    private PushMessage msgObj;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.push_msg_detail);
        ButterKnife.inject(this);

        initData();
    }


    private void initData() {
        title.setText("消息详情");

        Intent intent = getIntent();
        msgObj = (PushMessage) intent.getSerializableExtra("msg");
        if (null == msgObj)
            finish();
        tvContent.setText(msgObj.getContent());
        tvTime.setText(DateTimeUtils.getStringDateTime(msgObj.getTime()));
    }

    @OnClick({R.id.iv_back, R.id.rl_play_msg_content})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_play_msg_content:
                final ScaleAnimation animation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                animation.setDuration(500);
                animation.setRepeatCount(ValueAnimator.INFINITE);
                animation.setRepeatMode(ValueAnimator.INFINITE);
                animation.setInterpolator(new AccelerateInterpolator());

                Text2Speech.speech(MsgDetailActivity.this, msgObj.getContent(), true);
                Text2Speech.setOnText2SpeechListener(new OnText2SpeechListener() {
                    @Override
                    public void onCompletion() {
                        animation.cancel();
                    }

                    @Override
                    public void onPrepared() {
                        ivPlayMsgContent.startAnimation(animation);
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
                break;
        }
    }
}

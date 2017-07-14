package com.wulee.administrator.zuji.ui;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.entity.Constant;
import com.wulee.administrator.zuji.utils.OtherUtil;
import com.wulee.administrator.zuji.widget.FadeInTextView;

import net.youmi.android.AdManager;

import java.util.List;


/**
 * Created by wulee on 2016/8/17.
 */

public class SplashActivity extends BaseActivity {

    private FadeInTextView mFadeInTextView;
    private View startView = null;
    private AlphaAnimation loadAlphaAnimation=null;
    private ScaleAnimation loadScaleAnimation = null;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startView = View.inflate(this, R.layout.splash, null);
        setContentView(startView);

        mFadeInTextView = (FadeInTextView) findViewById(R.id.fadeInTextView);

        initData();


        Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).build(), new AcpListener() {
            @Override
            public void onGranted() {
                AdManager.getInstance(SplashActivity.this).init(Constant.YOUMI_APP_ID, Constant.YOUMI_APPSECRET, true);
            }
            @Override
            public void onDenied(List<String> permissions) {
            }
        });
    }

    private void initData() {
        loadPage();
    }

    private void loadPage() {
        AnimationSet animationSet =new AnimationSet(true);

        loadAlphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        loadScaleAnimation =  new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        loadAlphaAnimation.setDuration(3000);
        loadScaleAnimation.setDuration(6000);
        animationSet.addAnimation(loadAlphaAnimation);
        //animationSet.addAnimation(loadScaleAnimation);
        startView.setAnimation(animationSet);
        animationSet.setInterpolator(new AccelerateInterpolator());
        animationSet.startNow();

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                mFadeInTextView
                        .setTextString("一款可以记录并查看出行轨迹的工具类软件")
                        .setTextAnimationListener(new FadeInTextView.TextAnimationListener() {
                            @Override
                            public void animationFinish() {
                                startActivity();
                            }
                        });
                mFadeInTextView.startFadeInAnimation();
            }
        });
    }

    private void startActivity() {
        Intent intent = null;
        if(OtherUtil.hasLogin()){
            intent = new Intent(this, MainActivity.class);
        } else{
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

}

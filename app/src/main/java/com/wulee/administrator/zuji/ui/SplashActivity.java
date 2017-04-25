package com.wulee.administrator.zuji.ui;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;

import static com.wulee.administrator.zuji.App.aCache;


/**
 * Created by wulee on 2016/8/17.
 */

public class SplashActivity extends BaseActivity {

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
        initData();
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
        animationSet.addAnimation(loadScaleAnimation);
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
                startActivity();
            }
        });
    }

    private void startActivity() {
        boolean isLogin = false;
        if(TextUtils.equals("yes",aCache.getAsString("has_login"))){
            isLogin = true;
        }else{
            isLogin = false;
        }
        Intent intent = null;
        if(isLogin){
            intent = new Intent(this, MainActivity.class);
        } else{
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

}

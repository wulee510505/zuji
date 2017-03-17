package com.wulee.administrator.zuji.ui;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;

import static com.wulee.administrator.zuji.App.aCache;


/**
 * Created by wulee on 2016/8/17.
 */

public class SplashActivity extends BaseActivity {

    private View startView = null;
    private AlphaAnimation loadAlphaAnimation=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startView = View.inflate(this, R.layout.splash, null);
        setContentView(startView);
        initData();
    }

    private void initData() {
        loadPage();
    }

    private void loadPage() {
        loadAlphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        loadAlphaAnimation.setDuration(1500);
        startView.setAnimation(loadAlphaAnimation);
        loadAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
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

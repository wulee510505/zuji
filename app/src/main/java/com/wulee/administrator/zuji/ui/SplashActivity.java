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
import android.widget.ImageView;
import android.widget.TextView;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.entity.Constant;
import com.wulee.administrator.zuji.entity.SplashPic;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.utils.OtherUtil;
import com.wulee.administrator.zuji.widget.FadeInTextView;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.wulee.administrator.zuji.App.aCache;


/**
 * Created by wulee on 2016/8/17.
 */

public class SplashActivity extends BaseActivity implements View.OnClickListener{

    private FadeInTextView mFadeInTextView;
    private View startView = null;
    private ImageView ivSplash;
    private AlphaAnimation loadAlphaAnimation=null;
    private ScaleAnimation loadScaleAnimation = null;
    private TextView btnSkip;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startView = View.inflate(this, R.layout.splash, null);
        setContentView(startView);

        ivSplash = (ImageView) findViewById(R.id.iv_splash_bg);
        btnSkip= (TextView) findViewById(R.id.tv_skip);
        btnSkip.setOnClickListener(this);
        mFadeInTextView = (FadeInTextView) findViewById(R.id.fadeInTextView);
        mFadeInTextView.setDuration(150);

        initData();

    }

    private void initData() {
        loadPage();

        String url = aCache.getAsString(Constant.KEY_SPLASH_PIC_URL);
        if(!TextUtils.isEmpty(url)){
            ImageUtil.setDefaultImageView(ivSplash,url,R.mipmap.bg_wellcome,SplashActivity.this);
        }else{
            BmobQuery<SplashPic>  query  = new BmobQuery<>();
            query.findObjects(new FindListener<SplashPic>() {
                @Override
                public void done(List<SplashPic> list, BmobException e) {
                    if(e == null){
                        if(list != null && list.size()>0){
                            SplashPic picInfo = list.get(0);
                            if(null != picInfo && !TextUtils.isEmpty(picInfo.getUrl())){
                                ImageUtil.setDefaultImageView(ivSplash,picInfo.getUrl(),R.mipmap.bg_wellcome,SplashActivity.this);
                                aCache.put(Constant.KEY_SPLASH_PIC_URL,picInfo.getUrl(),Constant.SPLASH_PIC_URL_SAVE_TIME);
                            }
                        }
                    }
                }
            });
        }
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
        final Intent intent;
        if(OtherUtil.hasLogin()){
             intent = new Intent(SplashActivity.this, MainNewActivity.class);
        } else{
             intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_skip:
                mFadeInTextView.stopFadeInAnimation();
                startActivity();
             break;
        }
    }
}

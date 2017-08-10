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
import android.widget.RelativeLayout;

import com.facebook.stetho.common.LogUtil;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.utils.OtherUtil;
import com.wulee.administrator.zuji.widget.FadeInTextView;

import net.youmi.android.AdManager;
import net.youmi.android.nm.cm.ErrorCode;
import net.youmi.android.nm.sp.SplashViewSettings;
import net.youmi.android.nm.sp.SpotListener;
import net.youmi.android.nm.sp.SpotManager;
import net.youmi.android.nm.sp.SpotRequestListener;

import java.util.List;


import static com.wulee.administrator.zuji.entity.Constant.YOUMI_APPSECRET;
import static com.wulee.administrator.zuji.entity.Constant.YOUMI_APP_ID;


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
                runApp();
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

    /**
     * 跑应用的逻辑
     */
    private void runApp() {
        //初始化SDK
        AdManager.getInstance(this).init(YOUMI_APP_ID,YOUMI_APPSECRET, true);
        preloadAd();
    }

    /**
     * 预加载广告
     */
    private void preloadAd() {
        // 注意：不必每次展示插播广告前都请求，只需在应用启动时请求一次
        SpotManager.getInstance(this).requestSpot(new SpotRequestListener() {
            @Override
            public void onRequestSuccess() {
                LogUtil.d("请求开屏广告成功");
                //	 应用安装后首次展示开屏会因为本地没有数据而跳过
                //   如果开发者需要在首次也能展示开屏，可以在请求广告成功之前展示应用的logo，请求成功后再加载开屏
                setupSplashAd();
            }

            @Override
            public void onRequestFailed(int errorCode) {
                LogUtil.d("请求开屏广告失败，errorCode: %s", errorCode);
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        toast("网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        LogUtil.d("暂无开屏广告");
                        break;
                    default:
                        LogUtil.d("请稍后再试");
                        break;
                }
            }
        });
    }

    /**
     * 设置开屏广告
     */
    private void setupSplashAd() {
        // 创建开屏容器
        final RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.rl_splash);
        // 对开屏进行设置
        SplashViewSettings splashViewSettings = new SplashViewSettings();
        //		// 设置是否展示失败自动跳转，默认自动跳转
        //		splashViewSettings.setAutoJumpToTargetWhenShowFailed(false);
        // 设置跳转的窗口类
        splashViewSettings.setTargetClass(MainActivity.class);
        // 设置开屏的容器
        splashViewSettings.setSplashViewContainer(splashLayout);

        // 展示开屏广告
        SpotManager.getInstance(this)
                .showSplash(this, splashViewSettings, new SpotListener() {
                    @Override
                    public void onShowSuccess() {
                        toast("开屏展示成功");
                    }
                    @Override
                    public void onShowFailed(int errorCode) {
                        toast("开屏展示失败");
                        switch (errorCode) {
                            case ErrorCode.NON_NETWORK:
                                LogUtil.d("网络异常");
                                break;
                            case ErrorCode.NON_AD:
                                LogUtil.d("暂无开屏广告");
                                break;
                            case ErrorCode.RESOURCE_NOT_READY:
                                LogUtil.d("开屏资源还没准备好");
                                break;
                            case ErrorCode.SHOW_INTERVAL_LIMITED:
                                LogUtil.d("开屏展示间隔限制");
                                break;
                            case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                                LogUtil.d("开屏控件处在不可见状态");
                                break;
                            default:
                                LogUtil.d("errorCode: %d", errorCode);
                                break;
                        }
                    }
                    @Override
                    public void onSpotClosed() {
                        LogUtil.d("开屏被关闭");
                    }
                    @Override
                    public void onSpotClicked(boolean isWebPage) {
                        toast("开屏被点击");
                        LogUtil.d("是否是网页广告？%s", isWebPage ? "是" : "不是");
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpotManager.getInstance(this).onDestroy();
    }
}

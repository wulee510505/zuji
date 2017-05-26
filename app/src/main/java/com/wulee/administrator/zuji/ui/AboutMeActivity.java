package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;



/**
 * Created by xian on 2017/2/17.
 */

public class AboutMeActivity extends BaseActivity {

    private TextView tvSoftWareSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_me);

        initView();
    }

    private void initView() {
        ((TextView) findViewById(R.id.title)).setText("关于");
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvSoftWareSite = (TextView) findViewById(R.id.tv_software_site);
        tvSoftWareSite.setText(getClickableSpan());
        //设置超链接可点击
        tvSoftWareSite.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * 获取可点击的SpannableString
     * @return
     */
    private SpannableString getClickableSpan() {
        SpannableString spannableString = new SpannableString("软件官网：http://zuji51.bmob.site/");
        //设置文字的单击事件
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Uri uri = Uri.parse("http://zuji51.bmob.site/");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        }, 5, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }



    /**
     * 分享功能
     */
    public synchronized void shareClick(View view) {
        UMImage image = new UMImage(AboutMeActivity.this, R.mipmap.ic_launcher);
        UMWeb web = new UMWeb("http://zuji51.bmob.site/");
        web.setTitle("足迹 \n一款可以记录并查看出行轨迹的工具类软件");//标题
        web.setThumb(image);  //缩略图
        web.setDescription("一款可以记录并查看出行轨迹的工具类软件");//描述

        new ShareAction(AboutMeActivity.this).withMedia(web)
                .setDisplayList(SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.WEIXIN_FAVORITE)
                .setCallback(umShareListener).open();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
        }
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            Toast.makeText(AboutMeActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(AboutMeActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(AboutMeActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}

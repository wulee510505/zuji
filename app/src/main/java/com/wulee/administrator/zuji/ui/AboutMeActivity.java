package com.wulee.administrator.zuji.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
}

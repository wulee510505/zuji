package com.wulee.administrator.zuji.ui;

import android.os.Bundle;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.utils.ImageUtil;
import com.wulee.administrator.zuji.widget.SuperImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wulee on 2017/8/25 13:50
 */

public class BigImageActivity extends BaseActivity {

    @InjectView(R.id.iv_bigimg)
    SuperImageView ivBigimg;

    public static final String IMAGE_URL = "Image_url";

    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.circle_big_image);
        ButterKnife.inject(this);

        imgUrl = getIntent().getStringExtra(IMAGE_URL);
        ImageUtil.setDefaultImageView(ivBigimg,imgUrl,R.mipmap.bg_pic_def_rect,this);
    }

}

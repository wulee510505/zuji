package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;

/**
 * Created by wulee on 2017/4/5 16:10
 */

public class SignActivity extends BaseActivity {

    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sign_activity);

        initView();
        addListener();
        initData();
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back);

    }

    private void initData() {


    }


    private void addListener(){
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignActivity.this.finish();
            }
        });

    }

}

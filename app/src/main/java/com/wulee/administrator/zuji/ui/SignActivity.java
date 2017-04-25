package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.coorchice.library.SuperTextView;
import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;
import com.wulee.administrator.zuji.database.bean.PersonInfo;
import com.wulee.administrator.zuji.entity.SignInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by wulee on 2017/4/5 16:10
 */

public class SignActivity extends BaseActivity implements View.OnClickListener{

    private ImageView ivBack;
    private SuperTextView buttonSign;

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
        buttonSign = (SuperTextView) findViewById(R.id.button_sign);
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
        buttonSign.setOnClickListener(this);
    }

    /*
     * 获取服务器时间
    */
    private void syncServerTime() {
        showProgressDialog(false);
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long time, BmobException e) {
                if(e == null){
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dateStr = formatter.format(new Date(time * 1000L));

                    sign(dateStr);
                }else{
                    toast("签到失败");
                }
            }
        });
    }

    /**
     * 签到
     */
    public void sign(String date) {
        PersonInfo user = BmobUser.getCurrentUser(PersonInfo.class);

        SignInfo signInfo = new SignInfo();
        signInfo.personInfo  = user;
        signInfo.hasSign = true;
        signInfo.date = date;
        signInfo.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                stopProgressDialog();
                if(e == null){
                    toast("签到成功");
                    SignActivity.this.finish();
                }else{
                    toast("签到失败");
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_sign:
                syncServerTime();
                break;
        }
    }
}

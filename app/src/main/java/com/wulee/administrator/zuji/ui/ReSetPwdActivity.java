package com.wulee.administrator.zuji.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wulee.administrator.zuji.R;
import com.wulee.administrator.zuji.base.BaseActivity;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wulee on 2017/1/18 09:57
 * 重置密码
 */

public class ReSetPwdActivity extends BaseActivity implements View.OnClickListener{

    private EditText mEtMobile;
    private EditText mEtPwd;
    private EditText mEtPincode;
    private Button  mBtnReSetPwd;
    private Button  mBtnPincode;

    private String mobile ,authCode,newPwd ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_pwd);

        initView();
        addListener();
    }

    private void addListener() {
        mBtnPincode.setOnClickListener(this);
        mBtnReSetPwd.setOnClickListener(this);
    }

    private void initView() {
        mEtMobile = (EditText) findViewById(R.id.et_mobile);
        mEtPwd = (EditText) findViewById(R.id.et_new_pwd);
        mEtPincode = (EditText) findViewById(R.id.et_pincode);
        mBtnPincode = (Button) findViewById(R.id.btn_pincode);
        mBtnReSetPwd = (Button) findViewById(R.id.btn_reset_pwd);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_reset_pwd:
                mobile  = mEtMobile.getText().toString().trim();
                newPwd = mEtPwd.getText().toString().trim();
                authCode = mEtPincode.getText().toString().trim();

                if(TextUtils.isEmpty(mobile)){
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(newPwd)){
                    Toast.makeText(this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
               if(TextUtils.isEmpty(authCode)){
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                doReSetPwd(newPwd);
            break;
            case R.id.btn_pincode:
                mobile  = mEtMobile.getText().toString().trim();
                if(TextUtils.isEmpty(mobile)){
                    Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取短信验证码
                BmobSMS.requestSMSCode(mobile, "reset_pwd", new QueryListener<Integer>() {
                    @Override
                    public void done(Integer integer, BmobException e) {
                        if(e==null){
                            Toast.makeText(ReSetPwdActivity.this, "发送短信成功", Toast.LENGTH_SHORT).show();
                        }else{
                            toast("code ="+ e.getErrorCode()+"\nmsg = "+e.getLocalizedMessage());
                        }
                    }
                });
                break;
        }
    }


    private void doReSetPwd(String newPwd) {
        BmobUser.resetPasswordBySMSCode(authCode,newPwd, new UpdateListener() {
            @Override
            public void done(BmobException ex) {
                if(ex==null){
                    toast("密码重置成功");
                    ReSetPwdActivity.this.finish();
                }else{
                    toast("重置失败：code ="+ex.getErrorCode()+",msg = "+ex.getLocalizedMessage());
                }
            }
        });
    }
}
